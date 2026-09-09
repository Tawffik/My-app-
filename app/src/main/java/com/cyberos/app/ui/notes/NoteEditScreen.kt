package com.cyberos.app.ui.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.*
import com.cyberos.app.ui.lang.Lang

@Composable
fun NoteEditScreen(
    note: Note?,
    allNotes: List<Note>,
    onOpenNote: (Long) -> Unit,
    onBack: () -> Unit,
    onSave: (title: String, body: String, tags: List<String>, folder: String, pinned: Boolean) -> Unit,
    onAskAi: (String, String) -> Unit,
    onGenerateCards: (String, String) -> Unit
) {
    var title by remember(note?.id) { mutableStateOf(note?.title ?: "") }
    var body by remember(note?.id) { mutableStateOf(note?.body ?: "") }
    var tagsRaw by remember(note?.id) {
        mutableStateOf((note?.tags ?: emptyList()).joinToString(", "))
    }
    var folder by remember(note?.id) { mutableStateOf(note?.folder ?: "") }
    var pinned by remember(note?.id) { mutableStateOf(note?.pinned ?: false) }
    val linksInNote = remember(body) { WikiLinks.extractTargets(body) }
    val backlinks = remember(note?.id, title, allNotes) {
        WikiLinks.backlinksTo(title, allNotes, note?.id)
    }
    val inlineTags = remember(body) { WikiLinks.extractInlineTags(body) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Spacer(Modifier.width(8.dp))
            Text(
                Lang.t("Edit note", "تعديل نوت"),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = pinned,
                onClick = { pinned = !pinned },
                label = { Text(if (pinned) "📌" else "Pin") }
            )
            TextButton(
                onClick = {
                    onSave(title, body, parseTags(tagsRaw), folder, pinned)
                },
                enabled = title.isNotBlank() || body.isNotBlank()
            ) { Text(Lang.t("Save", "حفظ")) }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(Lang.t("Title (used by [[links]])", "العنوان (لـ [[روابط]])")) },
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = folder,
            onValueChange = { folder = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(Lang.t("Folder (e.g. Bug Bounty/IDOR)", "مجلد")) },
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = tagsRaw,
            onValueChange = { tagsRaw = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(Lang.t("Tags (comma) + #inline in body", "تاجات")) },
            singleLine = true
        )
        if (inlineTags.isNotEmpty()) {
            Text(
                "Detected: " + inlineTags.joinToString(" ") { "#$it" },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AssistChip(
                onClick = { body += if (body.isEmpty() || body.endsWith("\n")) "[[]]" else " [[]]" },
                label = { Text("[[link]]") }
            )
            AssistChip(
                onClick = { body += if (body.isEmpty() || body.endsWith("\n")) "## \n" else "\n## \n" },
                label = { Text("## H") }
            )
            AssistChip(
                onClick = { body += "\n- " },
                label = { Text("• list") }
            )
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            modifier = Modifier.fillMaxWidth().heightIn(min = 220.dp),
            label = { Text(Lang.t("Body (Markdown-friendly)", "المحتوى")) },
            placeholder = { Text("Write… use [[Other Note]] and #tags") }
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { onAskAi(title.ifBlank { "Note" }, body) },
                enabled = body.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) { Text("🤖 AI") }
            OutlinedButton(
                onClick = { onGenerateCards(title.ifBlank { "Note" }, body) },
                enabled = body.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) { Text("🃏 Cards") }
            Button(
                onClick = { onSave(title, body, parseTags(tagsRaw), folder, pinned) },
                enabled = title.isNotBlank() || body.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) { Text(Lang.t("Save", "حفظ")) }
        }

        if (linksInNote.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        Lang.t("Outgoing links", "روابط خارجة"),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    linksInNote.forEach { target ->
                        val targetNote = WikiLinks.resolveTarget(target, allNotes)
                        if (targetNote != null) {
                            Text(
                                "→ $target",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenNote(targetNote.id) }
                                    .padding(vertical = 4.dp)
                            )
                        } else {
                            Text(
                                "→ $target (${Lang.t("missing — create note with this title", "مش موجود")})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
        if (backlinks.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        Lang.t("Backlinks", "روابط راجعة"),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    backlinks.forEach { b ->
                        Text(
                            "← ${b.title.ifBlank { "Untitled" }}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenNote(b.id) }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

private fun parseTags(raw: String): List<String> =
    raw.split(',', '،').map { it.trim().removePrefix("#") }.filter { it.isNotEmpty() }.distinct()

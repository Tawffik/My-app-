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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
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
    onGenerateCards: (String, String) -> Unit,
    onCreateFinding: ((String, String) -> Unit)? = null,
    onCreateLinkedNote: ((title: String) -> Unit)? = null,
    onDuplicate: (() -> Unit)? = null,
    unlinkedMentions: List<Note> = emptyList()
) {
    var title by remember(note?.id) { mutableStateOf(note?.title ?: "") }
    var body by remember(note?.id) { mutableStateOf(note?.body ?: "") }
    var tagsRaw by remember(note?.id) {
        mutableStateOf((note?.tags ?: emptyList()).joinToString(", "))
    }
    var folder by remember(note?.id) { mutableStateOf(note?.folder ?: "") }
    var pinned by remember(note?.id) { mutableStateOf(note?.pinned ?: false) }
    var showLinkPicker by remember { mutableStateOf(false) }
    var studyHide by remember { mutableStateOf(false) }
    val linksInNote = remember(body) { WikiLinks.extractTargets(body) }
    val backlinks = remember(note?.id, title, allNotes) {
        WikiLinks.backlinksTo(title, allNotes, note?.id)
    }
    val inlineTags = remember(body) { WikiLinks.extractInlineTags(body) }
    val clipboard = LocalClipboardManager.current
    val folderSuggestions = listOf(
        "Inbox", "Daily", "Bug Bounty", "Bug Bounty/IDOR", "Bug Bounty/XSS",
        "Web Fundamentals", "AI Security", "Labs", "MOC"
    )

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
                onClick = { onSave(title, body, parseTags(tagsRaw), folder, pinned) },
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
            label = { Text(Lang.t("Folder", "مجلد")) },
            singleLine = true
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            folderSuggestions.take(5).forEach { f ->
                AssistChip(onClick = { folder = f }, label = { Text(f, style = MaterialTheme.typography.labelSmall) })
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = tagsRaw,
            onValueChange = { tagsRaw = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(Lang.t("Tags (comma) + #inline", "تاجات")) },
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
        Text(Lang.t("Insert", "إدراج"), style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AssistChip(
                onClick = { body += if (body.isEmpty() || body.endsWith("\n")) "[[]]" else " [[]]" },
                label = { Text("[[link]]") }
            )
            AssistChip(onClick = { showLinkPicker = true }, label = { Text(Lang.t("Link note", "ربط نوت")) })
            AssistChip(
                onClick = { body += if (body.isEmpty() || body.endsWith("\n")) "## \n" else "\n## \n" },
                label = { Text("## H") }
            )
            AssistChip(onClick = { body += "\n- " }, label = { Text("• list") })
            AssistChip(
                onClick = {
                    body += "\n\n## Active recall\nQ: \nA: \n"
                },
                label = { Text("Q/A") }
            )
        }
        Spacer(Modifier.height(8.dp))
        if (studyHide) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(Lang.t("Study mode — answers hidden", "وضع مذاكرة"), style = MaterialTheme.typography.titleSmall)
                    Text(
                        body.lines().joinToString("\n") { line ->
                            if (line.trim().startsWith("A:", ignoreCase = true) ||
                                line.trim().startsWith("Answer", ignoreCase = true)
                            ) "A: ••••••" else line
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = { studyHide = false }) { Text(Lang.t("Reveal", "أظهر")) }
                }
            }
        } else {
            OutlinedTextField(
                value = body,
                onValueChange = { body = it },
                modifier = Modifier.fillMaxWidth().heightIn(min = 220.dp),
                label = { Text(Lang.t("Body (Markdown + [[wiki]])", "المحتوى")) },
                placeholder = { Text("[[Other Note]]  #tag  ## heading") }
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { studyHide = !studyHide },
                modifier = Modifier.weight(1f)
            ) { Text(if (studyHide) Lang.t("Edit", "تعديل") else Lang.t("Study", "مذاكرة")) }
            OutlinedButton(
                onClick = {
                    clipboard.setText(AnnotatedString("# $title\n\n$body"))
                },
                modifier = Modifier.weight(1f)
            ) { Text(Lang.t("Copy MD", "نسخ")) }
            if (onDuplicate != null && note != null) {
                OutlinedButton(onClick = onDuplicate, modifier = Modifier.weight(1f)) {
                    Text(Lang.t("Dup", "نسخ نوت"))
                }
            }
        }
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
            if (onCreateFinding != null) {
                OutlinedButton(
                    onClick = { onCreateFinding(title.ifBlank { "Note" }, body) },
                    enabled = title.isNotBlank() || body.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) { Text(Lang.t("Finding", "Finding")) }
            }
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
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "→ $target (${Lang.t("missing", "مش موجود")})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                if (onCreateLinkedNote != null) {
                                    TextButton(onClick = { onCreateLinkedNote(target) }) {
                                        Text(Lang.t("Create", "إنشاء"))
                                    }
                                }
                            }
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
        if (unlinkedMentions.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        Lang.t("Unlinked mentions", "إشارات بدون [[]]"),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    unlinkedMentions.take(15).forEach { b ->
                        Text(
                            "· ${b.title.ifBlank { "Untitled" }}",
                            style = MaterialTheme.typography.bodyMedium,
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

    if (showLinkPicker) {
        AlertDialog(
            onDismissRequest = { showLinkPicker = false },
            title = { Text(Lang.t("Insert [[link]]", "إدراج رابط")) },
            text = {
                Column(Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState())) {
                    allNotes.sortedBy { it.title.lowercase() }.forEach { n ->
                        val t = n.title.ifBlank { "Untitled" }
                        Text(
                            t,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    body += if (body.isEmpty() || body.endsWith("\n")) "[[$t]]" else " [[$t]]"
                                    showLinkPicker = false
                                }
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLinkPicker = false }) { Text(Lang.t("Close", "إغلاق")) }
            }
        )
    }
}

private fun parseTags(raw: String): List<String> =
    raw.split(',', '،').map { it.trim().removePrefix("#") }.filter { it.isNotEmpty() }.distinct()

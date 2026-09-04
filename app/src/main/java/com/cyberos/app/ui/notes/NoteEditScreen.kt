package com.cyberos.app.ui.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    note: Note?, allNotes: List<Note>, onOpenNote: (Long) -> Unit,
    onBack: () -> Unit, onSave: (String, String, List<String>) -> Unit,
    onAskAi: (String, String) -> Unit, onGenerateCards: (String, String) -> Unit
) {
    var title by remember(note?.id) { mutableStateOf(note?.title ?: "") }
    var body by remember(note?.id) { mutableStateOf(note?.body ?: "") }
    var tagsRaw by remember(note?.id) { mutableStateOf((note?.tags ?: emptyList()).joinToString(", ")) }
    val linksInNote = remember(body) { WikiLinks.extractTargets(body) }
    val backlinks = remember(note?.id, title, allNotes) { WikiLinks.backlinksTo(title, allNotes, note?.id) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.weight(1f), placeholder = { Text(Lang.t("Title", "عنوان")) }, singleLine = true)
            Spacer(Modifier.width(8.dp))
            Button(onClick = { onSave(title.trim(), body, parseTags(tagsRaw)) }, enabled = title.isNotBlank() || body.isNotBlank()) { Text(Lang.t("Save", "حفظ")) }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = tagsRaw, onValueChange = { tagsRaw = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text(Lang.t("Tags", "وسوم")) }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = body, onValueChange = { body = it }, modifier = Modifier.fillMaxWidth().weight(1f), placeholder = { Text("... [[Title]] ...") })
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { onAskAi(title.ifBlank { "Note" }, body) }, enabled = body.isNotBlank(), modifier = Modifier.weight(1f)) { Text("🤖") }
            OutlinedButton(onClick = { onGenerateCards(title.ifBlank { "Note" }, body) }, enabled = body.isNotBlank(), modifier = Modifier.weight(1f)) { Text("🃏") }
        }

        if (linksInNote.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(Lang.t("Links", "روابط"), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    linksInNote.forEach { target ->
                        val targetNote = WikiLinks.resolveTarget(target, allNotes)
                        if (targetNote != null) {
                            Text("→ $target", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth().clickable { onOpenNote(targetNote.id) }.padding(vertical = 4.dp))
                        } else {
                            Text("→ $target (${Lang.t("no note", "مفيش")})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }
        if (backlinks.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(Lang.t("Backlinks", "ملاحظات بتشاور هنا"), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    backlinks.forEach { b ->
                        Text("← ${b.title.ifBlank { "Untitled" }}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth().clickable { onOpenNote(b.id) }.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}

private fun parseTags(raw: String): List<String> =
    raw.split(',', '،').map { it.trim() }.filter { it.isNotEmpty() }.distinct()

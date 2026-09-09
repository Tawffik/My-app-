package com.cyberos.app.ui.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.*
import com.cyberos.app.ui.EmptyState
import com.cyberos.app.ui.lang.Lang
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotesScreen(
    state: NotesState,
    onOpen: (Long) -> Unit,
    onOpenTemplate: (String) -> Unit = {}
) {
    var deleteTarget by remember { mutableStateOf<Note?>(null) }
    val list = state.filtered()
    val folders = listOf("All") + state.folders()
    val tags = listOf("All") + state.allTags()

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(Lang.t("Notes", "النوتس"), style = MaterialTheme.typography.headlineSmall)
                Text(
                    Lang.t(
                        "Wiki links [[Note]] · #tags · folders · backlinks",
                        "روابط [[نوت]] · #تاج · مجلدات · backlinks"
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = { onOpen(state.openOrCreateDaily()) }) {
                Text(Lang.t("Daily", "يومي"))
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            NoteTemplates.ALL.forEach { tpl ->
                AssistChip(
                    onClick = { onOpenTemplate(tpl.id) },
                    label = { Text(tpl.title, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.query,
            onValueChange = { state.query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(Lang.t("Search title, body, tags, folder…", "بحث…")) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true
        )
        Spacer(Modifier.height(6.dp))
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = state.pinnedOnly,
                onClick = { state.pinnedOnly = !state.pinnedOnly },
                label = { Text(Lang.t("Pinned", "مثبّت")) }
            )
            folders.take(12).forEach { f ->
                FilterChip(
                    selected = state.folderFilter == f,
                    onClick = { state.folderFilter = f },
                    label = { Text(if (f == "All") "📁 All" else "📁 $f", style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
        if (tags.size > 1) {
            Spacer(Modifier.height(4.dp))
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tags.take(20).forEach { tg ->
                    FilterChip(
                        selected = state.tagFilter == tg,
                        onClick = { state.tagFilter = tg },
                        label = { Text(if (tg == "All") "# All" else "#$tg", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        if (list.isEmpty()) {
            EmptyState(
                Lang.t(
                    "No notes yet. Use a template or Daily. Link notes with [[Title]].",
                    "مفيش نوتس. استخدم قالب أو يومي. اربط بـ [[عنوان]]."
                )
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(list, key = { it.id }) { note ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.clickable { onOpen(note.id) }.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (note.pinned) {
                                    Text("📌 ", style = MaterialTheme.typography.titleSmall)
                                }
                                Text(
                                    note.title.ifBlank { Lang.t("Untitled", "بدون عنوان") },
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { state.togglePin(note.id) }) {
                                    Icon(
                                        if (note.pinned) Icons.Filled.Star else Icons.Filled.Star,
                                        contentDescription = "pin",
                                        tint = if (note.pinned) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { deleteTarget = note }) {
                                    Icon(Icons.Filled.Delete, contentDescription = null)
                                }
                            }
                            val folder = note.folder.ifBlank { "Inbox" }
                            Text(
                                folder,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                WikiLinks.stripForPreview(note.body),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (note.tags.isNotEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    note.tags.joinToString(" ") { "#$it" },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                            val fmt = remember {
                                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            }
                            Text(
                                fmt.format(Date(note.updatedAt)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    deleteTarget?.let { n ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(Lang.t("Delete note?", "حذف النوت؟")) },
            text = { Text(n.title.ifBlank { "Untitled" }) },
            confirmButton = {
                TextButton(onClick = { state.delete(n.id); deleteTarget = null }) {
                    Text(Lang.t("Delete", "حذف"))
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text(Lang.t("Cancel", "إلغاء"))
                }
            }
        )
    }
}

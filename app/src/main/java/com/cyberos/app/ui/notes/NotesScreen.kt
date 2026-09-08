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

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(8.dp))
        Text(Lang.t("Notes", "Notes"), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(4.dp))
        Text(
            Lang.t("Templates keep notes structured for learning and bounty work.", "Templates keep notes structured for learning and bounty work."),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
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
            value = state.query, onValueChange = { state.query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(Lang.t("Search...", "Search...")) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        val list = state.filtered()
        if (list.isEmpty()) {
            EmptyState(
                if (state.query.isBlank())
                    Lang.t("No notes yet — pick a template or tap +", "No notes yet — pick a template or tap +")
                else Lang.t("No results", "No results")
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(list, key = { it.id }) { note ->
                    NoteCard(note = note, onOpen = { onOpen(note.id) }, onDelete = { deleteTarget = note })
                }
            }
        }
    }

    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(Lang.t("Delete note?", "Delete note?")) },
            text = { Text(Lang.t("Cannot be undone.", "Cannot be undone.")) },
            confirmButton = {
                TextButton(onClick = {
                    deleteTarget?.let { state.delete(it.id) }
                    deleteTarget = null
                }) { Text(Lang.t("Delete", "Delete"), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text(Lang.t("Cancel", "Cancel")) }
            }
        )
    }
}

@Composable
private fun NoteCard(note: Note, onOpen: () -> Unit, onDelete: () -> Unit) {
    val fmt = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    Card(Modifier.fillMaxWidth().clickable(onClick = onOpen)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(note.title.ifBlank { "Untitled" }, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (note.tags.isNotEmpty()) {
                    Text(note.tags.joinToString(" · "), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
                Text(fmt.format(Date(note.updatedAt)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

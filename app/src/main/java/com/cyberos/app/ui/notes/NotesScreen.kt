package com.cyberos.app.ui.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun NotesScreen(state: NotesState, onOpen: (Long) -> Unit) {
    var deleteTarget by remember { mutableStateOf<Note?>(null) }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.query, onValueChange = { state.query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(Lang.t("Search...", "بحث...")) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        val list = state.filtered()
        if (list.isEmpty()) {
            EmptyState(if (state.query.isBlank()) Lang.t("No notes — tap +", "مفيش — اضغط +") else Lang.t("No results", "مفيش نتائج"))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(list, key = { it.id }) { note ->
                    NoteCard(note = note, onOpen = { onOpen(note.id) }, onDelete = { deleteTarget = note })
                }
            }
        }
    }

    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(Lang.t("Delete note?", "حذف؟")) },
            text = { Text(Lang.t("Cannot be undone.", "نهائيًا.")) },
            confirmButton = {
                TextButton(onClick = {
                    deleteTarget?.let { state.delete(it.id) }
                    deleteTarget = null
                }) { Text(Lang.t("Delete", "حذف"), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text(Lang.t("Cancel", "إلغاء")) }
            }
        )
    }
}

@Composable
private fun NoteCard(note: Note, onOpen: () -> Unit, onDelete: () -> Unit) {
    val dateText = remember(note.updatedAt) {
        SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(note.updatedAt))
    }
    val preview = remember(note.body) { WikiLinks.stripForPreview(note.body) }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.clickable(onClick = onOpen).padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(note.title.ifBlank { "Untitled" }, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
            }
            if (preview.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(preview, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                note.tags.take(4).forEach { t ->
                    Text("#$t", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.weight(1f))
                Text(dateText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

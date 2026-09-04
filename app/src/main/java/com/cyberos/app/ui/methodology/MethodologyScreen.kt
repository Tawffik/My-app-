package com.cyberos.app.ui.methodology

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
import androidx.compose.ui.unit.dp
import com.cyberos.app.learning.ProgressState
import com.cyberos.app.methodology.*
import com.cyberos.app.ui.EmptyState
import com.cyberos.app.ui.lang.Lang

@Composable
fun MethodologyListScreen(state: MethodologyState, onBack: () -> Unit, onOpen: (Long) -> Unit) {
    var deleteTarget by remember { mutableStateOf<Methodology?>(null) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Text(Lang.t("Methodologies", "المنهجيات"), style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onOpen(state.create(Lang.t("New", "جديد"))) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Add, contentDescription = null); Text(Lang.t("New methodology", "منهجية جديدة"))
        }
        Spacer(Modifier.height(12.dp))
        if (state.list.isEmpty()) { EmptyState(Lang.t("None.", "مفيش.")) }
        else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(state.list, key = { it.id }) { m ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.clickable { onOpen(m.id) }.padding(14.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(m.title, style = MaterialTheme.typography.titleMedium)
                                    Text("${m.steps.count { it.done }} / ${m.steps.size}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { deleteTarget = m }) { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                            }
                        }
                    }
                }
            }
        }
    }
    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(Lang.t("Delete?", "حذف؟")) },
            text = { Text("...") },
            confirmButton = {
                TextButton(onClick = {
                    deleteTarget?.let { state.delete(it.id) }
                    deleteTarget = null
                }) { Text(Lang.t("Delete", "حذف"), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text(Lang.t("Cancel", "إلغاء")) } }
        )
    }
}

@Composable
fun MethodologyEditScreen(state: MethodologyState, id: Long, progress: ProgressState, onBack: () -> Unit) {
    val m = state.get(id)
    if (m == null) {
        Column(Modifier.fillMaxSize()) {
            IconButton(onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            EmptyState("Not found")
        }
        return
    }
    var title by remember(m.id) { mutableStateOf(m.title) }
    var newStep by remember { mutableStateOf("") }
    var confirmDelete by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Text(Lang.t("Edit Methodology", "تعديل"), style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text(Lang.t("Title", "العنوان")) }, singleLine = true)
        Spacer(Modifier.height(8.dp))
        Button(onClick = { state.rename(id, title) }, modifier = Modifier.fillMaxWidth()) { Text(Lang.t("Save title", "حفظ")) }

        Spacer(Modifier.height(16.dp))
        Text("Steps (${m.steps.count { it.done }}/${m.steps.size})", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        if (m.steps.isEmpty()) {
            Text(Lang.t("No steps.", "مفيش."), color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(m.steps, key = { it.id }) { step ->
                    val index = m.steps.indexOf(step)
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(8.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = step.done, onCheckedChange = {
                                state.toggleStep(id, step.id)
                                if (!step.done) progress.addXp(1)
                            })
                            Text(step.objective, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            IconButton(onClick = { state.moveStep(id, index, index - 1) }, enabled = index > 0) { Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null) }
                            IconButton(onClick = { state.moveStep(id, index, index + 1) }, enabled = index < m.steps.size - 1) { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null) }
                            IconButton(onClick = { state.deleteStep(id, step.id) }) { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = newStep, onValueChange = { newStep = it }, modifier = Modifier.weight(1f), placeholder = { Text("...") }, singleLine = true)
            Button(onClick = { state.addStep(id, newStep); newStep = "" }, enabled = newStep.isNotBlank()) { Icon(Icons.Filled.Add, contentDescription = null) }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = { confirmDelete = true }, modifier = Modifier.fillMaxWidth()) {
            Text(Lang.t("Delete all", "حذف الكل"), color = MaterialTheme.colorScheme.error)
        }
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(Lang.t("Delete all?", "حذف الكل؟")) },
            text = { Text("...") },
            confirmButton = {
                TextButton(onClick = { state.delete(id); confirmDelete = false; onBack() }) {
                    Text(Lang.t("Delete", "حذف"), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text(Lang.t("Cancel", "إلغاء")) } }
        )
    }
}

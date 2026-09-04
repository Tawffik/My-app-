package com.cyberos.app.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.*
import com.cyberos.app.learning.ProgressState
import com.cyberos.app.ui.EmptyState
import com.cyberos.app.ui.lang.Lang

@Composable
fun ProjectDetailScreen(
    projectState: ProjectState, taskState: TaskState, progress: ProgressState,
    id: Long, onBack: () -> Unit, onOpenTask: (Long) -> Unit
) {
    val p = projectState.get(id)
    if (p == null) {
        Column(Modifier.fillMaxSize()) {
            OutlinedButton(onClick = onBack) { Text("←") }
            EmptyState(Lang.t("Not found", "غير موجود"))
        }
        return
    }
    var title by remember(p.id) { mutableStateOf(p.title) }
    var newTask by remember { mutableStateOf("") }
    var confirmDeleteProject by remember { mutableStateOf(false) }

    val projectTasks = taskState.tasks.filter { it.projectId == id }
    val doneCount = projectTasks.count { it.status == TaskFields.DONE }
    val frac = if (projectTasks.isEmpty()) 0f else doneCount.toFloat() / projectTasks.size

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onBack) { Text(p.emoji) }
            Spacer(Modifier.width(8.dp))
            Text("${p.emoji} ${p.title}", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(progress = { frac }, modifier = Modifier.fillMaxWidth())
        Text("$doneCount / ${projectTasks.size}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)

        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = newTask, onValueChange = { newTask = it }, modifier = Modifier.weight(1f), placeholder = { Text("...") }, singleLine = true)
            Button(onClick = {
                taskState.upsert(-1L, newTask, "", TaskFields.TODO, TaskFields.MEDIUM, id, null)
                newTask = ""
            }, enabled = newTask.isNotBlank()) { Icon(Icons.Filled.Add, contentDescription = null) }
        }
        Spacer(Modifier.height(10.dp))
        if (projectTasks.isEmpty()) {
            Text(Lang.t("No tasks.", "مفيش."), color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(projectTasks, key = { it.id }) { t ->
                    TaskCard(t, null, { onOpenTask(t.id) }, {
                        val wasDone = t.status == TaskFields.DONE
                        taskState.toggleDone(t.id)
                        if (!wasDone) progress.addXp(5)
                    }, { })
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text(Lang.t("Rename", "تسمية")) }, singleLine = true)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { projectState.rename(id, title) }, modifier = Modifier.weight(1f)) { Text(Lang.t("Save", "حفظ")) }
            OutlinedButton(onClick = { confirmDeleteProject = true }, modifier = Modifier.weight(1f)) {
                Text(Lang.t("Delete project", "حذف"), color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (confirmDeleteProject) {
        AlertDialog(
            onDismissRequest = { confirmDeleteProject = false },
            title = { Text(Lang.t("Delete project?", "حذف المشروع؟")) },
            text = { Text(Lang.t("Tasks survive (data safety).", "المهام بتفضل (سلامة بيانات).")) },
            confirmButton = {
                TextButton(onClick = {
                    taskState.detachFromProject(id)
                    projectState.delete(id)
                    confirmDeleteProject = false
                    onBack()
                }) { Text(Lang.t("Delete", "حذف"), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmDeleteProject = false }) { Text(Lang.t("Cancel", "إلغاء")) } }
        )
    }
}

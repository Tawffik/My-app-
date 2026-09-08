package com.cyberos.app.ui.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.*
import com.cyberos.app.learning.ProgressState
import com.cyberos.app.ui.EmptyState
import com.cyberos.app.ui.lang.Lang
import java.time.LocalDate

internal fun priorityEmoji(priority: String): String = when (priority) {
    TaskFields.HIGH -> "🔴"; TaskFields.LOW -> "🟢"; else -> "🟡"
}

internal fun dueLabel(dueDay: Long?): String? {
    if (dueDay == null) return null
    val today = LocalDate.now().toEpochDay()
    return when {
        dueDay < today -> "${today - dueDay}d ⚠"
        dueDay == today -> Lang.t("Today", "النهاردة")
        dueDay == today + 1 -> Lang.t("Tomorrow", "بكرة")
        else -> "${dueDay - today}d"
    }
}

internal fun isOverdue(task: Task): Boolean {
    val today = LocalDate.now().toEpochDay()
    return task.status != TaskFields.DONE && task.dueDay != null && task.dueDay < today
}

@Composable
internal fun TaskCard(task: Task, projectName: String?, onOpen: () -> Unit, onToggle: () -> Unit, onDelete: () -> Unit) {
    val done = task.status == TaskFields.DONE
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 6.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = done, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f).clickable(onClick = onOpen).padding(vertical = 6.dp)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium.copy(textDecoration = if (done) TextDecoration.LineThrough else null), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(priorityEmoji(task.priority), style = MaterialTheme.typography.labelSmall)
                    dueLabel(task.dueDay)?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = if (isOverdue(task)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary) }
                    projectName?.let { Text("📁 $it", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
fun TasksScreen(
    state: TaskState, projectState: ProjectState, progress: ProgressState,
    onOpenTask: (Long) -> Unit, onOpenProject: (Long) -> Unit
) {
    var deleteTarget by remember { mutableStateOf<Task?>(null) }
    var showNewProject by remember { mutableStateOf(false) }
    var newProjectTitle by remember { mutableStateOf("") }
    val nameMap = projectState.list.associate { it.id to it.title }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(8.dp))
        Text(Lang.t("Tasks", "المهام"), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("ALL" to Lang.t("All", "الكل"), "OPEN" to Lang.t("Open", "مفتوحة"),
                "TODAY" to Lang.t("Today", "النهاردة"), "OVERDUE" to Lang.t("Overdue", "متأخرة"),
                "DONE" to Lang.t("Done", "مكتملة")).forEach { (value, label) ->
                FilterChip(selected = state.filter == value, onClick = { state.filter = value }, label = { Text(label) })
            }
        }
        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(Lang.t("Projects", "المشاريع"), style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            TextButton(onClick = { showNewProject = true }) { Text("+") }
        }
        if (projectState.list.isEmpty()) {
            Text(Lang.t("No projects.", "مفيش."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 4.dp)) {
                items(projectState.list, key = { it.id }) { p ->
                    Card(Modifier.clickable { onOpenProject(p.id) }) {
                        Column(Modifier.padding(12.dp)) {
                            Text("${p.emoji} ${p.title}", style = MaterialTheme.typography.labelLarge, maxLines = 1)
                            val done = state.tasks.count { it.projectId == p.id && it.status == TaskFields.DONE }
                            val total = state.tasks.count { it.projectId == p.id }
                            Text("$done/$total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        val list = state.filtered()
        if (list.isEmpty()) {
            EmptyState(if (state.filter == "ALL") Lang.t("No tasks — tap +", "مفيش مهام.") else Lang.t("None.", "مفيش."))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                items(list, key = { it.id }) { t ->
                    TaskCard(
                        task = t, projectName = t.projectId?.let { nameMap[it] },
                        onOpen = { onOpenTask(t.id) },
                        onToggle = {
                            val wasDone = t.status == TaskFields.DONE
                            state.toggleDone(t.id)
                            if (!wasDone) progress.addXp(5)
                        },
                        onDelete = { deleteTarget = t }
                    )
                }
            }
        }
    }

    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(Lang.t("Delete task?", "حذف؟")) },
            text = { Text("...") },
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
    if (showNewProject) {
        AlertDialog(
            onDismissRequest = { showNewProject = false },
            title = { Text(Lang.t("New Project", "مشروع جديد")) },
            text = { OutlinedTextField(value = newProjectTitle, onValueChange = { newProjectTitle = it }, placeholder = { Text("...") }, singleLine = true) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = projectState.create(newProjectTitle)
                        newProjectTitle = ""
                        showNewProject = false
                        if (id > 0) onOpenProject(id)
                    },
                    enabled = newProjectTitle.isNotBlank()
                ) { Text(Lang.t("Create", "إنشاء")) }
            },
            dismissButton = {
                TextButton(onClick = { showNewProject = false }) { Text(Lang.t("Cancel", "إلغاء")) }
            }
        )
    }
}

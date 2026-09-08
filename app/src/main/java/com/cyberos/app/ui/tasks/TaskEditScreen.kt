package com.cyberos.app.ui.tasks

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.*
import com.cyberos.app.ui.lang.Lang
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    task: Task?, projects: List<Project>, onBack: () -> Unit,
    onSave: (String, String, String, String, Long?, Long?) -> Unit, onDelete: (Long) -> Unit
) {
    var title by remember(task?.id) { mutableStateOf(task?.title ?: "") }
    var notes by remember(task?.id) { mutableStateOf(task?.notes ?: "") }
    var status by remember(task?.id) { mutableStateOf(task?.status ?: TaskFields.TODO) }
    var priority by remember(task?.id) { mutableStateOf(task?.priority ?: TaskFields.MEDIUM) }
    var projectId by remember(task?.id) { mutableStateOf(task?.projectId) }
    var dueDay by remember(task?.id) { mutableStateOf(task?.dueDay) }
    var showPicker by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = (dueDay ?: LocalDate.now().toEpochDay()) * 86_400_000L)

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Spacer(Modifier.width(8.dp))
            Text(if (task == null) Lang.t("New Task", "مهمة جديدة") else Lang.t("Edit", "تعديل"), style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text(Lang.t("Title", "العنوان")) }, singleLine = true)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = notes, onValueChange = { notes = it }, modifier = Modifier.fillMaxWidth(), label = { Text(Lang.t("Notes", "ملاحظات")) }, maxLines = 3)

        Spacer(Modifier.height(14.dp))
        Text(Lang.t("Status", "الحالة"), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            listOf(TaskFields.TODO to Lang.t("To Do", "قائمة"), TaskFields.IN_PROGRESS to Lang.t("Progress", "جارية"), TaskFields.DONE to Lang.t("Done", "مكتملة")).forEach { (value, label) ->
                FilterChip(selected = status == value, onClick = { status = value }, label = { Text(label) })
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(Lang.t("Priority", "الأولوية"), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(TaskFields.HIGH to "🔴", TaskFields.MEDIUM to "🟡", TaskFields.LOW to "🟢").forEach { (value, emoji) ->
                FilterChip(selected = priority == value, onClick = { priority = value }, label = { Text(emoji) })
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(Lang.t("Project", "المشروع"), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            FilterChip(selected = projectId == null, onClick = { projectId = null }, label = { Text(Lang.t("None", "بدون")) })
            projects.forEach { p -> FilterChip(selected = projectId == p.id, onClick = { projectId = p.id }, label = { Text("${p.emoji} ${p.title}") }) }
        }
        Spacer(Modifier.height(10.dp))
        Text(Lang.t("Due", "الاستحقاق"), style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(dueLabel(dueDay) ?: Lang.t("No date", "بدون"), style = MaterialTheme.typography.bodyMedium, color = if (dueDay != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            Button(onClick = { showPicker = true }) { Text(Lang.t("Pick", "اختر")) }
            if (dueDay != null) OutlinedButton(onClick = { dueDay = null }) { Text("×") }
        }

        Spacer(Modifier.height(20.dp))
        Button(onClick = { onSave(title, notes, status, priority, projectId, dueDay) }, enabled = title.isNotBlank(), modifier = Modifier.fillMaxWidth()) { Text(Lang.t("Save", "حفظ")) }
        if (task != null) {
            Spacer(Modifier.height(10.dp))
            OutlinedButton(onClick = { confirmDelete = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Text(Lang.t("Delete", "حذف"), color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { ms -> dueDay = Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate().toEpochDay() }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text(Lang.t("Cancel", "إلغاء")) } }
        ) { DatePicker(state = pickerState) }
    }
    if (confirmDelete && task != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(Lang.t("Delete?", "حذف؟")) },
            text = { Text("...") },
            confirmButton = {
                TextButton(onClick = { onDelete(task.id); confirmDelete = false }) {
                    Text(Lang.t("Delete", "حذف"), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text(Lang.t("Cancel", "إلغاء")) } }
        )
    }
}

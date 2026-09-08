package com.cyberos.app.ui.bugbounty

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.BugBountyState
import com.cyberos.app.ui.lang.Lang

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramEditScreen(
    state: BugBountyState,
    programId: Long,
    onBack: () -> Unit
) {
    val existing = if (programId > 0) state.getProgram(programId) else null
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var platform by remember { mutableStateOf(existing?.platform ?: "HackerOne") }
    var url by remember { mutableStateOf(existing?.url ?: "") }
    var scope by remember { mutableStateOf(existing?.scopeSummary ?: "") }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }
    var active by remember { mutableStateOf(existing?.active ?: true) }
    var showDelete by remember { mutableStateOf(false) }
    var platformMenu by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Text(
                if (programId > 0) Lang.t("Edit Program", "Edit Program")
                else Lang.t("New Program", "New Program"),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            if (programId > 0) {
                IconButton(onClick = { showDelete = true }) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(Lang.t("Program name", "Program name")) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(expanded = platformMenu, onExpandedChange = { platformMenu = it }) {
            OutlinedTextField(
                value = platform,
                onValueChange = {},
                readOnly = true,
                label = { Text(Lang.t("Platform", "Platform")) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(platformMenu) }
            )
            ExposedDropdownMenu(expanded = platformMenu, onDismissRequest = { platformMenu = false }) {
                BugBountyState.PLATFORMS.forEach { p ->
                    DropdownMenuItem(text = { Text(p) }, onClick = { platform = p; platformMenu = false })
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("URL") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = scope, onValueChange = { scope = it }, label = { Text(Lang.t("Scope summary", "Scope summary")) }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text(Lang.t("Notes", "Notes")) }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(Lang.t("Active", "Active"), modifier = Modifier.weight(1f))
            Switch(checked = active, onCheckedChange = { active = it })
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (name.isBlank()) return@Button
                state.upsertProgram(programId, name, platform, url, scope, notes, active)
                onBack()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank()
        ) {
            Text(Lang.t("Save Program", "Save Program"))
        }
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text(Lang.t("Delete program?", "Delete program?")) },
            text = { Text(Lang.t("Findings stay; only the program is removed.", "Findings stay; only the program is removed.")) },
            confirmButton = {
                TextButton(onClick = {
                    state.deleteProgram(programId)
                    showDelete = false
                    onBack()
                }) { Text(Lang.t("Delete", "Delete")) }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text(Lang.t("Cancel", "Cancel")) }
            }
        )
    }
}

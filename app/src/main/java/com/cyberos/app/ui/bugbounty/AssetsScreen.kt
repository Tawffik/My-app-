package com.cyberos.app.ui.bugbounty

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.AssetTypes
import com.cyberos.app.data.BugBountyState
import com.cyberos.app.ui.EmptyState
import com.cyberos.app.ui.lang.Lang

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetsScreen(
    state: BugBountyState,
    programId: Long,
    onBack: () -> Unit
) {
    val program = state.getProgram(programId)
    var value by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Domain") }
    var typeMenu by remember { mutableStateOf(false) }
    val assets = state.assetsFor(programId)

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Column(Modifier.weight(1f)) {
                Text(Lang.t("Assets", "Assets"), style = MaterialTheme.typography.headlineSmall)
                if (program != null) {
                    Text(program.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text(Lang.t("Asset (domain / URL / IP)", "Asset (domain / URL / IP)")) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        ExposedDropdownMenuBox(expanded = typeMenu, onExpandedChange = { typeMenu = it }) {
            OutlinedTextField(
                value = type,
                onValueChange = {},
                readOnly = true,
                label = { Text("Type") },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeMenu) }
            )
            ExposedDropdownMenu(expanded = typeMenu, onDismissRequest = { typeMenu = false }) {
                AssetTypes.ALL.forEach { t ->
                    DropdownMenuItem(text = { Text(t) }, onClick = { type = t; typeMenu = false })
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (value.isBlank() || programId <= 0) return@Button
                state.upsertAsset(0L, programId, value, type, "", true)
                value = ""
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = value.isNotBlank() && programId > 0
        ) {
            Text(Lang.t("Add Asset", "Add Asset"))
        }
        Spacer(Modifier.height(16.dp))

        if (assets.isEmpty()) {
            EmptyState(Lang.t("No assets yet for this program.", "No assets yet for this program."))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(assets, key = { it.id }) { a ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(a.value, style = MaterialTheme.typography.titleSmall)
                                Text(a.type, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { state.deleteAsset(a.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

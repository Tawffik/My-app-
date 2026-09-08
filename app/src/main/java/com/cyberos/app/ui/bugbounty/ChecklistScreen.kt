package com.cyberos.app.ui.bugbounty

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.BugBountyChecklists
import com.cyberos.app.data.ChecklistProgressStore
import com.cyberos.app.ui.lang.Lang

@Composable
fun ChecklistScreen(
    progressStore: ChecklistProgressStore,
    onBack: () -> Unit
) {
    var selected by remember { mutableStateOf(BugBountyChecklists.TEMPLATES.first().id) }
    val template = BugBountyChecklists.TEMPLATES.first { it.id == selected }
    // recompose key
    var tick by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Text(Lang.t("Checklists", "Checklists"), style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            BugBountyChecklists.TEMPLATES.forEach { t ->
                FilterChip(
                    selected = selected == t.id,
                    onClick = { selected = t.id },
                    label = { Text(t.title.split(" ").first()) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        val done = progressStore.checkedCount(template.id, template.items)
        Text(
            "${template.title} ($done/${template.items.size})",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        key(tick, selected) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(template.items, key = { it.id }) { item ->
                    val checked = progressStore.isChecked(template.id, item.id)
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                progressStore.setChecked(template.id, item.id, it)
                                tick++
                            }
                        )
                        Text(item.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

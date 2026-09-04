package com.cyberos.app.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.*
import com.cyberos.app.methodology.Methodology
import com.cyberos.app.ui.lang.Lang

@Composable
fun SearchScreen(
    notes: List<Note>, tasks: List<Task>, projects: List<Project>, meths: List<Methodology>,
    onOpen: (String, String) -> Unit, onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val hits = GlobalSearch.search(query, notes, tasks, projects, meths)
    val kindEmoji = mapOf("topic" to "📚", "note" to "📝", "task" to "📌", "project" to "📁", "methodology" to "🗂")
    val kindLabel = mapOf(
        "topic" to Lang.t("Topics", "المواضيع"), "note" to Lang.t("Notes", "الملاحظات"),
        "task" to Lang.t("Tasks", "المهام"), "project" to Lang.t("Projects", "المشاريع"),
        "methodology" to Lang.t("Methods", "المنهجيات")
    )
    val kindOrder = listOf("topic", "note", "task", "project", "methodology")

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.weight(1f), placeholder = { Text(Lang.t("Search...", "ابحث...")) }, leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) }, singleLine = true)
        }
        Spacer(Modifier.height(8.dp))

        if (query.isBlank()) {
            Text(Lang.t("Type to search all data.", "اكتب للبحث."), color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else if (hits.isEmpty()) {
            Text(Lang.t("No results.", "مفيش نتائج."), color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                kindOrder.forEach { kind ->
                    val group = hits.filter { it.kind == kind }
                    if (group.isNotEmpty()) {
                        item(key = "h-$kind") {
                            Text(kindLabel[kind] ?: kind, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                        }
                        group.forEach { h ->
                            item(key = "${h.kind}-${h.ref}") {
                                Row(Modifier.fillMaxWidth().clickable { onOpen(h.kind, h.ref) }.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(kindEmoji[h.kind] ?: "•", style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(h.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

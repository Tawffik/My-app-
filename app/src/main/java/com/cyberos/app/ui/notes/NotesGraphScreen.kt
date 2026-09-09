package com.cyberos.app.ui.notes

import androidx.compose.foundation.clickable
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
import com.cyberos.app.data.Note
import com.cyberos.app.data.WikiLinks
import com.cyberos.app.ui.lang.Lang

/**
 * Lightweight Obsidian-style graph: nodes + edges as a navigable list
 * (mobile-friendly; no force-directed canvas required).
 */
@Composable
fun NotesGraphScreen(
    notes: List<Note>,
    onBack: () -> Unit,
    onOpen: (Long) -> Unit
) {
    val edges = remember(notes) { WikiLinks.graphEdges(notes) }
    val degree = remember(edges, notes) {
        val m = mutableMapOf<Long, Int>()
        notes.forEach { m[it.id] = 0 }
        edges.forEach { (a, b) ->
            m[a] = (m[a] ?: 0) + 1
            m[b] = (m[b] ?: 0) + 1
        }
        m
    }
    val ranked = remember(notes, degree) {
        notes.sortedByDescending { degree[it.id] ?: 0 }
    }
    val orphans = remember(notes, degree) {
        notes.filter { (degree[it.id] ?: 0) == 0 }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Column(Modifier.weight(1f)) {
                Text(Lang.t("Notes graph", "شبكة النوتس"), style = MaterialTheme.typography.titleLarge)
                Text(
                    "${notes.size} notes · ${edges.size} links · ${orphans.size} orphans",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            Lang.t(
                "Most connected notes first. Orphans have no [[links]] in or out.",
                "الأكثر ارتباطًا أولًا. Orphans بدون [[روابط]]."
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Text(Lang.t("Hubs", "محاور"), style = MaterialTheme.typography.titleMedium)
            }
            items(ranked.take(40), key = { it.id }) { n ->
                val d = degree[n.id] ?: 0
                Card(Modifier.fillMaxWidth().clickable { onOpen(n.id) }) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            n.title.ifBlank { "Untitled" },
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "$d links",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            if (orphans.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(Lang.t("Orphans", "بدون روابط"), style = MaterialTheme.typography.titleMedium)
                }
                items(orphans.take(30), key = { "o-${it.id}" }) { n ->
                    Card(Modifier.fillMaxWidth().clickable { onOpen(n.id) }) {
                        Text(
                            n.title.ifBlank { "Untitled" },
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
                Text(Lang.t("Edges", "الروابط"), style = MaterialTheme.typography.titleMedium)
            }
            items(edges.take(80), key = { "${it.first}-${it.second}" }) { (a, b) ->
                val from = notes.firstOrNull { it.id == a }
                val to = notes.firstOrNull { it.id == b }
                if (from != null && to != null) {
                    Text(
                        "${from.title} → ${to.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpen(from.id) }
                            .padding(vertical = 4.dp)
                    )
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

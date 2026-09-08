package com.cyberos.app.ui.research

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.BrowserLauncher
import com.cyberos.app.data.ResearchState
import com.cyberos.app.ui.EmptyState
import com.cyberos.app.ui.lang.Lang

@Composable
fun ResearchDetailScreen(state: ResearchState, id: Long, onBack: () -> Unit, onAddFinding: ((title: String, vuln: String, researchId: Long) -> Unit)? = null) {
    val item = state.items.firstOrNull { it.id == id }
    val context = LocalContext.current
    LaunchedEffect(id) { state.markRead(id) }

    if (item == null) {
        Column(Modifier.fillMaxSize()) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            EmptyState(Lang.t("Not found", "غير موجود"))
        }
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Spacer(Modifier.width(8.dp))
            Text(item.title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            IconButton(onClick = { state.toggleBookmark(item.id) }) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = if (item.bookmarked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = {}, enabled = false, label = { Text(item.category) })
            if (item.vulnerabilityType.isNotBlank()) {
                AssistChip(onClick = {}, enabled = false, label = { Text(item.vulnerabilityType) })
            }
        }
        if (item.author.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                item.author,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth()) {
            Text(
                item.summary.ifBlank { Lang.t("No summary available.", "مفيش ملخص متاح.") },
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (item.tags.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(Lang.t("Tags", "وسوم"), style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item.tags.forEach { tag ->
                    AssistChip(onClick = {}, enabled = false, label = { Text(tag) })
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        if (onAddFinding != null) {
            OutlinedButton(
                onClick = {
                    onAddFinding(
                        item.title,
                        item.vulnerabilityType.ifBlank { "Other" },
                        item.id
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(Lang.t("Add Finding", "Add Finding"))
            }
            Spacer(Modifier.height(8.dp))
        }
        if (item.link.isNotBlank()) {
            Button(
                onClick = { BrowserLauncher.open(context, item.link) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(Lang.t("Open in Browser", "افتح في المتصفح"))
            }
            Spacer(Modifier.height(6.dp))
            Text(
                item.link,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

package com.cyberos.app.ui.learning

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberos.app.learning.*
import com.cyberos.app.ui.lang.Lang

@Composable
fun LearningScreen(progress: ProgressState, onOpenTopic: (String) -> Unit, onOpenGraph: () -> Unit) {
    val total = CyberCurriculum.totalTopics()
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(Lang.t("Learning Paths", "المسارات"), style = MaterialTheme.typography.headlineSmall)
                    Text(Lang.t("${progress.completedCount}/$total", "تقدمك: ${progress.completedCount}/$total"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = onOpenGraph) { Text(Lang.t("Graph", "رسم")) }
            }
        }
        CyberCurriculum.paths.forEach { path ->
            item(key = "path-${path.id}") {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text(path.title, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(path.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        val done = path.topics.count { progress.isCompleted(it.id) }
                        LinearProgressIndicator(progress = { done.toFloat() / path.topics.size }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            items(count = path.topics.size, key = { i -> path.topics[i].id }) { i ->
                val topic = path.topics[i]
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.clickable { onOpenTopic(topic.id) }.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        if (progress.isCompleted(topic.id)) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        } else {
                            Text("${i + 1}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 6.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(topic.title, style = MaterialTheme.typography.titleMedium)
                            Text(topic.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                        }
                        Text("${topic.flashcards.size} 🃏", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

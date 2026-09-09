package com.cyberos.app.ui.learning

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberos.app.learning.*
import com.cyberos.app.ui.lang.Lang

@Composable
fun LearningScreen(
    progress: ProgressState,
    onOpenTopic: (String) -> Unit,
    onOpenGraph: () -> Unit,
    customStore: CustomPathStore? = null,
    onCustomChanged: () -> Unit = {}
) {
    var showAddPath by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }
    var pathFilter by remember { mutableStateOf("All") }
    var tick by remember { mutableStateOf(0) }

    val allPaths = remember(tick) { CyberCurriculum.allPaths() }
    val total = CyberCurriculum.totalTopics()

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(Lang.t("Learning Paths", "المسارات"), style = MaterialTheme.typography.headlineSmall)
                    Text(
                        Lang.t("${progress.completedCount}/$total topics", "تقدمك: ${progress.completedCount}/$total"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onOpenGraph) { Text(Lang.t("Graph", "رسم")) }
            }
            Text(
                Lang.t(
                    "Pick a path → study topic → open sources → notes → apply.",
                    "اختار مسار → ذاكر موضوع → افتح المصادر → نوتس → طبّق."
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            FilterChip(
                selected = pathFilter == "All",
                onClick = { pathFilter = "All" },
                label = { Text("All") }
            )
            allPaths.chunked(2).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach { p ->
                        FilterChip(
                            selected = pathFilter == p.title,
                            onClick = { pathFilter = p.title },
                            label = { Text(p.title, maxLines = 1) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(4.dp))
            OutlinedButton(
                onClick = { showAddPath = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = customStore != null
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(Lang.t("Add my section", "أضف قسم خاص"))
            }
        }

        val visible = if (pathFilter == "All") allPaths else allPaths.filter { it.title == pathFilter }
        visible.forEach { path ->
            item(key = "path-${path.id}") {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text(path.title, style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            path.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        val done = path.topics.count { progress.isCompleted(it.id) }
                        val denom = path.topics.size.coerceAtLeast(1)
                        LinearProgressIndicator(
                            progress = { done.toFloat() / denom },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "$done / ${path.topics.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            path.topics.forEachIndexed { i, topic ->
                item(key = topic.id) {
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier
                                .clickable { onOpenTopic(topic.id) }
                                .padding(14.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (progress.isCompleted(topic.id)) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Text(
                                    "${i + 1}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(topic.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    topic.summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                if (topic.flashcards.isNotEmpty()) {
                                    Text(
                                        "${topic.flashcards.size} 🃏",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (topic.resources.isNotEmpty()) {
                                    Text(
                                        "🔗 ${topic.resources.size}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddPath && customStore != null) {
        AlertDialog(
            onDismissRequest = { showAddPath = false },
            title = { Text(Lang.t("New learning section", "قسم تعلّم جديد")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text(Lang.t("Title", "العنوان")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text(Lang.t("Description", "الوصف")) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            customStore.addPath(newTitle, newDesc)
                            CyberCurriculum.dynamicPaths = customStore.all()
                            tick++
                            onCustomChanged()
                            newTitle = ""
                            newDesc = ""
                            showAddPath = false
                        }
                    }
                ) { Text(Lang.t("Save", "حفظ")) }
            },
            dismissButton = {
                TextButton(onClick = { showAddPath = false }) {
                    Text(Lang.t("Cancel", "إلغاء"))
                }
            }
        )
    }
}

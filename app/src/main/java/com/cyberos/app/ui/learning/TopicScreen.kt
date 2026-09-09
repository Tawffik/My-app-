package com.cyberos.app.ui.learning

import com.cyberos.app.data.BrowserLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cyberos.app.learning.*
import com.cyberos.app.ui.EmptyState
import com.cyberos.app.ui.lang.Lang

@Composable
fun TopicScreen(
    topicId: String,
    progress: ProgressState,
    onBack: () -> Unit,
    onAskAi: (String) -> Unit,
    onOpenTopic: (String) -> Unit,
    onOpenQuiz: (String) -> Unit,
    onCreateNote: ((title: String, body: String) -> Unit)? = null
) {
    val topic = remember(topicId) { CyberCurriculum.findTopic(topicId) }
    val ctx = LocalContext.current
    if (topic == null) {
        Column(Modifier.fillMaxSize()) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            EmptyState("Not found")
        }
        return
    }
    var done by remember(topicId) { mutableStateOf(progress.isCompleted(topicId)) }

    fun openUrl(url: String) {
        try {
        } catch (_: Exception) {
        }
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
            Text(topic.title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Text(
            topic.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))

        Text(Lang.t("Study actions", "إجراءات المذاكرة"), style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (done) {
                OutlinedButton(
                    onClick = { progress.uncompleteTopic(topicId); done = false },
                    modifier = Modifier.weight(1f)
                ) { Text(Lang.t("Undo", "إلغاء")) }
            } else {
                Button(
                    onClick = { progress.completeTopic(topicId); done = true },
                    modifier = Modifier.weight(1f)
                ) { Text(Lang.t("Complete (+XP)", "مكتمل")) }
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                onAskAi(
                    "Teach me the topic \"${topic.title}\" step by step for bug bounty study. " +
                        "Then ask me 2 check questions. Keep hypotheses labeled. Summary: ${topic.summary}"
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(Lang.t("Ask AI Tutor", "اسأل مدرّب AI")) }

        if (onCreateNote != null) {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    val body = buildString {
                        append("Topic: ${topic.title}\n")
                        append("Summary: ${topic.summary}\n\n")
                        append("Observation:\n\n")
                        append("Hypothesis:\n\n")
                        append("How I will test (authorized only):\n\n")
                        append("Takeaway:\n")
                    }
                    onCreateNote("${topic.title} — study note", body)
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(Lang.t("Create study note", "إنشاء نوت مذاكرة")) }
        }

        if (topic.quiz.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { onOpenQuiz(topicId) }, modifier = Modifier.fillMaxWidth()) {
                Text("🧠 ${topic.quiz.size} ${Lang.t("questions", "أسئلة")}")
            }
        }

        if (topic.resources.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(Lang.t("Open sources", "المصادر"), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            topic.resources.forEach { (label, url) ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .clickable { openUrl(url) }
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(label, style = MaterialTheme.typography.titleSmall)
                            Text(
                                url,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(Lang.t("Content", "المحتوى"), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        topic.sections.forEach { section ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        section.heading,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(section.body, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        if (topic.flashcards.isNotEmpty()) {
            Text(Lang.t("Flashcards in this topic", "بطاقات الموضوع"), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            topic.flashcards.forEach { (q, a) ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(q, style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(4.dp))
                        Text(a, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }

        if (topic.related.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(Lang.t("Related", "مواضيع مرتبطة"), style = MaterialTheme.typography.titleMedium)
            topic.related.forEach { rid ->
                val rt = CyberCurriculum.findTopic(rid)
                if (rt != null) {
                    TextButton(onClick = { onOpenTopic(rid) }) {
                        Text("→ ${rt.title}")
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

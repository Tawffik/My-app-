package com.cyberos.app.ui.learning

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberos.app.learning.*
import com.cyberos.app.ui.EmptyState
import com.cyberos.app.ui.lang.Lang

@Composable
fun TopicScreen(
    topicId: String, progress: ProgressState,
    onBack: () -> Unit, onAskAi: (String) -> Unit,
    onOpenTopic: (String) -> Unit, onOpenQuiz: (String) -> Unit
) {
    val topic = remember(topicId) { CyberCurriculum.findTopic(topicId) }
    if (topic == null) {
        Column(Modifier.fillMaxSize()) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            EmptyState("Not found")
        }
        return
    }
    var done by remember(topicId) { mutableStateOf(progress.isCompleted(topicId)) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Spacer(Modifier.width(8.dp))
            Text(topic.title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Text(topic.summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(12.dp))

        topic.sections.forEach { section ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(section.heading, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(6.dp))
                    Text(section.body, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (done) {
                OutlinedButton(onClick = { progress.uncompleteTopic(topicId); done = false }, modifier = Modifier.weight(1f)) {
                    Text(Lang.t("Undo", "إلغاء"))
                }
            } else {
                Button(onClick = { progress.completeTopic(topicId); done = true }, modifier = Modifier.weight(1f)) {
                    Text(Lang.t("Complete (+10 XP)", "مكتمل (+10 XP)"))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            onAskAi("اشرحلي الموضوع \"${topic.title}\" بالتفصيل وبعدين اسألني سؤالين.")
        }, modifier = Modifier.fillMaxWidth()) {
            Text(Lang.t("Ask AI", "اسأل الـ AI"))
        }
        if (topic.quiz.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { onOpenQuiz(topicId) }, modifier = Modifier.fillMaxWidth()) {
                Text("🧠 ${topic.quiz.size} ${Lang.t("questions", "أسئلة")}")
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

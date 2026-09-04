package com.cyberos.app.ui.home

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
import com.cyberos.app.challenges.Challenges
import com.cyberos.app.data.Task
import com.cyberos.app.flashcards.FlashcardStore
import com.cyberos.app.learning.*
import com.cyberos.app.ui.lang.Lang
import java.time.LocalTime

@Composable
fun HomeScreen(
    progress: ProgressState, cardStore: FlashcardStore, tasks: List<Task>,
    onOpenTopic: (String) -> Unit, onGoReview: () -> Unit, onGoNotes: () -> Unit,
    onOpenMethodologies: () -> Unit, onOpenSearch: () -> Unit,
    onOpenFocus: () -> Unit, onOpenSettings: () -> Unit,
    onOpenQuiz: () -> Unit, onOpenChallenge: () -> Unit
) {
    LaunchedEffect(Unit) { progress.touchDay() }
    val hour = remember { LocalTime.now().hour }
    val greeting = when {
        hour < 12 -> Lang.t("Good morning", "صباح الخير")
        hour < 18 -> Lang.t("Good afternoon", "نهارك سعيد")
        else -> Lang.t("Good evening", "مساء الخير")
    }
    val dueNow = cardStore.countDue(System.currentTimeMillis())
    val totalTopics = CyberCurriculum.totalTopics()
    val nextTopic = CyberCurriculum.firstIncompleteTopic { progress.isCompleted(it) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(greeting, style = MaterialTheme.typography.headlineMedium)
                Text("CyberOS v1.0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onOpenSettings) { Icon(Icons.Filled.Settings, contentDescription = null) }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth().clickable { onOpenSearch() }) {
            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(10.dp))
                Text(Lang.t("Global Search", "البحث الشامل"), style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatChip("🔥", "${progress.streak}", Lang.t("Streak", "ستريك"))
                    StatChip("⭐", "${progress.xp}", "XP")
                    StatChip("🃏", "$dueNow", Lang.t("Due", "مستحق"))
                    StatChip("📚", "${progress.completedCount}/$totalTopics", Lang.t("Topics", "مواضيع"))
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(Lang.t("Continue Learning", "كمّل تعلّم"), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                if (nextTopic == null) {
                    Text(Lang.t("All done!", "خلّصت!"), color = MaterialTheme.colorScheme.primary)
                } else {
                    Text(nextTopic.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Text(nextTopic.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { onOpenTopic(nextTopic.id) }) { Text(Lang.t("Open", "افتح")) }
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(Lang.t("Challenge Mode", "وضع التحدي"), style = MaterialTheme.typography.titleMedium)
                Text("6 scenarios · Solved: ${progress.challengesDoneCount}/6", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                Button(onClick = onOpenChallenge, modifier = Modifier.fillMaxWidth()) { Text(Lang.t("Enter", "ادخل")) }
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(Lang.t("Mixed Quiz", "اختبار متنوع"), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                Button(onClick = onOpenQuiz, modifier = Modifier.fillMaxWidth()) { Text(Lang.t("Start", "ابدأ")) }
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(Lang.t("Focus Session", "جلسة تركيز"), style = MaterialTheme.typography.titleMedium)
                Text("${progress.focusSessions} (${progress.focusMinutes} min)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                Button(onClick = onOpenFocus, modifier = Modifier.fillMaxWidth()) { Text(Lang.t("Start", "ابدأ")) }
            }
        }
        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onGoReview, modifier = Modifier.weight(1f)) { Text(Lang.t("Review", "مراجعة")) }
            OutlinedButton(onClick = onOpenMethodologies, modifier = Modifier.weight(1f)) { Text(Lang.t("Methods", "منهجيات")) }
            OutlinedButton(onClick = onGoNotes, modifier = Modifier.weight(1f)) { Text(Lang.t("Notes", "ملاحظات")) }
        }
    }
}

@Composable
private fun StatChip(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$emoji $value", style = MaterialTheme.typography.titleLarge)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

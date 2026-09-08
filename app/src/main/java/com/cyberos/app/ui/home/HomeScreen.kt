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
import com.cyberos.app.data.Task
import com.cyberos.app.flashcards.FlashcardStore
import com.cyberos.app.learning.*
import com.cyberos.app.ui.lang.Lang
import java.time.LocalTime

@Composable
fun HomeScreen(
    progress: ProgressState,
    cardStore: FlashcardStore,
    tasks: List<Task>,
    onOpenTopic: (String) -> Unit,
    onGoReview: () -> Unit,
    onGoNotes: () -> Unit,
    onOpenMethodologies: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenFocus: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenQuiz: () -> Unit,
    onOpenChallenge: () -> Unit,
    onOpenBugBounty: () -> Unit = {},
    onOpenBrief: () -> Unit = {},
    onOpenAiTutor: () -> Unit = {},
    onOpenResearch: () -> Unit = {},
    openFindingsCount: Int = 0,
    researchPulse: Int = 0
) {
    LaunchedEffect(Unit) { progress.touchDay() }
    val hour = remember { LocalTime.now().hour }
    val greeting = when {
        hour < 12 -> Lang.t("Good morning", "Good morning")
        hour < 18 -> Lang.t("Good afternoon", "Good afternoon")
        else -> Lang.t("Good evening", "Good evening")
    }
    val dueNow = cardStore.countDue(System.currentTimeMillis())
    val totalTopics = CyberCurriculum.totalTopics()
    val nextTopic = CyberCurriculum.firstIncompleteTopic { progress.isCompleted(it) }
    val aiNext = CyberCurriculum.findTopic("prompt-injection")?.takeIf { !progress.isCompleted(it.id) }
        ?: CyberCurriculum.findTopic("llm-basics")

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(greeting, style = MaterialTheme.typography.headlineMedium)
                Text(
                    "CyberOS · Learn · Research · Practice",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onOpenSearch) { Icon(Icons.Filled.Search, contentDescription = null) }
            IconButton(onClick = onOpenSettings) { Icon(Icons.Filled.Settings, contentDescription = null) }
        }
        Spacer(Modifier.height(12.dp))

        // Daily Brief hero
        Card(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenBrief),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(Lang.t("Daily Brief", "Daily Brief"), style = MaterialTheme.typography.titleLarge)
                Text(
                    Lang.t(
                        "Your focused path for today — learn, research, practice.",
                        "Your focused path for today — learn, research, practice."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(10.dp))
                Button(onClick = onOpenBrief) { Text(Lang.t("Open Brief", "Open Brief")) }
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatChip("🔥", "${progress.streak}", "Streak")
                    StatChip("⭐", "${progress.xp}", "XP")
                    StatChip("🃏", "$dueNow", "Due")
                    StatChip("📚", "${progress.completedCount}/$totalTopics", "Topics")
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(Lang.t("Continue Learning", "Continue Learning"), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                if (nextTopic == null) {
                    Text(Lang.t("All topics complete — review or start AI Security.", "All topics complete — review or start AI Security."))
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onGoReview) { Text("Review") }
                        OutlinedButton(onClick = {
                            aiNext?.let { onOpenTopic(it.id) }
                        }) { Text("AI Security") }
                    }
                } else {
                    Text(nextTopic.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Text(nextTopic.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { onOpenTopic(nextTopic.id) }) { Text(Lang.t("Open", "Open")) }
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(Modifier.weight(1f).clickable(onClick = onOpenResearch)) {
                Column(Modifier.padding(14.dp)) {
                    Text("Research", style = MaterialTheme.typography.titleSmall)
                    Text("$researchPulse new", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Card(Modifier.weight(1f).clickable(onClick = onOpenBugBounty)) {
                Column(Modifier.padding(14.dp)) {
                    Text("Bug Bounty", style = MaterialTheme.typography.titleSmall)
                    Text("$openFindingsCount open", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(Modifier.weight(1f).clickable(onClick = onOpenAiTutor)) {
                Column(Modifier.padding(14.dp)) {
                    Text("AI Tutor", style = MaterialTheme.typography.titleSmall)
                    Text("LLM attacks", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Card(Modifier.weight(1f).clickable(onClick = onGoNotes)) {
                Column(Modifier.padding(14.dp)) {
                    Text("Notes", style = MaterialTheme.typography.titleSmall)
                    Text("Templates", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(Lang.t("Practice", "Practice"), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Button(onClick = onGoReview, modifier = Modifier.fillMaxWidth()) {
                    Text(Lang.t("Review flashcards", "Review flashcards") + if (dueNow > 0) " ($dueNow)" else "")
                }
                Spacer(Modifier.height(6.dp))
                OutlinedButton(onClick = onOpenQuiz, modifier = Modifier.fillMaxWidth()) {
                    Text(Lang.t("Mixed Quiz", "Mixed Quiz"))
                }
                Spacer(Modifier.height(6.dp))
                OutlinedButton(onClick = onOpenChallenge, modifier = Modifier.fillMaxWidth()) {
                    Text(Lang.t("Challenge Mode", "Challenge Mode"))
                }
                Spacer(Modifier.height(6.dp))
                OutlinedButton(onClick = onOpenFocus, modifier = Modifier.fillMaxWidth()) {
                    Text(Lang.t("Focus Session", "Focus Session"))
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onOpenMethodologies, modifier = Modifier.fillMaxWidth()) {
            Text(Lang.t("Methodologies", "Methodologies"))
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StatChip(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$emoji $value", style = MaterialTheme.typography.titleLarge)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

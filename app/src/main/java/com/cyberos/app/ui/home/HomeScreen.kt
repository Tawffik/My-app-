package com.cyberos.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
    onGoTasks: () -> Unit = {},
    onOpenMethodologies: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenFocus: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenQuiz: () -> Unit,
    onOpenChallenge: () -> Unit,
    onOpenBugBounty: () -> Unit = {},
    onOpenBrief: () -> Unit = {},
    onStartToday: () -> Unit = {},
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
    val openTasks = tasks.count { it.status != "Done" && it.status != "done" }

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
                    "CyberOS · Learn → Research → Apply",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onOpenSearch) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        }
        Spacer(Modifier.height(12.dp))

        // Hero: Daily Brief
        Card(
            Modifier.fillMaxWidth().clickable(onClick = onOpenBrief),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(Lang.t("Daily Brief", "Daily Brief"), style = MaterialTheme.typography.titleLarge)
                Text(
                    Lang.t(
                        "Today’s focus: cards, topic, findings, writeups.",
                        "تركيز اليوم: كروت، موضوع، findings، رايت أبز."
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onStartToday) { Text(Lang.t("Start today", "ابدأ اليوم")) }
                    OutlinedButton(onClick = onOpenBrief) { Text(Lang.t("Brief", "Brief")) }
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        // Stats row
        Card(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatChip("🔥", "${progress.streak}", "Streak")
                StatChip("⭐", "${progress.xp}", "XP")
                StatChip("🃏", "$dueNow", "Due")
                StatChip("📚", "${progress.completedCount}/$totalTopics", "Topics")
            }
        }
        Spacer(Modifier.height(12.dp))

        // Primary destinations
        Text(Lang.t("Workspace", "Workspace"), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HubCard(
                title = Lang.t("Research", "Research"),
                subtitle = if (researchPulse > 0) "$researchPulse new" else "Writeups",
                onClick = onOpenResearch,
                modifier = Modifier.weight(1f)
            )
            HubCard(
                title = Lang.t("Bug Bounty", "Bug Bounty"),
                subtitle = if (openFindingsCount > 0) "$openFindingsCount open" else "Programs",
                onClick = onOpenBugBounty,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HubCard(
                title = "AI Tutor",
                subtitle = "Learn safely",
                onClick = onOpenAiTutor,
                modifier = Modifier.weight(1f)
            )
            HubCard(
                title = Lang.t("Notes", "Notes"),
                subtitle = "Wiki vault",
                onClick = onGoNotes,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(16.dp))

        // Continue learning
        Text(Lang.t("Continue", "Continue"), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                if (nextTopic != null) {
                    Text(nextTopic.title, style = MaterialTheme.typography.titleSmall)
                    Text(
                        nextTopic.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { onOpenTopic(nextTopic.id) }) {
                        Text(Lang.t("Open topic", "Open topic"))
                    }
                } else {
                    Text(
                        Lang.t("All current topics complete — pick a path in Learn.", "كل المواضيع مكتملة — اختار مسار من تعلّم."),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // Practice secondary
        Text(Lang.t("Practice", "Practice"), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(onClick = onGoReview, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        Lang.t("Review flashcards", "Review flashcards") +
                            if (dueNow > 0) " ($dueNow)" else ""
                    )
                }
                OutlinedButton(onClick = onGoTasks, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        Lang.t("Tasks", "Tasks") +
                            if (openTasks > 0) " ($openTasks)" else ""
                    )
                }
                OutlinedButton(onClick = onOpenQuiz, modifier = Modifier.fillMaxWidth()) {
                    Text(Lang.t("Mixed Quiz", "Mixed Quiz"))
                }
                OutlinedButton(onClick = onOpenChallenge, modifier = Modifier.fillMaxWidth()) {
                    Text(Lang.t("Challenge", "Challenge"))
                }
                OutlinedButton(onClick = onOpenFocus, modifier = Modifier.fillMaxWidth()) {
                    Text(Lang.t("Focus Session", "Focus Session"))
                }
                OutlinedButton(onClick = onOpenMethodologies, modifier = Modifier.fillMaxWidth()) {
                    Text(Lang.t("Methodologies", "Methodologies"))
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun HubCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier.clickable(onClick = onClick)) {
        Column(Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatChip(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("$emoji $value", style = MaterialTheme.typography.titleMedium)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

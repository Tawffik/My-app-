package com.cyberos.app.ui.brief

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.BugBountyFinding
import com.cyberos.app.data.ResearchItem
import com.cyberos.app.learning.TopicData
import com.cyberos.app.ui.lang.Lang

data class DailyBriefModel(
    val cardsDue: Int,
    val streak: Int,
    val xp: Long,
    val nextTopic: TopicData?,
    val openFindings: List<BugBountyFinding>,
    val recentWriteups: List<ResearchItem>,
    val aiSecurityItems: List<ResearchItem>
)

@Composable
fun DailyBriefScreen(
    model: DailyBriefModel,
    onBack: () -> Unit,
    onReview: () -> Unit,
    onOpenTopic: (String) -> Unit,
    onOpenBugBounty: () -> Unit,
    onOpenResearch: () -> Unit,
    onOpenAiTutor: () -> Unit,
    onOpenNotes: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Column(Modifier.weight(1f)) {
                Text(Lang.t("Daily Brief", "Daily Brief"), style = MaterialTheme.typography.headlineSmall)
                Text(
                    Lang.t("Learn · Research · Practice · Document", "Learn · Research · Practice · Document"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(Lang.t("Today’s Focus", "Today’s Focus"), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                FocusRow("1", Lang.t("Review flashcards", "Review flashcards"), "${model.cardsDue} due") {
                    onReview()
                }
                FocusRow(
                    "2",
                    Lang.t("Continue learning", "Continue learning"),
                    model.nextTopic?.title ?: Lang.t("All topics complete", "All topics complete")
                ) {
                    model.nextTopic?.let { onOpenTopic(it.id) }
                }
                FocusRow(
                    "3",
                    Lang.t("Findings needing attention", "Findings needing attention"),
                    "${model.openFindings.size} open"
                ) { onOpenBugBounty() }
                FocusRow(
                    "4",
                    Lang.t("AI Security session", "AI Security session"),
                    Lang.t("Tutor mode", "Tutor mode")
                ) { onOpenAiTutor() }
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(Lang.t("Progress", "Progress"), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Streak ${model.streak} · XP ${model.xp} · Cards due ${model.cardsDue}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        if (model.openFindings.isNotEmpty()) {
            SectionTitle(Lang.t("Open findings", "Open findings"))
            model.openFindings.take(5).forEach { f ->
                ListCard(f.title, "${f.status} · ${f.vulnerabilityType}") { onOpenBugBounty() }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (model.recentWriteups.isNotEmpty()) {
            SectionTitle(Lang.t("Recent writeups", "Recent writeups"))
            model.recentWriteups.take(5).forEach { w ->
                ListCard(w.title, w.vulnerabilityType.ifBlank { w.category }) { onOpenResearch() }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (model.aiSecurityItems.isNotEmpty()) {
            SectionTitle(Lang.t("AI Security pulse", "AI Security pulse"))
            model.aiSecurityItems.take(5).forEach { w ->
                ListCard(w.title, "AI Security") { onOpenResearch() }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = onOpenNotes, modifier = Modifier.fillMaxWidth()) {
            Text(Lang.t("Write a structured note", "Write a structured note"))
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onOpenBugBounty, modifier = Modifier.fillMaxWidth()) {
            Text(Lang.t("Open Bug Bounty Workspace", "Open Bug Bounty Workspace"))
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun FocusRow(num: String, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(onClick = onClick, label = { Text(num) })
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ListCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
            .clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, maxLines = 2)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

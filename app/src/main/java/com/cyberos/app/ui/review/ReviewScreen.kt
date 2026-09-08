package com.cyberos.app.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberos.app.flashcards.*
import com.cyberos.app.learning.*
import com.cyberos.app.ui.lang.Lang

@Composable
fun ReviewScreen(review: ReviewState, progress: ProgressState) {
    LaunchedEffect(Unit) { review.start() }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(Lang.t("Today's Review", "مراجعة اليوم"), style = MaterialTheme.typography.headlineSmall)
        Text("SM-2", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))

        when {
            review.queue.isEmpty() -> {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(Lang.t("No cards due", "مفيش كروت"), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            review.done -> {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(Lang.t("Session complete!", "خلصت!"), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Text("+${review.reviewedCount * 2} XP")
                        Button(onClick = { review.start() }) { Text(Lang.t("New session", "جلسة جديدة")) }
                    }
                }
            }
            else -> {
                val card = review.current()
                if (card != null) {
                    Text("Card ${review.index + 1}/${review.queue.size}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    card.topicId?.let { Text(CyberCurriculum.topicTitle(it), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) }
                    Spacer(Modifier.height(8.dp))
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(20.dp)) {
                            Text(card.question, style = MaterialTheme.typography.titleLarge)
                            if (review.revealed) {
                                Spacer(Modifier.height(12.dp))
                                Text(card.answer, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    if (!review.revealed) {
                        Button(onClick = { review.reveal() }, modifier = Modifier.fillMaxWidth()) { Text(Lang.t("Show answer", "اظهر")) }
                    } else {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { review.grade(SrsScheduler.AGAIN); progress.addXp(2) }, modifier = Modifier.weight(1f)) { Text(Lang.t("Again", "تاني"), color = MaterialTheme.colorScheme.error) }
                            OutlinedButton(onClick = { review.grade(SrsScheduler.HARD); progress.addXp(2) }, modifier = Modifier.weight(1f)) { Text(Lang.t("Hard", "صعب")) }
                            OutlinedButton(onClick = { review.grade(SrsScheduler.GOOD); progress.addXp(2) }, modifier = Modifier.weight(1f)) { Text(Lang.t("Good", "كويس")) }
                            Button(onClick = { review.grade(SrsScheduler.EASY); progress.addXp(2) }, modifier = Modifier.weight(1f)) { Text(Lang.t("Easy", "سهل")) }
                        }
                    }
                }
            }
        }
    }
}

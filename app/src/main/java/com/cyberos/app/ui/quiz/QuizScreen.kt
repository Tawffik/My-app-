package com.cyberos.app.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cyberos.app.learning.*
import com.cyberos.app.ui.EmptyState
import com.cyberos.app.ui.lang.Lang

@Composable
fun QuizScreen(state: QuizState, progress: ProgressState, onClose: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Spacer(Modifier.width(8.dp))
            Text(Lang.t("Quiz", "اختبار") + " — ${state.quizTitle}", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))

        if (state.questions.isEmpty()) {
            EmptyState(Lang.t("No questions.", "مفيش أسئلة."))
        } else if (state.finished) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(Lang.t("Complete!", "خلصت!"), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text("Score: ${state.correctCount}/${state.questions.size}", style = MaterialTheme.typography.headlineMedium)
                    Text("+${state.correctCount * 3} XP", color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onClose) { Text(Lang.t("Done", "تمام")) }
                }
            }
        } else {
            val current = state.current()
            if (current != null) {
                val (topicTitle, q) = current
                Text("Q${state.index + 1}/${state.questions.size} · ✔ ${state.correctCount}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(topicTitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Card(Modifier.fillMaxWidth()) { Text(q.question, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(18.dp)) }
                Spacer(Modifier.height(12.dp))

                q.options.forEachIndexed { i, opt ->
                    val isCorrect = state.revealed && i == q.correct
                    val isWrong = state.revealed && state.selected == i && i != q.correct
                    Button(
                        onClick = { state.select(i) },
                        enabled = !state.revealed,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when { isCorrect -> MaterialTheme.colorScheme.primary; isWrong -> MaterialTheme.colorScheme.error; else -> MaterialTheme.colorScheme.surfaceVariant },
                            contentColor = when { isCorrect -> MaterialTheme.colorScheme.onPrimary; isWrong -> MaterialTheme.colorScheme.onError; else -> MaterialTheme.colorScheme.onSurface }
                        )
                    ) { Text(opt, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start) }
                    Spacer(Modifier.height(6.dp))
                }

                if (state.revealed) {
                    Spacer(Modifier.height(6.dp))
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                if (state.selected == q.correct) "✔" else "✖",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (state.selected == q.correct) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(q.explanation, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    val isLast = state.index + 1 >= state.questions.size
                    Button(
                        onClick = { state.next(); if (isLast) progress.addXp((state.correctCount * 3).toLong()) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(if (isLast) Lang.t("Finish", "إنهاء") else Lang.t("Next", "التالي")) }
                }
            }
        }
    }
}

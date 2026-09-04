package com.cyberos.app.ui.challenge

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberos.app.challenges.*
import com.cyberos.app.data.*
import com.cyberos.app.learning.ProgressState
import com.cyberos.app.ui.lang.Lang
import kotlinx.coroutines.launch

@Composable
fun ChallengeScreen(
    vault: ApiKeyVault, settingsStore: AiSettingsStore, client: AiClient,
    progress: ProgressState, onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedId by remember { mutableStateOf(Challenges.all.first().id) }
    var vuln by remember { mutableStateOf("") }
    var rootCause by remember { mutableStateOf("") }
    var impact by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var verdict by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var revealed by remember { mutableStateOf(false) }
    var confirmReveal by remember { mutableStateOf(false) }

    val challenge = Challenges.byId(selectedId) ?: Challenges.all.first()
    val done = progress.isChallengeDone(challenge.id)

    fun resetFor(c: Challenge) {
        selectedId = c.id; vuln = ""; rootCause = ""; impact = ""
        verdict = null; feedback = null; error = null; revealed = false
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Text(Lang.t("Challenge Mode", "وضع التحدي"), style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Challenges.all.forEach { c ->
                val isDone = progress.isChallengeDone(c.id)
                FilterChip(selected = selectedId == c.id, onClick = { resetFor(c) }, label = { Text(if (isDone) "✓ ${c.title}" else c.title) })
            }
        }
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(challenge.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text("${challenge.difficulty}" + if (done) " ✓" else "", style = MaterialTheme.typography.labelMedium, color = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                Text(Lang.t("Scenario", "السيناريو"), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Text(challenge.scenario, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(10.dp))
                challenge.clues.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = vuln, onValueChange = { if (!busy) vuln = it }, modifier = Modifier.fillMaxWidth(), label = { Text(Lang.t("Vulnerability?", "الثغرة؟")) }, singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = rootCause, onValueChange = { if (!busy) rootCause = it }, modifier = Modifier.fillMaxWidth(), label = { Text(Lang.t("Root cause?", "السبب؟")) }, maxLines = 3)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = impact, onValueChange = { if (!busy) impact = it }, modifier = Modifier.fillMaxWidth(), label = { Text(Lang.t("Impact?", "الأثر؟")) }, maxLines = 3)
        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                error = null; verdict = null; feedback = null
                scope.launch {
                    val key = vault.loadApiKey()
                    if (key.isNullOrBlank()) { error = Lang.t("No key — use Reveal.", "مفيش مفتاح — استخدم الكشف."); return@launch }
                    busy = true
                    try {
                        val answers = "V: ${Redactor.redact(vuln).text}\nR: ${Redactor.redact(rootCause).text}\nI: ${Redactor.redact(impact).text}"
                        val reference = "REF:\nV: ${challenge.expectedVuln}\nR: ${challenge.expectedRootCause}\nI: ${challenge.expectedImpact}"
                        val prompt = reference + "\n\n" + RagSanitizer.contextBlock("learner", answers) + "\n\nGrade."
                        val s = settingsStore.load()
                        val r = client.chat(s.baseUrl, key, s.model, listOf(AiChatMessage("user", prompt)), Agents.CHALLENGE_EVALUATOR)
                        when (r) {
                            is AiResult.Success -> {
                                try {
                                    val v = JsonCodec.parseChallengeVerdict(r.reply)
                                    verdict = v.verdict; feedback = v.feedback
                                    if (!done) {
                                        when (v.verdict) {
                                            "correct" -> progress.completeChallenge(challenge.id, 12)
                                            "partial" -> progress.completeChallenge(challenge.id, 6)
                                        }
                                    }
                                } catch (e: Exception) { error = Lang.t("Invalid JSON.", "رد غير صالح.") }
                            }
                            is AiResult.Failure -> error = r.userMessage
                        }
                    } finally { busy = false }
                }
            },
            enabled = !busy && (vuln.isNotBlank() || rootCause.isNotBlank() || impact.isNotBlank()),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (busy) { CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp); Text("...") }
            else Text("🔬 ${Lang.t("Evaluate", "قيّم")}")
        }

        OutlinedButton(onClick = { confirmReveal = true }, enabled = !busy && !revealed, modifier = Modifier.fillMaxWidth()) {
            Text("💡 ${Lang.t("Reveal (+2)", "اكشف (+2)")}")
        }

        error?.let { Spacer(Modifier.height(6.dp)); Text(it, color = MaterialTheme.colorScheme.error) }

        verdict?.let { v ->
            Spacer(Modifier.height(10.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    val label = when (v) { "correct" -> "✔"; "partial" -> "◐"; else -> "✖" }
                    Text(label, style = MaterialTheme.typography.titleMedium, color = if (v == "incorrect") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                    feedback?.let { f -> Spacer(Modifier.height(6.dp)); Text(f) }
                }
            }
        }

        if (revealed) {
            Spacer(Modifier.height(10.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(Lang.t("Answer", "الإجابة"), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Text("V: ${challenge.expectedVuln}")
                    Text("R: ${challenge.expectedRootCause}", style = MaterialTheme.typography.bodySmall)
                    Text("I: ${challenge.expectedImpact}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("${progress.challengesDoneCount}/6", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }

    if (confirmReveal) {
        AlertDialog(
            onDismissRequest = { confirmReveal = false },
            title = { Text(Lang.t("Reveal answer?", "تكشف؟")) },
            text = { Text("...") },
            confirmButton = {
                TextButton(onClick = {
                    revealed = true; confirmReveal = false
                    if (!done) progress.completeChallenge(challenge.id, 2)
                }) { Text(Lang.t("Reveal", "اكشف"), color = MaterialTheme.colorScheme.primary) }
            },
            dismissButton = { TextButton(onClick = { confirmReveal = false }) { Text(Lang.t("Cancel", "إلغاء")) } }
        )
    }
}

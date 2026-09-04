package com.cyberos.app.ui.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cyberos.app.data.*
import com.cyberos.app.flashcards.FlashcardStore
import com.cyberos.app.learning.ProgressState
import com.cyberos.app.ui.lang.Lang
import kotlinx.coroutines.launch

@Composable
fun CardGenScreen(
    source: String, vault: ApiKeyVault, settingsStore: AiSettingsStore,
    client: AiClient, cardStore: FlashcardStore, progress: ProgressState, onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var draft by remember { mutableStateOf(source.take(2000)) }
    val cards = remember { mutableStateListOf<Pair<String, String>>() }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var notice by remember { mutableStateOf<String?>(null) }
    var added by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = null) }
            Spacer(Modifier.width(8.dp))
            Text(Lang.t("Flashcard Generator", "توليد كروت"), style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = draft, onValueChange = { draft = it }, modifier = Modifier.fillMaxWidth().height(140.dp), label = { Text(Lang.t("Source (secrets hidden)", "المصدر")) }, maxLines = 6)
        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                error = null; notice = null; added = false; cards.clear()
                scope.launch {
                    val key = vault.loadApiKey()
                    if (key.isNullOrBlank()) { error = Lang.t("No API key — Settings.", "مفيش مفتاح."); return@launch }
                    busy = true
                    try {
                        val redacted = Redactor.redact(draft)
                        if (redacted.found > 0) notice = "🔒 ${redacted.found} hidden"
                        val s = settingsStore.load()
                        val prompt = RagSanitizer.contextBlock("note", redacted.text) + "\n\nGenerate flashcards."
                        val r = client.chat(s.baseUrl, key, s.model, listOf(AiChatMessage("user", prompt)), Agents.CARD_GENERATOR)
                        when (r) {
                            is AiResult.Success -> {
                                try {
                                    cards.addAll(JsonCodec.parseGeneratedCards(r.reply))
                                    if (cards.isEmpty()) error = Lang.t("Empty cards.", "كروت فاضية.")
                                } catch (e: Exception) { error = Lang.t("Invalid JSON.", "رد غير صالح.") }
                            }
                            is AiResult.Failure -> error = r.userMessage
                        }
                    } finally { busy = false }
                }
            },
            enabled = !busy && draft.isNotBlank(), modifier = Modifier.fillMaxWidth()
        ) {
            if (busy) { CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp); Text("...") }
            else Text("🤖 ${Lang.t("Generate", "ولّد")}")
        }

        notice?.let { Spacer(Modifier.height(6.dp)); Text(it, color = MaterialTheme.colorScheme.primary) }
        error?.let { Spacer(Modifier.height(6.dp)); Text(it, color = MaterialTheme.colorScheme.error) }

        if (cards.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Text("${cards.size} cards", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(cards.size) { i ->
                    val c = cards[i]
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(10.dp)) {
                            OutlinedTextField(value = c.first, onValueChange = { cards[i] = it to c.second }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Q") }, singleLine = true)
                            Spacer(Modifier.height(6.dp))
                            OutlinedTextField(value = c.second, onValueChange = { cards[i] = c.first to it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("A") }, singleLine = true)
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            if (added) {
                Text(Lang.t("Done! (+XP)", "تم!"), color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            } else {
                Button(onClick = { cardStore.addGenerated(cards.toList()); progress.addXp((cards.size * 3).toLong()); added = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("✅ ${Lang.t("Save", "احفظ")} ${cards.size}")
                }
            }
        }
    }
}

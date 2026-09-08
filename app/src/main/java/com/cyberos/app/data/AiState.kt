package com.cyberos.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.cyberos.app.ui.lang.Lang

enum class ChatMode(val labelEn: String, val labelAr: String, val prompt: String) {
    NORMAL("Normal", "عادي", Agents.CHAT_NORMAL),
    TEACHER("Teacher", "معلّم", Agents.CHAT_TEACHER),
    SOCRATIC("Socratic", "سقراطي", Agents.CHAT_SOCRATIC),
    RESEARCHER("Researcher", "باحث", Agents.CHAT_RESEARCHER)
}

class AiState(
    private val vault: ApiKeyVault,
    private val settingsStore: AiSettingsStore,
    private val client: AiClient
) {
    var messages by mutableStateOf<List<AiChatMessage>>(emptyList())
        private set
    var busy by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
    var configured by mutableStateOf(false)
        private set
    var pendingQuestion by mutableStateOf<String?>(null)
    var pendingContext by mutableStateOf<String?>(null)
    var mode by mutableStateOf(ChatMode.NORMAL)
    var redactionNotice by mutableStateOf<String?>(null)
    var ragNotice by mutableStateOf<String?>(null)
    var currentChatTitle by mutableStateOf<String?>(null)

    var ragSource: () -> List<Note> = { emptyList() }

    init { configured = vault.loadApiKey() != null }

    fun markConfigured() { configured = vault.loadApiKey() != null }
    fun settings(): AiSettings = settingsStore.load()

    fun saveSettings(newKey: String?, s: AiSettings) {
        if (!newKey.isNullOrBlank()) vault.saveApiKey(newKey.trim())
        settingsStore.save(s)
        markConfigured()
    }

    fun removeKey() { vault.wipe(); markConfigured() }

    fun clearChat() {
        messages = emptyList(); error = null; redactionNotice = null; ragNotice = null; currentChatTitle = null
    }

    fun loadArchive(a: ChatArchive) {
        messages = a.messages
        currentChatTitle = a.title
        mode = ChatMode.values().firstOrNull { it.name == a.mode } ?: ChatMode.NORMAL
        error = null; redactionNotice = null; ragNotice = null
    }

    suspend fun send(text: String) {
        val q = text.trim().take(4000)
        if (q.isEmpty() || busy) return
        error = null

        val key = vault.loadApiKey()
        if (key.isNullOrBlank()) {
            configured = false
            error = Lang.t("No API key — open Settings.", "مفيش مفتاح — افتح الإعدادات.")
            return
        }

        var ctx = pendingContext
        pendingContext = null
        if (ctx == null && messages.isEmpty()) {
            val hits = MiniRag.retrieve(q, ragSource())
            if (hits.isNotEmpty()) {
                ctx = hits.joinToString("\n\n") { n -> "[${n.title}]\n${n.body}" }
                val titles = hits.joinToString(", ") { it.title.ifBlank { "Untitled" } }
                ragNotice = Lang.t("🧠 Added ${hits.size} notes: $titles", "🧠 ضمّت ${hits.size} ملاحظات: $titles")
            }
        } else { ragNotice = null }

        val rq = Redactor.redact(q)
        val rctx = ctx?.let { Redactor.redact(it) }
        val hidden = rq.found + (rctx?.found ?: 0)
        redactionNotice = if (hidden > 0)
            Lang.t("🔒 $hidden secret(s) hidden — originals on device", "🔒 تم إخفاء $hidden عنصر — الأصل عندك")
        else null

        val s = settingsStore.load()
        messages = messages + AiChatMessage("user", q)
        if (currentChatTitle == null) currentChatTitle = q.take(40)

        val apiHistory = when {
            rctx != null -> messages.dropLast(1) + AiChatMessage(
                "user", RagSanitizer.contextBlock("user_notes", rctx.text) + "\n\n" + rq.text)
            rq.found > 0 -> messages.dropLast(1) + AiChatMessage("user", rq.text)
            else -> messages
        }

        val sys = if (ctx != null) mode.prompt + " " + RagSanitizer.GUARD else mode.prompt

        busy = true
        try {
            when (val r = client.chat(s.baseUrl, key, s.model, apiHistory, sys)) {
                is AiResult.Success -> messages = messages + AiChatMessage("assistant", r.reply)
                is AiResult.Failure -> error = r.userMessage
            }
        } finally { busy = false }
    }
}

package com.cyberos.app.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.cyberos.app.ui.lang.Lang
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class CouncilSession(
    val id: Long, val question: String, val analyst: String,
    val critic: String, val synthesis: String, val createdAt: Long
)

class CouncilStore(context: Context) {

    private val file: File = File(context.filesDir, "council.json")
    private var cache: MutableList<CouncilSession> = load()

    fun all(): List<CouncilSession> = cache.sortedByDescending { it.createdAt }
    fun nextId(): Long = (cache.maxOfOrNull { it.id } ?: 0L) + 1L

    @Synchronized
    fun add(s: CouncilSession) { cache.add(s); persist() }

    private fun persist() {
        try {
            val a = JSONArray()
            cache.forEach { s ->
                a.put(JSONObject().apply {
                    put("id", s.id); put("q", s.question); put("analyst", s.analyst)
                    put("critic", s.critic); put("synthesis", s.synthesis); put("createdAt", s.createdAt)
                })
            }
            file.writeText(a.toString())
        } catch (_: Exception) { }
    }

    private fun load(): MutableList<CouncilSession> {
        if (!file.exists()) return mutableListOf()
        return try {
            val a = JSONArray(file.readText())
            val out = mutableListOf<CouncilSession>()
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                out.add(CouncilSession(o.getLong("id"), o.optString("q"), o.optString("analyst"),
                    o.optString("critic"), o.optString("synthesis"), o.optLong("createdAt")))
            }
            out
        } catch (_: Exception) { mutableListOf() }
    }
}

class CouncilState(
    private val vault: ApiKeyVault,
    private val settingsStore: AiSettingsStore,
    private val client: AiClient,
    private val store: CouncilStore
) {
    var sessions by mutableStateOf(store.all())
        private set
    var running by mutableStateOf(false)
        private set
    var phase by mutableStateOf(0)
        private set
    var error by mutableStateOf<String?>(null)
    var notice by mutableStateOf<String?>(null)
    var configured by mutableStateOf(false)
        private set

    init { configured = vault.loadApiKey() != null }

    suspend fun run(raw: String): Boolean {
        val q = raw.trim().take(3000)
        if (q.isEmpty() || running) return false
        error = null

        val key = vault.loadApiKey()
        if (key.isNullOrBlank()) {
            error = Lang.t("No API key — open Settings.", "مفيش مفتاح — افتح الإعدادات.")
            return false
        }

        val rq = Redactor.redact(q)
        notice = if (rq.found > 0) Lang.t("🔒 ${rq.found} secret(s) hidden", "🔒 إخفاء ${rq.found}") else null
        val question = rq.text

        val s = settingsStore.load()
        running = true
        phase = 1
        try {
            val analyst = when (val r = client.chat(s.baseUrl, key, s.model,
                listOf(AiChatMessage("user", question)), Agents.COUNCIL_ANALYST)) {
                is AiResult.Success -> r.reply
                is AiResult.Failure -> { error = r.userMessage; return false }
            }

            phase = 2
            val critic = when (val r = client.chat(s.baseUrl, key, s.model,
                listOf(AiChatMessage("user",
                    RagSanitizer.contextBlock("peer_analysis", analyst) + "\n\nQuestion: $question")),
                Agents.COUNCIL_CRITIC)) {
                is AiResult.Success -> r.reply
                is AiResult.Failure -> { error = r.userMessage; return false }
            }

            phase = 3
            val synthesis = when (val r = client.chat(s.baseUrl, key, s.model,
                listOf(AiChatMessage("user",
                    RagSanitizer.contextBlock("analysis", analyst) + "\n\n" +
                        RagSanitizer.contextBlock("critique", critic) + "\n\nQuestion: $question")),
                Agents.COUNCIL_SYNTH)) {
                is AiResult.Success -> r.reply
                is AiResult.Failure -> { error = r.userMessage; return false }
            }

            store.add(CouncilSession(store.nextId(), question, analyst, critic, synthesis, System.currentTimeMillis()))
            sessions = store.all()
            return true
        } finally { running = false; phase = 0 }
    }
}

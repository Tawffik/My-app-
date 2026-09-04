package com.cyberos.app.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class ChatArchive(
    val id: Long,
    val title: String,
    val messages: List<AiChatMessage>,
    val mode: String,
    val createdAt: Long
)

class ChatArchiveStore(context: Context) {

    private val file: File = File(context.filesDir, "chats.json")
    private var cache: MutableList<ChatArchive> = load()

    fun all(): List<ChatArchive> = cache.sortedByDescending { it.createdAt }
    fun get(id: Long): ChatArchive? = cache.firstOrNull { it.id == id }
    private fun nextId(): Long = (cache.maxOfOrNull { it.id } ?: 0L) + 1L

    @Synchronized
    fun add(title: String, messages: List<AiChatMessage>, mode: String): ChatArchive {
        val a = ChatArchive(nextId(), title.trim().take(60), messages, mode, System.currentTimeMillis())
        cache.add(a)
        while (cache.size > 10) { cache.removeAt(0) }
        persist()
        return a
    }

    @Synchronized
    fun delete(id: Long) { cache.removeAll { it.id == id }; persist() }

    private fun persist() {
        try {
            val a = JSONArray()
            cache.forEach { c ->
                a.put(JSONObject().apply {
                    put("id", c.id); put("title", c.title); put("mode", c.mode); put("createdAt", c.createdAt)
                    put("messages", JSONArray().apply {
                        c.messages.forEach { m ->
                            put(JSONObject().apply { put("role", m.role); put("content", m.content) })
                        }
                    })
                })
            }
            file.writeText(a.toString())
        } catch (_: Exception) { }
    }

    private fun load(): MutableList<ChatArchive> {
        if (!file.exists()) return mutableListOf()
        return try {
            val a = JSONArray(file.readText())
            val out = mutableListOf<ChatArchive>()
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                val msgs = mutableListOf<AiChatMessage>()
                o.optJSONArray("messages")?.let { ma ->
                    for (j in 0 until ma.length()) {
                        val mo = ma.getJSONObject(j)
                        msgs.add(AiChatMessage(mo.optString("role"), mo.optString("content")))
                    }
                }
                out.add(ChatArchive(o.getLong("id"), o.optString("title"), msgs, o.optString("mode", "NORMAL"), o.optLong("createdAt")))
            }
            out
        } catch (_: Exception) { mutableListOf() }
    }
}

class ChatArchiveState(private val store: ChatArchiveStore) {
    var list by mutableStateOf(store.all())
        private set

    fun refresh() { list = store.all() }

    fun save(title: String, messages: List<AiChatMessage>, mode: String): ChatArchive {
        val a = store.add(title, messages, mode)
        list = store.all()
        return a
    }

    fun delete(id: Long) { store.delete(id); list = store.all() }
}

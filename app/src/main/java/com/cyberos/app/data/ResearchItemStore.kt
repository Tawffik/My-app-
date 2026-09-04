package com.cyberos.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ResearchItemStore(context: Context) {

    private val file: File = File(context.filesDir, "research_items.json")
    private var cache: MutableList<ResearchItem> = load()

    fun all(): List<ResearchItem> = cache.sortedByDescending { it.publishedAt }
    fun get(id: Long): ResearchItem? = cache.firstOrNull { it.id == id }
    fun nextId(): Long = (cache.maxOfOrNull { it.id } ?: 0L) + 1L

    @Synchronized
    fun addAll(items: List<ResearchItem>) {
        items.forEach { cache.add(it) }
        while (cache.size > 500) { cache.removeAt(0) }
        persist()
    }

    @Synchronized
    fun toggleBookmark(id: Long) {
        val idx = cache.indexOfFirst { it.id == id }
        if (idx >= 0) { cache[idx] = cache[idx].copy(bookmarked = !cache[idx].bookmarked); persist() }
    }

    @Synchronized
    fun markRead(id: Long) {
        val idx = cache.indexOfFirst { it.id == id }
        if (idx >= 0 && !cache[idx].read) { cache[idx] = cache[idx].copy(read = true); persist() }
    }

    fun bookmarked(): List<ResearchItem> = cache.filter { it.bookmarked }.sortedByDescending { it.publishedAt }

    private fun toJson(): String {
        val a = JSONArray()
        cache.forEach { r ->
            a.put(JSONObject().apply {
                put("id", r.id); put("sourceId", r.sourceId); put("title", r.title)
                put("link", r.link); put("author", r.author); put("summary", r.summary)
                put("category", r.category); put("tags", JSONArray(r.tags))
                put("publishedAt", r.publishedAt); put("retrievedAt", r.retrievedAt)
                put("read", r.read); put("bookmarked", r.bookmarked)
            })
        }
        return a.toString()
    }

    private fun persist() { try { file.writeText(toJson()) } catch (_: Exception) { } }

    private fun load(): MutableList<ResearchItem> {
        if (!file.exists()) return mutableListOf()
        return try {
            val a = JSONArray(file.readText())
            val out = mutableListOf<ResearchItem>()
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                val tags = mutableListOf<String>()
                o.optJSONArray("tags")?.let { t -> for (j in 0 until t.length()) tags.add(t.optString(j)) }
                out.add(ResearchItem(
                    o.getLong("id"), o.optLong("sourceId"), o.optString("title"), o.optString("link"),
                    o.optString("author"), o.optString("summary"), o.optString("category", "General"),
                    tags, o.optLong("publishedAt"), o.optLong("retrievedAt"),
                    o.optBoolean("read", false), o.optBoolean("bookmarked", false)
                ))
            }
            out
        } catch (_: Exception) { mutableListOf() }
    }
}

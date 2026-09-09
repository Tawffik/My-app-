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
        while (cache.size > 800) { cache.removeAt(0) }
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
                put("vulnerabilityType", r.vulnerabilityType)
                put("publishedAt", r.publishedAt); put("retrievedAt", r.retrievedAt)
                put("read", r.read); put("bookmarked", r.bookmarked)
            })
        }
        return a.toString()
    }

    
    /**
     * Merge curated writeups + channel hubs by URL.
     * Safe to call on every launch — only inserts missing links (never wipes user data).
     */
    @Synchronized
    fun seedCuratedIfNeeded() {
        val marker = "cyberos-curated-v2"
        val existingLinks = cache.map { it.link.trim().lowercase() }.toHashSet()
        val now = System.currentTimeMillis()
        var id = nextId()
        val toAdd = mutableListOf<ResearchItem>()

        for (e in CuratedWriteupLibrary.WRITEUPS) {
            val link = e.link.trim()
            if (link.isEmpty() || link.lowercase() in existingLinks) continue
            existingLinks.add(link.lowercase())
            toAdd.add(
                ResearchItem(
                    id = id++,
                    sourceId = 0L,
                    title = e.title,
                    link = link,
                    author = "CyberOS Curated",
                    summary = e.summary,
                    category = e.category,
                    tags = (e.tags + listOf("writeup", "curated", marker)).distinct(),
                    vulnerabilityType = BugBountyFilters.detectVulnType(e.title, e.summary),
                    publishedAt = now,
                    retrievedAt = now
                )
            )
        }
        for ((name, url) in CuratedWriteupLibrary.CHANNEL_HINTS) {
            val link = url.trim()
            if (link.isEmpty() || link.lowercase() in existingLinks) continue
            existingLinks.add(link.lowercase())
            toAdd.add(
                ResearchItem(
                    id = id++,
                    sourceId = 0L,
                    title = "Follow: $name",
                    link = link,
                    author = "CyberOS Curated",
                    summary = "Live channel / site — open in browser (not auto-synced).",
                    category = "Tips",
                    tags = listOf("tips", "channel", "curated", marker),
                    vulnerabilityType = "",
                    publishedAt = now,
                    retrievedAt = now
                )
            )
        }
        if (toAdd.isEmpty()) return
        cache.addAll(toAdd)
        while (cache.size > 1200) cache.removeAt(0)
        persist()
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
                out.add(
                    ResearchItem(
                        o.getLong("id"),
                        o.optLong("sourceId"),
                        o.optString("title"),
                        o.optString("link"),
                        o.optString("author"),
                        o.optString("summary"),
                        o.optString("category", "General"),
                        tags,
                        o.optString("vulnerabilityType", ""),
                        o.optLong("publishedAt"),
                        o.optLong("retrievedAt"),
                        o.optBoolean("read", false),
                        o.optBoolean("bookmarked", false)
                    )
                )
            }
            out
        } catch (_: Exception) {
            mutableListOf()
        }
    }
}

package com.cyberos.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ResearchSourceStore(context: Context) {

    private val file: File = File(context.filesDir, "research_sources.json")
    private var cache: MutableList<ResearchSource> = load()

    fun all(): List<ResearchSource> = cache.sortedBy { it.name }
    fun enabled(): List<ResearchSource> = cache.filter { it.enabled }
    fun get(id: Long): ResearchSource? = cache.firstOrNull { it.id == id }
    fun nextId(): Long = (cache.maxOfOrNull { it.id } ?: 0L) + 1L

    @Synchronized
    fun save(source: ResearchSource) {
        val idx = cache.indexOfFirst { it.id == source.id }
        if (idx >= 0) cache[idx] = source else cache.add(source)
        persist()
    }

    @Synchronized
    fun toggleEnabled(id: Long) {
        val idx = cache.indexOfFirst { it.id == id }
        if (idx >= 0) { cache[idx] = cache[idx].copy(enabled = !cache[idx].enabled); persist() }
    }

    fun ensureSeeded() {
        if (file.exists() || cache.isNotEmpty()) return
        cache.addAll(listOf(
            ResearchSource(1, "PortSwigger Research", "https://portswigger.net/research/rss", "RSS", true, "Tier2", "Web Security"),
            ResearchSource(2, "OWASP News", "https://owasp.org/news/index.xml", "RSS", true, "Tier1", "General"),
            ResearchSource(3, "CISA Advisories", "https://www.cisa.gov/cybersecurity-advisories/all.xml", "RSS", true, "Tier1", "Vulnerabilities")
        ))
        persist()
    }

    private fun toJson(): String {
        val a = JSONArray()
        cache.forEach { s ->
            a.put(JSONObject().apply {
                put("id", s.id); put("name", s.name); put("url", s.url); put("type", s.type)
                put("enabled", s.enabled); put("trustLevel", s.trustLevel); put("category", s.category)
            })
        }
        return a.toString()
    }

    private fun persist() { try { file.writeText(toJson()) } catch (_: Exception) { } }

    private fun load(): MutableList<ResearchSource> {
        if (!file.exists()) return mutableListOf()
        return try {
            val a = JSONArray(file.readText())
            val out = mutableListOf<ResearchSource>()
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                out.add(ResearchSource(
                    o.getLong("id"), o.optString("name"), o.optString("url"),
                    o.optString("type", "RSS"), o.optBoolean("enabled", true),
                    o.optString("trustLevel", "Tier2"), o.optString("category", "General")
                ))
            }
            out
        } catch (_: Exception) { mutableListOf() }
    }
}

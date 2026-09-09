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
        if (idx >= 0) {
            cache[idx] = cache[idx].copy(enabled = !cache[idx].enabled)
            persist()
        }
    }

    /**
     * Seed default sources. Also merges any missing curated sources by URL
     * so upgrades pick up new Daily Writeup feeds without wiping user data.
     */
    fun ensureSeeded() {
        val defaults = defaultSources()
        if (cache.isEmpty()) {
            cache.addAll(defaults)
            persist()
            return
        }
        val existingUrls = cache.map { it.url.lowercase() }.toSet()
        var changed = false
        var next = nextId()
        for (src in defaults) {
            if (src.url.lowercase() !in existingUrls) {
                cache.add(src.copy(id = next++))
                changed = true
            }
        }
        if (changed) persist()
    }

    private fun defaultSources(): List<ResearchSource> = listOf(
        ResearchSource(1, "PortSwigger Research", "https://portswigger.net/research/rss", "RSS", true, "Tier1", "Web Security"),
        ResearchSource(2, "OWASP News", "https://owasp.org/news/index.xml", "RSS", true, "Tier1", "General"),
        ResearchSource(3, "CISA Advisories", "https://www.cisa.gov/cybersecurity-advisories/all.xml", "RSS", true, "Tier1", "Vulnerabilities"),
        ResearchSource(4, "Medium Bug Bounty", "https://medium.com/feed/tag/bug-bounty", "RSS", true, "Tier2", "Bug Bounty"),
        ResearchSource(5, "InfoSec Write-ups", "https://infosecwriteups.com/feed", "RSS", true, "Tier2", "Bug Bounty"),
        ResearchSource(
            6,
            "Daily BB Writeups (SecurityCipher)",
            "https://raw.githubusercontent.com/securitycipher/daily-bugbounty-writeups/main/README.md",
            "MARKDOWN",
            true,
            "Tier2",
            "Bug Bounty"
        ),
        ResearchSource(7, "Medium BugBountyWriteup", "https://medium.com/feed/tag/bugbountywriteup", "RSS", true, "Tier2", "Bug Bounty"),
        ResearchSource(8, "NVIDIA Developer Cybersecurity", "https://developer.nvidia.com/blog/tag/cybersecurity/feed/", "RSS", true, "Tier1", "AI Security"),
        ResearchSource(9, "Simon Willison", "https://simonwillison.net/atom/everything/", "RSS", true, "Tier1", "AI Security"),
        ResearchSource(10, "Google Project Zero", "https://googleprojectzero.blogspot.com/feeds/posts/default", "RSS", true, "Tier1", "Vulnerabilities"),
        ResearchSource(11, "Krebs on Security", "https://krebsonsecurity.com/feed/", "RSS", true, "Tier2", "Threat Intelligence"),
        // Additional writeup aggregators & platforms
        ResearchSource(
            12,
            "Awesome BB Writeups (GitHub)",
            "https://raw.githubusercontent.com/devanshbatham/Awesome-Bugbounty-Writeups/master/README.md",
            "MARKDOWN",
            true,
            "Tier2",
            "Bug Bounty"
        ),
        ResearchSource(
            13,
            "HackerOne Disclosed (archive list)",
            "https://raw.githubusercontent.com/ajaysenr/HackerOne-Disclosed-Reports/main/reports.txt",
            "LINKLIST",
            true,
            "Tier2",
            "Bug Bounty"
        ),
        ResearchSource(14, "Intigriti Blog", "https://www.intigriti.com/blog/feed", "RSS", true, "Tier2", "Bug Bounty"),
        ResearchSource(15, "YesWeHack Blog", "https://www.yeswehack.com/feed", "RSS", true, "Tier2", "Bug Bounty"),
        ResearchSource(16, "Medium XSS tag", "https://medium.com/feed/tag/xss", "RSS", true, "Tier2", "Bug Bounty"),
        ResearchSource(17, "Medium IDOR tag", "https://medium.com/feed/tag/idor", "RSS", true, "Tier2", "Bug Bounty"),
        ResearchSource(18, "GitHub Blog Bug Bounty", "https://github.blog/tag/bug-bounty/feed/", "RSS", true, "Tier2", "Bug Bounty"),
        ResearchSource(19, "Medium Bug Bounty Tips", "https://medium.com/feed/tag/bug-bounty-tips", "RSS", true, "Tier2", "Tips"),
        ResearchSource(20, "Embrace The Red", "https://embracethered.com/blog/index.xml", "RSS", true, "Tier1", "AI Security")
    )

    private fun toJson(): String {
        val a = JSONArray()
        cache.forEach { s ->
            a.put(
                JSONObject().apply {
                    put("id", s.id); put("name", s.name); put("url", s.url); put("type", s.type)
                    put("enabled", s.enabled); put("trustLevel", s.trustLevel); put("category", s.category)
                }
            )
        }
        return a.toString()
    }

    private fun persist() {
        try {
            file.writeText(toJson())
        } catch (_: Exception) {
        }
    }

    private fun load(): MutableList<ResearchSource> {
        if (!file.exists()) return mutableListOf()
        return try {
            val a = JSONArray(file.readText())
            val out = mutableListOf<ResearchSource>()
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                out.add(
                    ResearchSource(
                        o.getLong("id"),
                        o.optString("name"),
                        o.optString("url"),
                        o.optString("type", "RSS"),
                        o.optBoolean("enabled", true),
                        o.optString("trustLevel", "Tier2"),
                        o.optString("category", "General")
                    )
                )
            }
            out
        } catch (_: Exception) {
            mutableListOf()
        }
    }
}

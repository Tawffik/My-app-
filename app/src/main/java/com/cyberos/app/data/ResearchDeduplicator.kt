package com.cyberos.app.data

object ResearchDeduplicator {

    fun normalize(title: String, link: String): String {
        val t = title.trim().lowercase()
            .replace(Regex("[^a-z0-9\\u0621-\\u064A ]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
        val cleanLink = link.trim().lowercase().substringBefore("?").removeSuffix("/")
        return if (t.isNotBlank()) t else cleanLink
    }

    fun dedupe(items: List<ResearchItem>, existing: List<ResearchItem> = emptyList()): List<ResearchItem> {
        val seen = existing.map { normalize(it.title, it.link) }.toMutableSet()
        val out = mutableListOf<ResearchItem>()
        for (item in items) {
            val key = normalize(item.title, item.link)
            if (key.isBlank() || seen.contains(key)) continue
            seen.add(key)
            out.add(item)
        }
        return out
    }
}

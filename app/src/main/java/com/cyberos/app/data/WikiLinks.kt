package com.cyberos.app.data

object WikiLinks {

    private val LINK_RE = Regex("\\[\\[([^\\]\\n]+)\\]\\]")

    fun extractTargets(text: String): List<String> =
        LINK_RE.findAll(text)
            .map { it.groupValues[1].trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .toList()

    fun stripForPreview(text: String, maxLen: Int = 120): String {
        val cleaned = LINK_RE.replace(text, "$1")
        return cleaned.trim().take(maxLen)
    }

    fun backlinksTo(title: String, notes: List<Note>, excludeId: Long? = null): List<Note> {
        val target = title.trim()
        if (target.isEmpty()) return emptyList()
        val lower = target.lowercase()
        return notes.filter { n ->
            n.id != excludeId && extractTargets(n.body).any { it.lowercase() == lower }
        }
    }

    fun resolveTarget(target: String, notes: List<Note>): Note? {
        val t = target.trim().lowercase()
        if (t.isEmpty()) return null
        return notes.firstOrNull { it.title.trim().lowercase() == t }
    }
}

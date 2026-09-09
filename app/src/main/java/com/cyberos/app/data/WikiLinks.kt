package com.cyberos.app.data

object WikiLinks {

    private val LINK_RE = Regex("""\[\[([^\]\n]+)\]\]""")
    private val TAG_RE = Regex("""(?<![/#\w])#([\w\u0600-\u06FF\-/]+)""")

    fun extractTargets(text: String): List<String> =
        LINK_RE.findAll(text)
            .map { it.groupValues[1].trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .toList()

    fun extractInlineTags(text: String): List<String> =
        TAG_RE.findAll(text)
            .map { it.groupValues[1].trim().lowercase() }
            .filter { it.isNotEmpty() }
            .distinct()
            .toList()

    fun mergeTags(explicit: List<String>, body: String): List<String> =
        (explicit.map { it.trim().removePrefix("#") }.filter { it.isNotEmpty() } + extractInlineTags(body))
            .map { it.lowercase() }
            .distinct()

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

    /** Simple edges for graph: from note id → target note ids */
    fun graphEdges(notes: List<Note>): List<Pair<Long, Long>> {
        val byTitle = notes.associateBy { it.title.trim().lowercase() }
        val edges = mutableListOf<Pair<Long, Long>>()
        for (n in notes) {
            for (t in extractTargets(n.body)) {
                val other = byTitle[t.lowercase()] ?: continue
                if (other.id != n.id) edges.add(n.id to other.id)
            }
        }
        return edges
    }
}

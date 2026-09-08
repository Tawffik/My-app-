package com.cyberos.app.data

object MiniRag {

    private val STOP = setOf(
        "اتكلم", "عن", "من", "في", "على", "الى", "إلى", "ما", "هو", "هي",
        "the", "about", "what", "is", "are", "how", "does", "and", "or", "of", "to", "in",
        "for", "a", "an", "عني", "لي", "قولي", "اشرح", "فيه", "دي", "ده",
        "tell", "me", "explain"
    )

    fun keywords(query: String): List<String> =
        query.split(Regex("[\\s,.:;!?()\\[\\]\"']+") )
            .map { it.trim().lowercase() }
            .filter { it.length >= 3 && it !in STOP && !it.all { c -> c.isDigit() } }
            .distinct()
            .take(6)

    fun retrieve(query: String, notes: List<Note>, limit: Int = 3): List<Note> {
        val kws = keywords(query)
        if (kws.isEmpty()) return emptyList()
        return notes.mapNotNull { n ->
            val text = (n.title + " " + n.body + " " + n.tags.joinToString(" ")).lowercase()
            var score = 0
            kws.forEach { k -> if (text.contains(k)) score += (if (n.title.lowercase().contains(k)) 2 else 1) }
            if (score > 0) n to score else null
        }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }
}

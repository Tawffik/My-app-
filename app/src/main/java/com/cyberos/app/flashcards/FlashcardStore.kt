package com.cyberos.app.flashcards

import android.content.Context
import com.cyberos.app.data.JsonCodec
import com.cyberos.app.learning.CyberCurriculum
import java.io.File

class FlashcardStore(context: Context) {

    private val file: File = File(context.filesDir, "flashcards.json")
    private var cache: MutableList<Flashcard> = load()

    fun all(): List<Flashcard> = cache.sortedBy { it.id }
    fun get(id: Long): Flashcard? = cache.firstOrNull { it.id == id }
    fun nextId(): Long = (cache.maxOfOrNull { it.id } ?: 0L) + 1L
    fun countDue(now: Long): Int = cache.count { it.dueAt <= now }
    fun countBySource(source: String): Int = cache.count { it.source == source }

    @Synchronized
    fun save(card: Flashcard) {
        val index = cache.indexOfFirst { it.id == card.id }
        if (index >= 0) cache[index] = card else cache.add(card)
        persist()
    }

    @Synchronized
    fun addGenerated(pairs: List<Pair<String, String>>): Int {
        var id = nextId()
        var added = 0
        pairs.forEach { (q, a) -> cache.add(Flashcard(id, null, q, a, source = "ai")); id++; added++ }
        persist()
        return added
    }

    @Synchronized
    fun delete(id: Long) { cache.removeAll { it.id == id }; persist() }

    @Synchronized
    fun deleteBySource(source: String) { cache.removeAll { it.source == source }; persist() }

    fun toJson(): String = JsonCodec.flashcardsToJson(cache)

    @Synchronized
    fun replaceAll(text: String) {
        cache = try { JsonCodec.parseFlashcards(text).toMutableList() } catch (_: Exception) { mutableListOf() }
        persist()
    }

    fun ensureSeeded() {
        if (file.exists() || cache.isNotEmpty()) return
        var id = 1L
        CyberCurriculum.allFlashcards().forEach { (topicId, q, a) -> cache.add(Flashcard(id, topicId, q, a)); id++ }
        persist()
    }

    private fun persist() { try { file.writeText(toJson()) } catch (_: Exception) { } }
    private fun load(): MutableList<Flashcard> {
        if (!file.exists()) return mutableListOf()
        return try { JsonCodec.parseFlashcards(file.readText()).toMutableList() } catch (_: Exception) { mutableListOf() }
    }
}

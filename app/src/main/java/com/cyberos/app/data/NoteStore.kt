package com.cyberos.app.data

import android.content.Context
import java.io.File

class NoteStore(context: Context) {

    private val file: File = File(context.filesDir, "notes.json")
    private var cache: MutableList<Note> = load()

    fun all(): List<Note> = cache.sortedByDescending { it.updatedAt }
    fun get(id: Long): Note? = cache.firstOrNull { it.id == id }
    fun nextId(): Long = (cache.maxOfOrNull { it.id } ?: 0L) + 1L

    @Synchronized
    fun save(note: Note) {
        val index = cache.indexOfFirst { it.id == note.id }
        if (index >= 0) cache[index] = note else cache.add(note)
        persist()
    }

    @Synchronized
    fun delete(id: Long) { cache.removeAll { it.id == id }; persist() }

    fun toJson(): String = JsonCodec.notesToJson(cache)

    @Synchronized
    fun replaceAll(text: String) {
        cache = try { JsonCodec.parseNotes(text).toMutableList() } catch (_: Exception) { mutableListOf() }
        persist()
    }

    private fun persist() { try { file.writeText(toJson()) } catch (_: Exception) { } }
    private fun load(): MutableList<Note> {
        if (!file.exists()) return mutableListOf()
        return try { JsonCodec.parseNotes(file.readText()).toMutableList() } catch (_: Exception) { mutableListOf() }
    }
}

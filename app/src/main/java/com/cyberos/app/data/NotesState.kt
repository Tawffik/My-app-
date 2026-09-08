package com.cyberos.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class NotesState(private val store: NoteStore) {

    var notes by mutableStateOf(store.all())
        private set
    var query by mutableStateOf("")

    fun reload() { notes = store.all() }
    fun get(id: Long): Note? = store.get(id)

    fun upsert(existingId: Long, title: String, body: String, tags: List<String>) {
        val now = System.currentTimeMillis()
        val note = if (existingId > 0) {
            val old = store.get(existingId) ?: return
            old.copy(title = title, body = body, tags = tags, updatedAt = now)
        } else {
            Note(store.nextId(), title, body, tags, now, now)
        }
        store.save(note)
        notes = store.all()
    }

    fun delete(id: Long) { store.delete(id); notes = store.all() }

    fun filtered(): List<Note> {
        val q = query.trim()
        if (q.isEmpty()) return notes
        return notes.filter { note ->
            note.title.contains(q, ignoreCase = true) ||
                note.body.contains(q, ignoreCase = true) ||
                note.tags.any { it.contains(q, ignoreCase = true) }
        }
    }
}

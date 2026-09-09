package com.cyberos.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesState(private val store: NoteStore) {

    var notes by mutableStateOf(store.all())
        private set
    var query by mutableStateOf("")
    var folderFilter by mutableStateOf("All")
    var tagFilter by mutableStateOf("All")
    var pinnedOnly by mutableStateOf(false)

    fun reload() { notes = store.all() }
    fun get(id: Long): Note? = store.get(id)

    fun upsert(
        existingId: Long,
        title: String,
        body: String,
        tags: List<String>,
        folder: String = "",
        pinned: Boolean = false
    ) {
        val now = System.currentTimeMillis()
        val mergedTags = WikiLinks.mergeTags(tags, body)
        val note = if (existingId > 0) {
            val old = store.get(existingId) ?: return
            old.copy(
                title = title,
                body = body,
                tags = mergedTags,
                updatedAt = now,
                folder = folder.trim(),
                pinned = pinned
            )
        } else {
            Note(
                id = store.nextId(),
                title = title,
                body = body,
                tags = mergedTags,
                createdAt = now,
                updatedAt = now,
                folder = folder.trim(),
                pinned = pinned
            )
        }
        store.save(note)
        notes = store.all()
    }

    fun togglePin(id: Long) {
        val n = store.get(id) ?: return
        store.save(n.copy(pinned = !n.pinned, updatedAt = System.currentTimeMillis()))
        notes = store.all()
    }

    fun delete(id: Long) { store.delete(id); notes = store.all() }

    fun folders(): List<String> =
        notes.map { it.folder.ifBlank { "Inbox" } }.distinct().sorted()

    fun allTags(): List<String> =
        notes.flatMap { it.tags }.map { it.lowercase() }.distinct().sorted()

    fun filtered(): List<Note> {
        var list = notes
        if (pinnedOnly) list = list.filter { it.pinned }
        if (folderFilter != "All") {
            list = list.filter {
                val f = it.folder.ifBlank { "Inbox" }
                f.equals(folderFilter, ignoreCase = true)
            }
        }
        if (tagFilter != "All") {
            list = list.filter { n -> n.tags.any { it.equals(tagFilter, ignoreCase = true) } }
        }
        val q = query.trim()
        if (q.isNotEmpty()) {
            list = list.filter { note ->
                note.title.contains(q, ignoreCase = true) ||
                    note.body.contains(q, ignoreCase = true) ||
                    note.tags.any { it.contains(q, ignoreCase = true) } ||
                    note.folder.contains(q, ignoreCase = true)
            }
        }
        return list.sortedWith(
            compareByDescending<Note> { it.pinned }.thenByDescending { it.updatedAt }
        )
    }

    fun orphans(): List<Note> {
        val edges = WikiLinks.graphEdges(notes)
        val linked = edges.flatMap { listOf(it.first, it.second) }.toSet()
        return notes.filter { it.id !in linked }
    }

    fun duplicate(id: Long): Long {
        val n = store.get(id) ?: return -1L
        val copy = n.copy(
            id = store.nextId(),
            title = n.title + " (copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            pinned = false
        )
        store.save(copy)
        notes = store.all()
        return copy.id
    }

    /** Mentions of this title in other notes without formal [[link]] — title substring heuristic. */
    fun unlinkedMentions(title: String, excludeId: Long?): List<Note> {
        val t = title.trim()
        if (t.length < 3) return emptyList()
        val lower = t.lowercase()
        return notes.filter { n ->
            n.id != excludeId &&
                n.body.contains(t, ignoreCase = true) &&
                WikiLinks.extractTargets(n.body).none { it.equals(t, ignoreCase = true) }
        }
    }

    /** Create or open today's daily note. Returns note id. */
    fun openOrCreateDaily(): Long {
        val day = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val title = "Daily $day"
        val existing = notes.firstOrNull { it.title.equals(title, ignoreCase = true) }
        if (existing != null) return existing.id
        val body = """
## Focus
-

## Learned
-

## Linked notes
- [[]]

## Next
-
""".trimIndent()
        upsert(-1L, title, body, listOf("daily"), folder = "Daily", pinned = false)
        return notes.first { it.title.equals(title, ignoreCase = true) }.id
    }
}

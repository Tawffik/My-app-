package com.cyberos.app.methodology

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MethodologyState(private val store: MethodologyStore) {

    var list by mutableStateOf(store.all())
        private set

    fun refresh() { list = store.all() }
    fun get(id: Long): Methodology? = store.get(id)

    fun create(title: String): Long {
        val id = store.nextId()
        val now = System.currentTimeMillis()
        store.save(Methodology(id = id, title = title.trim(), createdAt = now, updatedAt = now))
        refresh()
        return id
    }

    fun rename(id: Long, title: String) {
        val m = store.get(id) ?: return
        val t = title.trim()
        if (t.isEmpty()) return
        store.save(m.copy(title = t, updatedAt = System.currentTimeMillis()))
        refresh()
    }

    fun delete(id: Long) { store.delete(id); refresh() }

    fun addStep(id: Long, text: String) {
        val m = store.get(id) ?: return
        val t = text.trim()
        if (t.isEmpty()) return
        store.save(m.copy(steps = m.steps + MethodologyStep(System.currentTimeMillis(), t), updatedAt = System.currentTimeMillis()))
        refresh()
    }

    fun toggleStep(id: Long, stepId: Long) {
        val m = store.get(id) ?: return
        store.save(m.copy(
            steps = m.steps.map { if (it.id == stepId) it.copy(done = !it.done) else it },
            updatedAt = System.currentTimeMillis()
        ))
        refresh()
    }

    fun deleteStep(id: Long, stepId: Long) {
        val m = store.get(id) ?: return
        store.save(m.copy(steps = m.steps.filter { it.id != stepId }, updatedAt = System.currentTimeMillis()))
        refresh()
    }

    fun moveStep(id: Long, from: Int, to: Int) {
        val m = store.get(id) ?: return
        if (from == to || from < 0 || to < 0 || from >= m.steps.size || to >= m.steps.size) return
        val steps = m.steps.toMutableList()
        val item = steps.removeAt(from)
        steps.add(to, item)
        store.save(m.copy(steps = steps, updatedAt = System.currentTimeMillis()))
        refresh()
    }
}

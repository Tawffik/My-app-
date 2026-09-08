package com.cyberos.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDate

class TaskState(private val store: TaskStore) {

    var tasks by mutableStateOf(store.all())
        private set
    var filter by mutableStateOf("ALL")

    fun refresh() { tasks = store.all() }
    fun get(id: Long): Task? = store.get(id)

    fun delete(id: Long) { store.delete(id); tasks = store.all() }

    fun toggleDone(id: Long) {
        val t = store.get(id) ?: return
        val now = System.currentTimeMillis()
        if (t.status == TaskFields.DONE) {
            store.save(t.copy(status = TaskFields.TODO, completedAt = null, updatedAt = now))
        } else {
            store.save(t.copy(status = TaskFields.DONE, completedAt = now, updatedAt = now))
        }
        tasks = store.all()
    }

    fun upsert(existingId: Long, title: String, notes: String, status: String, priority: String, projectId: Long?, dueDay: Long?) {
        val t = title.trim()
        if (t.isEmpty()) return
        val now = System.currentTimeMillis()
        if (existingId > 0) {
            val old = store.get(existingId) ?: return
            store.save(old.copy(
                title = t, notes = notes, status = status, priority = priority,
                projectId = projectId, dueDay = dueDay, updatedAt = now,
                completedAt = if (status == TaskFields.DONE) (old.completedAt ?: now) else null
            ))
        } else {
            store.save(Task(
                id = store.nextId(), title = t, notes = notes, status = status,
                priority = priority, projectId = projectId, dueDay = dueDay,
                createdAt = now, updatedAt = now,
                completedAt = if (status == TaskFields.DONE) now else null
            ))
        }
        tasks = store.all()
    }

    fun detachFromProject(pid: Long) {
        val now = System.currentTimeMillis()
        tasks.filter { it.projectId == pid }.forEach { store.save(it.copy(projectId = null, updatedAt = now)) }
        tasks = store.all()
    }

    private val priorityRank = mapOf(TaskFields.HIGH to 0, TaskFields.MEDIUM to 1, TaskFields.LOW to 2)

    fun filtered(): List<Task> {
        val today = LocalDate.now().toEpochDay()
        val base = when (filter) {
            "TODAY" -> tasks.filter { it.status != TaskFields.DONE && it.dueDay != null && it.dueDay == today }
            "OVERDUE" -> tasks.filter { it.status != TaskFields.DONE && it.dueDay != null && it.dueDay < today }
            "OPEN" -> tasks.filter { it.status != TaskFields.DONE }
            "DONE" -> tasks.filter { it.status == TaskFields.DONE }
            else -> tasks
        }
        return base.sortedWith(compareBy(
            { it.status == TaskFields.DONE },
            { it.dueDay ?: Long.MAX_VALUE },
            { priorityRank[it.priority] ?: 1 }
        ))
    }
}

class ProjectState(private val store: ProjectStore) {

    var list by mutableStateOf(store.all())
        private set

    fun refresh() { list = store.all() }
    fun get(id: Long): Project? = store.get(id)

    fun create(title: String): Long {
        val t = title.trim()
        if (t.isEmpty()) return -1L
        val id = store.nextId()
        store.save(Project(id = id, title = t, createdAt = System.currentTimeMillis()))
        list = store.all()
        return id
    }

    fun rename(id: Long, title: String) {
        val p = store.get(id) ?: return
        val t = title.trim()
        if (t.isEmpty()) return
        store.save(p.copy(title = t))
        list = store.all()
    }

    fun delete(id: Long) { store.delete(id); list = store.all() }
}

package com.cyberos.app.data

import android.content.Context
import java.io.File

object TaskFields {
    const val TODO = "TODO"
    const val IN_PROGRESS = "IN_PROGRESS"
    const val DONE = "DONE"
    const val HIGH = "HIGH"
    const val MEDIUM = "MEDIUM"
    const val LOW = "LOW"
}

data class Task(
    val id: Long,
    val title: String,
    val notes: String = "",
    val status: String = TaskFields.TODO,
    val priority: String = TaskFields.MEDIUM,
    val projectId: Long? = null,
    val dueDay: Long? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val completedAt: Long? = null
)

data class Project(
    val id: Long,
    val title: String,
    val emoji: String = "📁",
    val createdAt: Long = 0L
)

class TaskStore(context: Context) {

    private val file: File = File(context.filesDir, "tasks.json")
    private var cache: MutableList<Task> = load()

    fun all(): List<Task> = cache.sortedByDescending { it.updatedAt }
    fun get(id: Long): Task? = cache.firstOrNull { it.id == id }
    fun nextId(): Long = (cache.maxOfOrNull { it.id } ?: 0L) + 1L

    @Synchronized
    fun save(task: Task) {
        val index = cache.indexOfFirst { it.id == task.id }
        if (index >= 0) cache[index] = task else cache.add(task)
        persist()
    }

    @Synchronized
    fun delete(id: Long) {
        cache.removeAll { it.id == id }
        persist()
    }

    fun toJson(): String = JsonCodec.tasksToJson(cache)

    @Synchronized
    fun replaceAll(text: String) {
        cache = try { JsonCodec.parseTasks(text).toMutableList() } catch (_: Exception) { mutableListOf() }
        persist()
    }

    private fun persist() { try { file.writeText(toJson()) } catch (_: Exception) { } }
    private fun load(): MutableList<Task> {
        if (!file.exists()) return mutableListOf()
        return try { JsonCodec.parseTasks(file.readText()).toMutableList() } catch (_: Exception) { mutableListOf() }
    }
}

class ProjectStore(context: Context) {

    private val file: File = File(context.filesDir, "projects.json")
    private var cache: MutableList<Project> = load()

    fun all(): List<Project> = cache.sortedByDescending { it.createdAt }
    fun get(id: Long): Project? = cache.firstOrNull { it.id == id }
    fun nextId(): Long = (cache.maxOfOrNull { it.id } ?: 0L) + 1L

    @Synchronized
    fun save(p: Project) {
        val index = cache.indexOfFirst { it.id == p.id }
        if (index >= 0) cache[index] = p else cache.add(p)
        persist()
    }

    @Synchronized
    fun delete(id: Long) {
        cache.removeAll { it.id == id }
        persist()
    }

    fun toJson(): String = JsonCodec.projectsToJson(cache)

    @Synchronized
    fun replaceAll(text: String) {
        cache = try { JsonCodec.parseProjects(text).toMutableList() } catch (_: Exception) { mutableListOf() }
        persist()
    }

    private fun persist() { try { file.writeText(toJson()) } catch (_: Exception) { } }
    private fun load(): MutableList<Project> {
        if (!file.exists()) return mutableListOf()
        return try { JsonCodec.parseProjects(file.readText()).toMutableList() } catch (_: Exception) { mutableListOf() }
    }
}

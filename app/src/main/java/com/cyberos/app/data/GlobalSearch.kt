package com.cyberos.app.data

import com.cyberos.app.learning.CyberCurriculum
import com.cyberos.app.methodology.Methodology

object GlobalSearch {

    data class Hit(val kind: String, val ref: String, val title: String, val subtitle: String)

    private const val LIMIT = 8

    fun search(q: String, notes: List<Note>, tasks: List<Task>, projects: List<Project>, meths: List<Methodology>): List<Hit> {
        val query = q.trim().lowercase()
        if (query.isEmpty()) return emptyList()
        val out = mutableListOf<Hit>()

        var topicCount = 0
        for (p in CyberCurriculum.paths) {
            for (t in p.topics) {
                if (topicCount >= LIMIT) break
                if (t.title.lowercase().contains(query) || t.summary.lowercase().contains(query)) {
                    out.add(Hit("topic", t.id, t.title, t.summary))
                    topicCount++
                }
            }
        }

        notes.filter { n ->
            n.title.lowercase().contains(query) || n.body.lowercase().contains(query) ||
                n.tags.any { it.lowercase().contains(query) }
        }.take(LIMIT).forEach { n -> out.add(Hit("note", n.id.toString(), n.title.ifBlank { "Untitled" }, "Note")) }

        tasks.filter { t -> t.title.lowercase().contains(query) || t.notes.lowercase().contains(query) }
            .take(LIMIT).forEach { t -> out.add(Hit("task", t.id.toString(), t.title, "Task")) }

        projects.filter { p -> p.title.lowercase().contains(query) }
            .take(LIMIT).forEach { p -> out.add(Hit("project", p.id.toString(), p.title, "Project")) }

        meths.filter { m -> m.title.lowercase().contains(query) || m.steps.any { it.objective.lowercase().contains(query) } }
            .take(LIMIT).forEach { m -> out.add(Hit("methodology", m.id.toString(), m.title, "Methodology")) }

        return out
    }
}

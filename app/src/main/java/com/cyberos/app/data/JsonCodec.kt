package com.cyberos.app.data

import com.cyberos.app.challenges.ChallengeVerdict
import com.cyberos.app.flashcards.Flashcard
import com.cyberos.app.methodology.Methodology
import com.cyberos.app.methodology.MethodologyStep
import org.json.JSONArray
import org.json.JSONObject

object JsonCodec {

    fun notesToJson(notes: List<Note>): String {
        val a = JSONArray()
        notes.forEach { n ->
            a.put(JSONObject().apply {
                put("id", n.id); put("title", n.title); put("body", n.body)
                put("tags", JSONArray(n.tags)); put("createdAt", n.createdAt); put("updatedAt", n.updatedAt)
            })
        }
        return a.toString()
    }

    fun parseNotes(text: String): List<Note> {
        val a = JSONArray(text)
        val out = mutableListOf<Note>()
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            val tags = mutableListOf<String>()
            o.optJSONArray("tags")?.let { t -> for (j in 0 until t.length()) tags.add(t.optString(j)) }
            out.add(Note(o.getLong("id"), o.optString("title"), o.optString("body"), tags, o.optLong("createdAt"), o.optLong("updatedAt")))
        }
        return out
    }

    fun flashcardsToJson(cards: List<Flashcard>): String {
        val a = JSONArray()
        cards.forEach { c ->
            a.put(JSONObject().apply {
                put("id", c.id)
                if (c.topicId != null) put("topicId", c.topicId) else put("topicId", JSONObject.NULL)
                put("q", c.question); put("a", c.answer); put("ease", c.ease)
                put("interval", c.intervalDays); put("reps", c.reps)
                put("dueAt", c.dueAt); put("source", c.source)
            })
        }
        return a.toString()
    }

    fun parseFlashcards(text: String): List<Flashcard> {
        val a = JSONArray(text)
        val out = mutableListOf<Flashcard>()
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            out.add(Flashcard(o.getLong("id"),
                if (o.isNull("topicId")) null else o.optString("topicId"),
                o.optString("q"), o.optString("a"),
                o.optDouble("ease", 2.5), o.optDouble("interval", 0.0),
                o.optInt("reps", 0), o.optLong("dueAt", 0L), o.optString("source", "demo")))
        }
        return out
    }

    fun methodologiesToJson(ms: List<Methodology>): String {
        val a = JSONArray()
        ms.forEach { m ->
            a.put(JSONObject().apply {
                put("id", m.id); put("title", m.title)
                put("createdAt", m.createdAt); put("updatedAt", m.updatedAt)
                put("steps", JSONArray().apply {
                    m.steps.forEach { s ->
                        put(JSONObject().apply { put("id", s.id); put("objective", s.objective); put("done", s.done) })
                    }
                })
            })
        }
        return a.toString()
    }

    fun parseMethodologies(text: String): List<Methodology> {
        val a = JSONArray(text)
        val out = mutableListOf<Methodology>()
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            val steps = mutableListOf<MethodologyStep>()
            o.optJSONArray("steps")?.let { sa ->
                for (j in 0 until sa.length()) {
                    val so = sa.getJSONObject(j)
                    steps.add(MethodologyStep(so.getLong("id"), so.optString("objective"), so.optBoolean("done", false)))
                }
            }
            out.add(Methodology(o.getLong("id"), o.optString("title"), steps, o.optLong("createdAt"), o.optLong("updatedAt")))
        }
        return out
    }

    fun tasksToJson(ts: List<Task>): String {
        val a = JSONArray()
        ts.forEach { t ->
            a.put(JSONObject().apply {
                put("id", t.id); put("title", t.title); put("notes", t.notes)
                put("status", t.status); put("priority", t.priority)
                if (t.projectId != null) put("projectId", t.projectId) else put("projectId", JSONObject.NULL)
                if (t.dueDay != null) put("dueDay", t.dueDay) else put("dueDay", JSONObject.NULL)
                put("createdAt", t.createdAt); put("updatedAt", t.updatedAt)
                if (t.completedAt != null) put("completedAt", t.completedAt) else put("completedAt", JSONObject.NULL)
            })
        }
        return a.toString()
    }

    fun parseTasks(text: String): List<Task> {
        val a = JSONArray(text)
        val out = mutableListOf<Task>()
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            out.add(Task(o.getLong("id"), o.optString("title"), o.optString("notes"),
                o.optString("status", TaskFields.TODO), o.optString("priority", TaskFields.MEDIUM),
                if (o.isNull("projectId")) null else o.optLong("projectId"),
                if (o.isNull("dueDay")) null else o.optLong("dueDay"),
                o.optLong("createdAt"), o.optLong("updatedAt"),
                if (o.isNull("completedAt")) null else o.optLong("completedAt")))
        }
        return out
    }

    fun projectsToJson(ps: List<Project>): String {
        val a = JSONArray()
        ps.forEach { p ->
            a.put(JSONObject().apply {
                put("id", p.id); put("title", p.title); put("emoji", p.emoji); put("createdAt", p.createdAt)
            })
        }
        return a.toString()
    }

    fun parseProjects(text: String): List<Project> {
        val a = JSONArray(text)
        val out = mutableListOf<Project>()
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            out.add(Project(o.getLong("id"), o.optString("title"), o.optString("emoji", "📁"), o.optLong("createdAt")))
        }
        return out
    }

    fun parseGeneratedCards(text: String): List<Pair<String, String>> {
        val cleaned = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val a = JSONArray(cleaned)
        val out = mutableListOf<Pair<String, String>>()
        for (i in 0 until a.length()) {
            val o = a.getJSONObject(i)
            val q = o.optString("q").trim()
            val ans = o.optString("a").trim()
            if (q.isNotEmpty() && ans.isNotEmpty()) out.add(q to ans)
        }
        return out
    }

    fun parseChallengeVerdict(text: String): ChallengeVerdict {
        val cleaned = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val o = JSONObject(cleaned)
        val v = o.optString("verdict").lowercase()
        val verdict = when (v) { "correct" -> "correct"; "partial" -> "partial"; else -> "incorrect" }
        return ChallengeVerdict(verdict, o.optString("feedback").trim())
    }
}

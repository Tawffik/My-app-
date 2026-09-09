package com.cyberos.app.learning

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * User-defined learning paths/topics (additive to built-in curriculum).
 */
class CustomPathStore(context: Context) {
    private val file = File(context.filesDir, "custom_learning_paths.json")
    private var cache: MutableList<PathData> = load()

    fun all(): List<PathData> = cache.toList()

    @Synchronized
    fun addPath(title: String, description: String): PathData {
        val id = "custom-${System.currentTimeMillis()}"
        val path = PathData(id, title.trim(), description.trim(), emptyList())
        cache.add(path)
        persist()
        return path
    }

    @Synchronized
    fun addTopic(
        pathId: String,
        title: String,
        summary: String,
        body: String,
        resourceUrl: String = ""
    ): TopicData? {
        val idx = cache.indexOfFirst { it.id == pathId }
        if (idx < 0) return null
        val topicId = "custom-topic-${System.currentTimeMillis()}"
        val resources = if (resourceUrl.startsWith("http")) {
            listOf("Source" to resourceUrl.trim())
        } else emptyList()
        val topic = TopicData(
            id = topicId,
            title = title.trim(),
            summary = summary.trim().ifBlank { "Custom topic" },
            sections = listOf(
                TopicSection("Notes", body.trim().ifBlank { "Add your notes here." }),
                TopicSection("Apply", "Write one test idea and one flashcard from this topic.")
            ),
            related = emptyList(),
            flashcards = emptyList(),
            quiz = emptyList(),
            resources = resources
        )
        val old = cache[idx]
        cache[idx] = old.copy(topics = old.topics + topic)
        persist()
        return topic
    }

    private fun persist() {
        try {
            val arr = JSONArray()
            cache.forEach { path ->
                val topics = JSONArray()
                path.topics.forEach { t ->
                    val secs = JSONArray()
                    t.sections.forEach { s ->
                        secs.put(JSONObject().put("h", s.heading).put("b", s.body))
                    }
                    val res = JSONArray()
                    t.resources.forEach { (a, b) -> res.put(JSONObject().put("t", a).put("u", b)) }
                    topics.put(
                        JSONObject()
                            .put("id", t.id)
                            .put("title", t.title)
                            .put("summary", t.summary)
                            .put("sections", secs)
                            .put("resources", res)
                    )
                }
                arr.put(
                    JSONObject()
                        .put("id", path.id)
                        .put("title", path.title)
                        .put("description", path.description)
                        .put("topics", topics)
                )
            }
            file.writeText(arr.toString())
        } catch (_: Exception) {
        }
    }

    private fun load(): MutableList<PathData> {
        if (!file.exists()) return mutableListOf()
        return try {
            val arr = JSONArray(file.readText())
            val out = mutableListOf<PathData>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val topicsArr = o.optJSONArray("topics") ?: JSONArray()
                val topics = mutableListOf<TopicData>()
                for (j in 0 until topicsArr.length()) {
                    val tj = topicsArr.getJSONObject(j)
                    val secsArr = tj.optJSONArray("sections") ?: JSONArray()
                    val secs = mutableListOf<TopicSection>()
                    for (k in 0 until secsArr.length()) {
                        val s = secsArr.getJSONObject(k)
                        secs.add(TopicSection(s.optString("h"), s.optString("b")))
                    }
                    val resArr = tj.optJSONArray("resources") ?: JSONArray()
                    val res = mutableListOf<Pair<String, String>>()
                    for (k in 0 until resArr.length()) {
                        val r = resArr.getJSONObject(k)
                        res.add(r.optString("t") to r.optString("u"))
                    }
                    topics.add(
                        TopicData(
                            id = tj.optString("id"),
                            title = tj.optString("title"),
                            summary = tj.optString("summary"),
                            sections = secs,
                            related = emptyList(),
                            flashcards = emptyList(),
                            resources = res
                        )
                    )
                }
                out.add(
                    PathData(
                        o.optString("id"),
                        o.optString("title"),
                        o.optString("description"),
                        topics
                    )
                )
            }
            out
        } catch (_: Exception) {
            mutableListOf()
        }
    }
}

package com.cyberos.app.data

import android.content.Context
import android.net.Uri
import com.cyberos.app.flashcards.FlashcardStore
import com.cyberos.app.learning.ProgressStore
import com.cyberos.app.methodology.MethodologyStore
import org.json.JSONArray
import org.json.JSONObject

object Backup {

    const val VERSION = 2

    fun export(
        context: Context, uri: Uri,
        notes: NoteStore, cards: FlashcardStore,
        meths: MethodologyStore, progress: ProgressStore,
        tasks: TaskStore, projects: ProjectStore
    ): Boolean = try {
        val json = JSONObject().apply {
            put("app", "CyberOS")
            put("version", VERSION)
            put("exportedAt", System.currentTimeMillis())
            put("notes", JSONArray(notes.toJson()))
            put("flashcards", JSONArray(cards.toJson()))
            put("methodologies", JSONArray(meths.toJson()))
            put("progress", progress.toJson())
            put("tasks", JSONArray(tasks.toJson()))
            put("projects", JSONArray(projects.toJson()))
        }
        context.contentResolver.openOutputStream(uri)?.use { it.write(json.toString().toByteArray()) } != null
    } catch (e: Exception) { false }

    fun import(
        context: Context, uri: Uri,
        notes: NoteStore, cards: FlashcardStore,
        meths: MethodologyStore, progress: ProgressStore,
        tasks: TaskStore, projects: ProjectStore
    ): Boolean {
        return try {
            val text = context.contentResolver.openInputStream(uri)?.use { it.bufferedReader().readText() }
            val root = text?.let { JSONObject(it) }
            val v = root?.optInt("version", 1) ?: 0
            if (root != null && (v == 1 || v == 2)) {
                notes.replaceAll(root.optString("notes", "[]"))
                cards.replaceAll(root.optString("flashcards", "[]"))
                meths.replaceAll(root.optString("methodologies", "[]"))
                root.optJSONObject("progress")?.let { progress.restore(it) }
                if (v >= 2) {
                    tasks.replaceAll(root.optString("tasks", "[]"))
                    projects.replaceAll(root.optString("projects", "[]"))
                }
                true
            } else false
        } catch (e: Exception) { false }
    }
}

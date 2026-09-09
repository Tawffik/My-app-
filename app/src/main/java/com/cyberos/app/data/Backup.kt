package com.cyberos.app.data

import android.content.Context
import android.net.Uri
import com.cyberos.app.flashcards.FlashcardStore
import com.cyberos.app.learning.ProgressStore
import com.cyberos.app.methodology.MethodologyStore
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Full-device backup of CyberOS local stores.
 * v1–v2: notes, cards, methodologies, progress, tasks, projects
 * v3: + research items, bug bounty programs/findings/assets, custom learning paths, checklist progress
 */
object Backup {

    const val VERSION = 3

    private val EXTRA_FILES = listOf(
        "research_items.json",
        "bb_programs.json",
        "bb_findings.json",
        "bb_assets.json",
        "custom_learning_paths.json",
        "notes.json"
    )

    fun export(
        context: Context,
        uri: Uri,
        notes: NoteStore,
        cards: FlashcardStore,
        meths: MethodologyStore,
        progress: ProgressStore,
        tasks: TaskStore,
        projects: ProjectStore
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
            // Raw file snapshots for stores without dedicated replace APIs
            val files = JSONObject()
            EXTRA_FILES.forEach { name ->
                val f = File(context.filesDir, name)
                if (f.exists()) {
                    try {
                        files.put(name, f.readText())
                    } catch (_: Exception) {
                    }
                }
            }
            put("files", files)
            // Checklist progress lives in SharedPreferences
            try {
                val prefs = context.getSharedPreferences("bb_checklists", Context.MODE_PRIVATE)
                val prefObj = JSONObject()
                prefs.all.forEach { (k, v) -> prefObj.put(k, v.toString()) }
                put("prefs_bb_checklists", prefObj)
            } catch (_: Exception) {}
        }
        context.contentResolver.openOutputStream(uri)?.use {
            it.write(json.toString().toByteArray(Charsets.UTF_8))
        } != null
    } catch (_: Exception) {
        false
    }

    fun import(
        context: Context,
        uri: Uri,
        notes: NoteStore,
        cards: FlashcardStore,
        meths: MethodologyStore,
        progress: ProgressStore,
        tasks: TaskStore,
        projects: ProjectStore
    ): Boolean {
        return try {
            val text = context.contentResolver.openInputStream(uri)?.use { it.bufferedReader().readText() }
                ?: return false
            val root = JSONObject(text)
            val v = root.optInt("version", 1)
            if (v !in 1..3) return false

            notes.replaceAll(root.optString("notes", "[]"))
            cards.replaceAll(root.optString("flashcards", "[]"))
            meths.replaceAll(root.optString("methodologies", "[]"))
            root.optJSONObject("progress")?.let { progress.restore(it) }
            if (v >= 2) {
                tasks.replaceAll(root.optString("tasks", "[]"))
                projects.replaceAll(root.optString("projects", "[]"))
            }
            if (v >= 3) {
                root.optJSONObject("prefs_bb_checklists")?.let { prefObj ->
                    try {
                        val prefs = context.getSharedPreferences("bb_checklists", Context.MODE_PRIVATE).edit()
                        prefObj.keys().forEach { k ->
                            prefs.putString(k, prefObj.optString(k))
                        }
                        prefs.apply()
                    } catch (_: Exception) {}
                }
                val files = root.optJSONObject("files")
                if (files != null) {
                    EXTRA_FILES.forEach { name ->
                        if (files.has(name)) {
                            try {
                                File(context.filesDir, name).writeText(files.getString(name))
                            } catch (_: Exception) {
                            }
                        }
                    }
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}

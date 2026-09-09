package com.cyberos.app.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class BugBountyProgramStore(context: Context) {
    private val file = File(context.filesDir, "bb_programs.json")
    private var cache: MutableList<BugBountyProgram> = load()

    fun all(): List<BugBountyProgram> = cache.sortedByDescending { it.createdAt }
    fun get(id: Long): BugBountyProgram? = cache.firstOrNull { it.id == id }
    fun nextId(): Long = (cache.maxOfOrNull { it.id } ?: 0L) + 1L

    @Synchronized
    fun save(p: BugBountyProgram) {
        val i = cache.indexOfFirst { it.id == p.id }
        if (i >= 0) cache[i] = p else cache.add(p)
        persist()
    }

    @Synchronized
    fun delete(id: Long) {
        cache.removeAll { it.id == id }
        persist()
    }

    private fun persist() {
        try {
            val a = JSONArray()
            cache.forEach { p ->
                a.put(JSONObject().apply {
                    put("id", p.id); put("name", p.name); put("platform", p.platform)
                    put("url", p.url); put("scopeSummary", p.scopeSummary); put("notes", p.notes)
                    put("active", p.active); put("createdAt", p.createdAt)
                })
            }
            file.writeText(a.toString())
        } catch (_: Exception) {
        }
    }

    private fun load(): MutableList<BugBountyProgram> {
        if (!file.exists()) return mutableListOf()
        return try {
            val a = JSONArray(file.readText())
            val out = mutableListOf<BugBountyProgram>()
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                out.add(
                    BugBountyProgram(
                        o.getLong("id"),
                        o.optString("name"),
                        o.optString("platform"),
                        o.optString("url"),
                        o.optString("scopeSummary"),
                        o.optString("notes"),
                        o.optBoolean("active", true),
                        o.optLong("createdAt")
                    )
                )
            }
            out
        } catch (_: Exception) {
            mutableListOf()
        }
    }
}

class BugBountyFindingStore(context: Context) {
    private val file = File(context.filesDir, "bb_findings.json")
    private var cache: MutableList<BugBountyFinding> = load()

    fun all(): List<BugBountyFinding> = cache.sortedByDescending { it.updatedAt }
    fun get(id: Long): BugBountyFinding? = cache.firstOrNull { it.id == id }
    fun nextId(): Long = (cache.maxOfOrNull { it.id } ?: 0L) + 1L
    fun forProgram(programId: Long): List<BugBountyFinding> =
        cache.filter { it.programId == programId }.sortedByDescending { it.updatedAt }

    @Synchronized
    fun save(f: BugBountyFinding) {
        val i = cache.indexOfFirst { it.id == f.id }
        if (i >= 0) cache[i] = f else cache.add(f)
        persist()
    }

    @Synchronized
    fun delete(id: Long) {
        cache.removeAll { it.id == id }
        persist()
    }

    private fun persist() {
        try {
            val a = JSONArray()
            cache.forEach { f ->
                a.put(JSONObject().apply {
                    put("id", f.id); put("programId", f.programId); put("title", f.title)
                    put("vulnerabilityType", f.vulnerabilityType); put("severity", f.severity)
                    put("status", f.status); put("asset", f.asset); put("endpoint", f.endpoint)
                    put("description", f.description); put("stepsToReproduce", f.stepsToReproduce)
                    put("impact", f.impact); put("rootCause", f.rootCause)
                    put("remediation", f.remediation); put("evidenceNotes", f.evidenceNotes)
                    put("linkedResearchIds", JSONArray(f.linkedResearchIds))
                    put("createdAt", f.createdAt); put("updatedAt", f.updatedAt)
                })
            }
            file.writeText(a.toString())
        } catch (_: Exception) {
        }
    }

    private fun load(): MutableList<BugBountyFinding> {
        if (!file.exists()) return mutableListOf()
        return try {
            val a = JSONArray(file.readText())
            val out = mutableListOf<BugBountyFinding>()
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                val linked = mutableListOf<Long>()
                o.optJSONArray("linkedResearchIds")?.let { arr ->
                    for (j in 0 until arr.length()) linked.add(arr.optLong(j))
                }
                out.add(
                    BugBountyFinding(
                        o.getLong("id"),
                        o.optLong("programId"),
                        o.optString("title"),
                        o.optString("vulnerabilityType", "Other"),
                        o.optString("severity", "None"),
                        o.optString("status", "Idea"),
                        o.optString("asset"),
                        o.optString("endpoint"),
                        o.optString("description"),
                        o.optString("stepsToReproduce"),
                        o.optString("impact"),
                        o.optString("rootCause"),
                        o.optString("remediation"),
                        o.optString("evidenceNotes"),
                        linked,
                        o.optLong("createdAt"),
                        o.optLong("updatedAt")
                    )
                )
            }
            out
        } catch (_: Exception) {
            mutableListOf()
        }
    }
}

class BugBountyState(
    private val programStore: BugBountyProgramStore,
    private val findingStore: BugBountyFindingStore,
    private val assetStore: BugBountyAssetStore
) {
    var programs by mutableStateOf(programStore.all())
        private set
    var findings by mutableStateOf(findingStore.all())
        private set
    var assets by mutableStateOf(assetStore.all())
        private set
    var statusFilter by mutableStateOf("All")
    var vulnFilter by mutableStateOf("All")
    var programFilter by mutableStateOf(0L) // 0 = all programs

    fun refresh() {
        programs = programStore.all()
        findings = findingStore.all()
        assets = assetStore.all()
    }

    fun upsertProgram(
        id: Long,
        name: String,
        platform: String,
        url: String,
        scope: String,
        notes: String,
        active: Boolean
    ): Long {
        val realId = if (id > 0) id else programStore.nextId()
        val existing = if (id > 0) programStore.get(id) else null
        programStore.save(
            BugBountyProgram(
                id = realId,
                name = name.trim(),
                platform = platform.trim(),
                url = url.trim(),
                scopeSummary = scope.trim(),
                notes = notes.trim(),
                active = active,
                createdAt = existing?.createdAt ?: System.currentTimeMillis()
            )
        )
        refresh()
        return realId
    }

    fun deleteProgram(id: Long) {
        programStore.delete(id)
        refresh()
    }

    fun upsertFinding(finding: BugBountyFinding): Long {
        val realId = if (finding.id > 0) finding.id else findingStore.nextId()
        val now = System.currentTimeMillis()
        val existing = if (finding.id > 0) findingStore.get(finding.id) else null
        findingStore.save(
            finding.copy(
                id = realId,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now
            )
        )
        refresh()
        return realId
    }

    fun deleteFinding(id: Long) {
        findingStore.delete(id)
        refresh()
    }

    fun getFinding(id: Long): BugBountyFinding? = findingStore.get(id)
    fun getProgram(id: Long): BugBountyProgram? = programStore.get(id)

    fun filteredFindings(): List<BugBountyFinding> {
        var list = findings
        if (programFilter > 0L) list = list.filter { it.programId == programFilter }
        if (statusFilter == "Needs action") {
            list = list.filter { FindingNextAction.needsActionToday(it.status) }
        } else if (statusFilter != "All") {
            list = list.filter { it.status == statusFilter }
        }
        if (vulnFilter != "All") list = list.filter { it.vulnerabilityType.equals(vulnFilter, true) }
        return list
    }

    fun programName(id: Long): String =
        if (id <= 0L) "" else programs.firstOrNull { it.id == id }?.name ?: ""


    fun assetsFor(programId: Long): List<BugBountyAsset> = assetStore.forProgram(programId)

    fun upsertAsset(
        id: Long,
        programId: Long,
        value: String,
        type: String,
        notes: String,
        inScope: Boolean
    ): Long {
        val realId = if (id > 0) id else assetStore.nextId()
        val existing = if (id > 0) assetStore.get(id) else null
        assetStore.save(
            BugBountyAsset(
                id = realId,
                programId = programId,
                value = value.trim(),
                type = type,
                notes = notes.trim(),
                inScope = inScope,
                createdAt = existing?.createdAt ?: System.currentTimeMillis()
            )
        )
        refresh()
        return realId
    }

    fun deleteAsset(id: Long) {
        assetStore.delete(id)
        refresh()
    }

    fun reportMarkdown(findingId: Long): String {
        val f = findingStore.get(findingId) ?: return ""
        return BugBountyReportGenerator.toMarkdown(f, programName(f.programId))
    }

    companion object {
        val STATUS_FILTERS = listOf("All") + FindingStatuses.ALL
        val PLATFORMS = listOf(
            "HackerOne", "Bugcrowd", "Intigriti", "YesWeHack",
            "Synack", "Private", "VDP", "Other"
        )
    }
}

class BugBountyAssetStore(context: Context) {
    private val file = File(context.filesDir, "bb_assets.json")
    private var cache: MutableList<BugBountyAsset> = load()

    fun all(): List<BugBountyAsset> = cache.sortedByDescending { it.createdAt }
    fun forProgram(programId: Long): List<BugBountyAsset> =
        cache.filter { it.programId == programId }.sortedBy { it.value.lowercase() }
    fun get(id: Long): BugBountyAsset? = cache.firstOrNull { it.id == id }
    fun nextId(): Long = (cache.maxOfOrNull { it.id } ?: 0L) + 1L

    @Synchronized
    fun save(a: BugBountyAsset) {
        val i = cache.indexOfFirst { it.id == a.id }
        if (i >= 0) cache[i] = a else cache.add(a)
        persist()
    }

    @Synchronized
    fun delete(id: Long) {
        cache.removeAll { it.id == id }
        persist()
    }

    private fun persist() {
        try {
            val arr = JSONArray()
            cache.forEach { a ->
                arr.put(JSONObject().apply {
                    put("id", a.id); put("programId", a.programId); put("value", a.value)
                    put("type", a.type); put("notes", a.notes); put("inScope", a.inScope)
                    put("createdAt", a.createdAt)
                })
            }
            file.writeText(arr.toString())
        } catch (_: Exception) {
        }
    }

    private fun load(): MutableList<BugBountyAsset> {
        if (!file.exists()) return mutableListOf()
        return try {
            val arr = JSONArray(file.readText())
            val out = mutableListOf<BugBountyAsset>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(
                    BugBountyAsset(
                        o.getLong("id"),
                        o.optLong("programId"),
                        o.optString("value"),
                        o.optString("type", "Domain"),
                        o.optString("notes"),
                        o.optBoolean("inScope", true),
                        o.optLong("createdAt")
                    )
                )
            }
            out
        } catch (_: Exception) {
            mutableListOf()
        }
    }
}

/** Tracks checked items per checklist template (program-scoped optional). */
class ChecklistProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("bb_checklists", Context.MODE_PRIVATE)

    fun isChecked(templateId: String, itemId: String): Boolean =
        prefs.getBoolean("$templateId::$itemId", false)

    fun setChecked(templateId: String, itemId: String, checked: Boolean) {
        prefs.edit().putBoolean("$templateId::$itemId", checked).apply()
    }

    fun checkedCount(templateId: String, items: List<BugBountyChecklists.Item>): Int =
        items.count { isChecked(templateId, it.id) }
}

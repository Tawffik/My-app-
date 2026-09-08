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
    private val findingStore: BugBountyFindingStore
) {
    var programs by mutableStateOf(programStore.all())
        private set
    var findings by mutableStateOf(findingStore.all())
        private set
    var statusFilter by mutableStateOf("All")
    var vulnFilter by mutableStateOf("All")
    var programFilter by mutableStateOf(0L) // 0 = all programs

    fun refresh() {
        programs = programStore.all()
        findings = findingStore.all()
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
        if (statusFilter != "All") list = list.filter { it.status == statusFilter }
        if (vulnFilter != "All") list = list.filter { it.vulnerabilityType.equals(vulnFilter, true) }
        return list
    }

    fun programName(id: Long): String =
        if (id <= 0L) "" else programs.firstOrNull { it.id == id }?.name ?: ""

    companion object {
        val STATUS_FILTERS = listOf("All") + FindingStatuses.ALL
        val PLATFORMS = listOf(
            "HackerOne", "Bugcrowd", "Intigriti", "YesWeHack",
            "Synack", "Private", "VDP", "Other"
        )
    }
}

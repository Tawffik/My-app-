package com.cyberos.app.learning

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDate

class ProgressStore(context: Context) {

    private val file: File = File(context.filesDir, "progress.json")
    private val completed = mutableSetOf<String>()
    private val challengesDone = mutableSetOf<String>()
    private var xp = 0L
    private var streak = 0
    private var lastDay = ""
    private var focusSessions = 0
    private var focusMinutes = 0L
    private var lastWeekReviewDay = ""

    init { load() }

    fun xp(): Long = xp
    fun streak(): Int = streak
    fun completedCount(): Int = completed.size
    fun focusSessions(): Int = focusSessions
    fun focusMinutes(): Long = focusMinutes
    fun isCompleted(id: String) = completed.contains(id)
    fun isChallengeDone(id: String) = challengesDone.contains(id)
    fun challengesDoneCount(): Int = challengesDone.size

    fun completeTopic(id: String): Boolean {
        if (completed.contains(id)) return false
        completed.add(id)
        xp += 10
        persist()
        return true
    }

    fun uncompleteTopic(id: String) { if (completed.remove(id)) persist() }

    fun addXp(n: Long) { xp += n; persist() }

    fun completeChallenge(id: String, xpGain: Long): Boolean {
        if (challengesDone.contains(id)) return false
        challengesDone.add(id)
        xp += xpGain
        persist()
        return true
    }

    fun addFocusSession(minutes: Int) {
        focusSessions += 1
        focusMinutes += minutes
        xp += 10
        persist()
    }

    fun touchDay() {
        val today = LocalDate.now().toString()
        if (lastDay == today) return
        val yesterday = LocalDate.now().minusDays(1).toString()
        streak = if (lastDay == yesterday) streak + 1 else 1
        lastDay = today
        persist()
    }

    fun markWeekReviewSeen() { lastWeekReviewDay = LocalDate.now().toString(); persist() }

    fun shouldShowWeekReview(): Boolean {
        val today = LocalDate.now()
        return today.dayOfWeek == java.time.DayOfWeek.FRIDAY && lastWeekReviewDay != today.toString()
    }

    fun toJson(): JSONObject = JSONObject().apply {
        put("xp", xp); put("streak", streak); put("lastDay", lastDay)
        put("focusSessions", focusSessions); put("focusMinutes", focusMinutes)
        put("lastWeekReviewDay", lastWeekReviewDay)
        put("completed", JSONArray(completed))
        put("challengesDone", JSONArray(challengesDone))
    }

    fun restore(o: JSONObject) {
        xp = o.optLong("xp", 0)
        streak = o.optInt("streak", 0)
        lastDay = o.optString("lastDay", "")
        focusSessions = o.optInt("focusSessions", 0)
        focusMinutes = o.optLong("focusMinutes", 0)
        lastWeekReviewDay = o.optString("lastWeekReviewDay", "")
        completed.clear()
        o.optJSONArray("completed")?.let { arr -> for (i in 0 until arr.length()) completed.add(arr.optString(i)) }
        challengesDone.clear()
        o.optJSONArray("challengesDone")?.let { arr -> for (i in 0 until arr.length()) challengesDone.add(arr.optString(i)) }
        persist()
    }

    private fun persist() { try { file.writeText(toJson().toString()) } catch (_: Exception) { } }
    private fun load() {
        if (!file.exists()) return
        try { restore(JSONObject(file.readText())) } catch (_: Exception) { }
    }
}

class ProgressState(private val store: ProgressStore) {

    var xp by mutableStateOf(store.xp())
        private set
    var streak by mutableStateOf(store.streak())
        private set
    var completedCount by mutableStateOf(store.completedCount())
        private set
    var focusSessions by mutableStateOf(store.focusSessions())
        private set
    var focusMinutes by mutableStateOf(store.focusMinutes())
        private set
    var challengesDoneCount by mutableStateOf(store.challengesDoneCount())
        private set

    fun refresh() {
        xp = store.xp(); streak = store.streak(); completedCount = store.completedCount()
        focusSessions = store.focusSessions(); focusMinutes = store.focusMinutes()
        challengesDoneCount = store.challengesDoneCount()
    }

    fun isCompleted(id: String) = store.isCompleted(id)
    fun isChallengeDone(id: String) = store.isChallengeDone(id)
    fun completeTopic(id: String) { store.completeTopic(id); refresh() }
    fun uncompleteTopic(id: String) { store.uncompleteTopic(id); refresh() }
    fun completeChallenge(id: String, xpGain: Long): Boolean {
        val ok = store.completeChallenge(id, xpGain); refresh(); return ok
    }
    fun addXp(n: Long) { store.addXp(n); refresh() }
    fun addFocusSession(minutes: Int) { store.addFocusSession(minutes); refresh() }
    fun touchDay() { store.touchDay(); refresh() }
    fun shouldShowWeekReview() = store.shouldShowWeekReview()
    fun markWeekReviewSeen() { store.markWeekReviewSeen() }
}

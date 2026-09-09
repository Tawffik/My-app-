package com.cyberos.app.data

/**
 * Single next action per finding status — keeps BB work focused.
 */
object FindingNextAction {

    fun forStatus(status: String): String = when (status) {
        "Idea" -> "Define asset + hypothesis (authorized program only)"
        "Investigating" -> "Reproduce safely and note exact steps"
        "Potential" -> "Capture evidence (request/response, screenshots)"
        "Validated" -> "Write impact clearly with evidence"
        "Needs Evidence" -> "Collect missing proof before drafting report"
        "Draft" -> "Polish Markdown report and self-review"
        "Submitted" -> "Wait for triage; track platform status"
        "Triaged" -> "Respond to program questions; keep notes"
        "Resolved", "Closed", "Duplicate", "N/A" -> "Archive notes; extract lessons to vault"
        else -> "Clarify status, then pick one concrete next step"
    }

    fun needsActionToday(status: String): Boolean =
        status !in listOf("Submitted", "Triaged", "Resolved", "Closed", "Duplicate", "N/A")

    val ACTION_FILTER = "Needs action"
}

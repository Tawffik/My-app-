package com.cyberos.app.data

/**
 * Foundation models for Phase E — Bug Bounty Workspace.
 * Kept offline-first and independent from Research feed items.
 */

data class BugBountyProgram(
    val id: Long,
    val name: String,
    val platform: String = "", // HackerOne, Bugcrowd, Intigriti, ...
    val url: String = "",
    val scopeSummary: String = "",
    val notes: String = "",
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class BugBountyFinding(
    val id: Long,
    val programId: Long = 0L,
    val title: String,
    val vulnerabilityType: String = "Other",
    val severity: String = "None", // Critical, High, Medium, Low, None
    val status: String = "Idea",
    // Idea | Investigating | Potential | Validated | Needs Evidence |
    // Draft | Submitted | Triaged | Resolved | Closed | Duplicate | N/A
    val asset: String = "",
    val endpoint: String = "",
    val description: String = "",
    val stepsToReproduce: String = "",
    val impact: String = "",
    val rootCause: String = "",
    val remediation: String = "",
    val evidenceNotes: String = "",
    val linkedResearchIds: List<Long> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

object FindingStatuses {
    val ALL = listOf(
        "Idea", "Investigating", "Potential", "Validated", "Needs Evidence",
        "Draft", "Submitted", "Triaged", "Resolved", "Closed", "Duplicate", "N/A"
    )
}

object FindingSeverities {
    val ALL = listOf("Critical", "High", "Medium", "Low", "None")
}

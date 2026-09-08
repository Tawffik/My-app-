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

data class BugBountyAsset(
    val id: Long,
    val programId: Long,
    val value: String,
    val type: String = "Domain", // Domain | URL | IP | Mobile | API | Other
    val notes: String = "",
    val inScope: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

object AssetTypes {
    val ALL = listOf("Domain", "URL", "IP", "API", "Mobile", "Other")
}

/** Seeded methodology checklists for bug bounty testing. */
object BugBountyChecklists {
    data class Item(val id: String, val text: String)
    data class Template(val id: String, val title: String, val items: List<Item>)

    val TEMPLATES: List<Template> = listOf(
        Template(
            "web-app",
            "Web Application Checklist",
            listOf(
                Item("w1", "Identify technologies and versions"),
                Item("w2", "Map authentication flows"),
                Item("w3", "Map authorization boundaries"),
                Item("w4", "Enumerate API endpoints"),
                Item("w5", "Review exposed documentation"),
                Item("w6", "Identify input points"),
                Item("w7", "Test file upload"),
                Item("w8", "Review access control (IDOR/BAC)"),
                Item("w9", "Review business logic"),
                Item("w10", "Review client-side behavior"),
                Item("w11", "Review security headers"),
                Item("w12", "Review error handling / verbose errors")
            )
        ),
        Template(
            "recon",
            "Recon Checklist",
            listOf(
                Item("r1", "Asset mapping (domains / ASNs)"),
                Item("r2", "Subdomain enumeration"),
                Item("r3", "Live host probing"),
                Item("r4", "Port / service discovery"),
                Item("r5", "Technology fingerprinting"),
                Item("r6", "Content discovery"),
                Item("r7", "Parameter discovery"),
                Item("r8", "Third-party service mapping")
            )
        ),
        Template(
            "api",
            "API Security Checklist",
            listOf(
                Item("a1", "Map all endpoints and methods"),
                Item("a2", "Test authentication on each endpoint"),
                Item("a3", "Test object-level authorization (BOLA/IDOR)"),
                Item("a4", "Test function-level authorization"),
                Item("a5", "Mass assignment / extra fields"),
                Item("a6", "Rate limiting and abuse"),
                Item("a7", "Injection in parameters"),
                Item("a8", "GraphQL introspection / batching if applicable")
            )
        )
    )
}

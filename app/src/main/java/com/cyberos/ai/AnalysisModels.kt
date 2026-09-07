package com.cyberos.ai

enum class ContentType { WRITEUP, ADVISORY, TUTORIAL, NEWS, RESEARCH_PAPER, UNKNOWN }

enum class Severity { LOW, MEDIUM, HIGH, CRITICAL, NONE }

enum class ReportSource {
    HACKERONE, BUGCROWD, INTIGRITI, YESWEHACK,
    TWITTER, MEDIUM, REDDIT, RESEARCHER_BLOG, OTHER
}

data class AnalysisResult(
    val summary: String,
    val techniques: List<String>,
    val contentType: ContentType,
    val topics: List<String>,
    val confidence: Double
)

data class BugBountyReport(
    val id: String,
    val title: String,
    val url: String,
    val source: ReportSource,
    val vulnerabilityType: String,
    val severity: Severity,
    val reward: Int?,
    val programName: String,
    val excerpt: String,
    val publishedAt: String,
    val techniquesUsed: List<String>
)

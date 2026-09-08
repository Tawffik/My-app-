package com.cyberos.ai

import org.json.JSONArray
import org.json.JSONObject

class HackerOneCollector {

    fun parseHacktivity(json: String): List<BugBountyReport> {
        val out = mutableListOf<BugBountyReport>()
        val root = JSONObject(json)
        val nodes: JSONArray = root.optJSONArray("reports")
            ?: root.optJSONArray("data") ?: JSONArray()
        var i = 0
        while (i < nodes.length()) {
            val n = nodes.getJSONObject(i)
            val reward = if (n.has("reward")) n.optInt("reward", 0) else null
            out.add(
                BugBountyReport(
                    id = n.optString("id", "h1-" + i),
                    title = n.optString("title", "Untitled report"),
                    url = n.optString("url", ""),
                    source = ReportSource.HACKERONE,
                    vulnerabilityType = n.optString("weakness", "OTHER"),
                    severity = parseSeverity(n.optString("severity", "")),
                    reward = reward,
                    programName = n.optString("program", "unknown"),
                    excerpt = n.optString("excerpt", ""),
                    publishedAt = n.optString("published_at", ""),
                    techniquesUsed = splitTags(n.optString("tags", ""))
                )
            )
            i++
        }
        return out.sortedWith(compareByDescending<BugBountyReport> { it.reward ?: 0 })
    }

    fun highValue(reports: List<BugBountyReport>, threshold: Int): List<BugBountyReport> =
        reports.filter { (it.reward ?: 0) >= threshold }

    fun trending(reports: List<BugBountyReport>, take: Int): List<BugBountyReport> =
        reports.sortedWith(compareByDescending<BugBountyReport> { it.publishedAt }).take(take)

    private fun splitTags(raw: String): List<String> =
        if (raw.isBlank()) emptyList()
        else raw.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() }

    private fun parseSeverity(raw: String): Severity = when (raw.lowercase()) {
        "critical" -> Severity.CRITICAL
        "high" -> Severity.HIGH
        "medium" -> Severity.MEDIUM
        "low" -> Severity.LOW
        else -> Severity.NONE
    }
}

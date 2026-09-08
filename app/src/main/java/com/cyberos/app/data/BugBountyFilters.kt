package com.cyberos.app.data

/**
 * Pure logic for classifying bug bounty writeups by vulnerability type.
 * Used by Research feed filters and future Bug Bounty workspace.
 */
object BugBountyFilters {

    /** Canonical vulnerability types shown in the UI. */
    val VULN_TYPES: List<String> = listOf(
        "All",
        "XSS",
        "SQLi",
        "IDOR",
        "SSRF",
        "RCE",
        "Auth",
        "Access Control",
        "Business Logic",
        "Open Redirect",
        "CSRF",
        "XXE",
        "File Upload",
        "Info Disclosure",
        "Cloud Misconfig",
        "OAuth",
        "JWT",
        "GraphQL",
        "API",
        "Other"
    )

    private val RULES: List<Pair<String, List<String>>> = listOf(
        "XSS" to listOf(
            "xss", "cross-site scripting", "cross site scripting", "stored xss",
            "reflected xss", "dom xss", "csp bypass"
        ),
        "SQLi" to listOf(
            "sql injection", "sqli", "sql i ", "blind sql", "union select",
            "error-based sql", "time-based sql"
        ),
        "IDOR" to listOf(
            "idor", "insecure direct object", "broken object level", "bola"
        ),
        "SSRF" to listOf(
            "ssrf", "server-side request forgery", "server side request"
        ),
        "RCE" to listOf(
            "rce", "remote code execution", "command injection", "os command",
            "arbitrary code", "code execution"
        ),
        "OAuth" to listOf(
            "oauth", "openid", "redirect_uri", "authorization code"
        ),
        "JWT" to listOf(
            "jwt", "json web token", "algorithm confusion", "none algorithm"
        ),
        "Auth" to listOf(
            "authentication bypass", "auth bypass", "login bypass", "password reset",
            "account takeover", "ato ", "session fixation", "mfa bypass", "2fa bypass",
            "otp bypass", "broken authentication"
        ),
        "Access Control" to listOf(
            "broken access control", "privilege escalation", "vertical privilege",
            "horizontal privilege", "missing authorization", "unauthorized access",
            "access control", "rbac"
        ),
        "Business Logic" to listOf(
            "business logic", "logic flaw", "race condition", "price manipulation",
            "workflow bypass"
        ),
        "Open Redirect" to listOf(
            "open redirect", "open redirection", "unvalidated redirect"
        ),
        "CSRF" to listOf(
            "csrf", "cross-site request forgery", "cross site request forgery"
        ),
        "XXE" to listOf(
            "xxe", "xml external entity", "xml injection"
        ),
        "File Upload" to listOf(
            "file upload", "unrestricted upload", "malicious upload", "shell upload"
        ),
        "Info Disclosure" to listOf(
            "information disclosure", "info disclosure", "sensitive data exposure",
            "source code disclosure", "path disclosure", "debug endpoint"
        ),
        "Cloud Misconfig" to listOf(
            "s3 bucket", "cloud misconfig", "public bucket", "iam misconfig",
            "exposed secrets", "credential leak", "api key leak"
        ),
        "GraphQL" to listOf(
            "graphql", "introspection", "batching attack"
        ),
        "API" to listOf(
            "api security", "rest api", "endpoint", "mass assignment", "bola"
        )
    )

    /**
     * Detect the primary vulnerability type from title + summary text.
     * Returns one of [VULN_TYPES] excluding "All", or "Other".
     */
    fun detectVulnType(title: String, summary: String = ""): String {
        val text = (title + " " + summary).lowercase()
        for ((type, keywords) in RULES) {
            if (keywords.any { text.contains(it) }) return type
        }
        return "Other"
    }

    /**
     * Detect all matching vulnerability types (multi-label).
     */
    fun detectAllVulnTypes(title: String, summary: String = ""): List<String> {
        val text = (title + " " + summary).lowercase()
        val hits = RULES.filter { (_, keywords) -> keywords.any { text.contains(it) } }
            .map { it.first }
        return hits.ifEmpty { listOf("Other") }
    }

    /**
     * True when the item looks like a practical writeup / report rather than general news.
     */
    fun looksLikeWriteup(title: String, summary: String = ""): Boolean {
        val text = (title + " " + summary).lowercase()
        val signals = listOf(
            "writeup", "write-up", "write up", "how i found", "how i hacked",
            "bug bounty", "\$", "bounty", "disclosed", "poc", "proof of concept",
            "steps to reproduce", "vulnerability report", "hackerone", "bugcrowd",
            "intigriti", "yeswehack"
        )
        return signals.any { text.contains(it) }
    }

    fun filterByVulnType(items: List<ResearchItem>, vulnType: String): List<ResearchItem> {
        if (vulnType == "All" || vulnType.isBlank()) return items
        return items.filter { it.vulnerabilityType.equals(vulnType, ignoreCase = true) }
    }
}

package com.cyberos.app.data

object ResearchCategorizer {

    private val RULES: List<Pair<String, List<String>>> = listOf(
        "Bug Bounty" to listOf("bug bounty", "hackerone", "bugcrowd", "vdp"),
        "Web Security" to listOf("xss", "csrf", "sql injection", "web security"),
        "API Security" to listOf("api security", "rest api", "graphql", "openapi"),
        "Authentication" to listOf("authentication", "login", "mfa", "session fixation"),
        "Authorization" to listOf("authorization", "idor", "access control", "rbac"),
        "Cloud" to listOf("aws", "azure", "gcp", "cloud security", "s3 bucket"),
        "Mobile" to listOf("android", "ios", "mobile security", "apk"),
        "AI Security" to listOf("llm security", "prompt injection", "ai security", "jailbreak"),
        "Vulnerabilities" to listOf("vulnerability", "exploit", "poc"),
        "CVE" to listOf("cve-", "cve "),
        "Threat Intelligence" to listOf("threat actor", "apt", "malware campaign"),
        "Pentesting" to listOf("penetration test", "pentest", "red team"),
        "OSINT" to listOf("osint", "reconnaissance"),
        "Supply Chain" to listOf("supply chain", "dependency confusion", "npm package")
    )

    fun categorize(title: String, summary: String): String {
        val text = (title + " " + summary).lowercase()
        for ((category, keywords) in RULES) {
            if (keywords.any { text.contains(it) }) return category
        }
        return "General"
    }
}

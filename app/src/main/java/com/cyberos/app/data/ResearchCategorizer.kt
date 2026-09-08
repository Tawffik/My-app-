package com.cyberos.app.data

object ResearchCategorizer {

    private val RULES: List<Pair<String, List<String>>> = listOf(
        "Bug Bounty" to listOf(
            "bug bounty", "hackerone", "bugcrowd", "intigriti", "yeswehack", "vdp",
            "writeup", "write-up", "bounty writeup"
        ),
        "Web Security" to listOf("xss", "csrf", "sql injection", "web security", "ssrf", "xxe"),
        "API Security" to listOf("api security", "rest api", "graphql", "openapi", "bola"),
        "Authentication" to listOf(
            "authentication", "login", "mfa", "session fixation", "password reset",
            "account takeover", "oauth", "jwt"
        ),
        "Authorization" to listOf("authorization", "idor", "access control", "rbac", "privilege"),
        "Cloud" to listOf("aws", "azure", "gcp", "cloud security", "s3 bucket", "iam"),
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

    /** Build tags including vulnerability type when relevant. */
    fun buildTags(title: String, summary: String): List<String> {
        val tags = mutableListOf<String>()
        val vuln = BugBountyFilters.detectVulnType(title, summary)
        if (vuln != "Other") tags.add(vuln)
        if (BugBountyFilters.looksLikeWriteup(title, summary)) tags.add("writeup")
        return tags.distinct()
    }
}

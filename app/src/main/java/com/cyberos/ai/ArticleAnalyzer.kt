package com.cyberos.ai

class ArticleAnalyzer {

    private val techniqueKeywords: List<Pair<String, String>> = listOf(
        "cross-site scripting" to "XSS",
        "xss" to "XSS",
        "ssrf" to "SSRF",
        "request smuggling" to "HTTP Request Smuggling",
        "desync" to "HTTP Desync",
        "sql injection" to "SQLi",
        "rce" to "RCE",
        "remote code execution" to "RCE",
        "idor" to "IDOR",
        "bola" to "IDOR",
        "oauth" to "OAuth Abuse",
        "open redirect" to "Open Redirect",
        "use-after-free" to "UAF",
        "race condition" to "Race Condition",
        "privilege escalation" to "PrivEsc",
        "frida" to "Frida Instrumentation",
        "ssl pinning" to "SSL Unpinning",
        "webview" to "WebView Abuse",
        "path traversal" to "Path Traversal"
    )

    private val topicKeywords: List<String> = listOf(
        "android", "ios", "web", "cloud", "api",
        "proxy", "kernel", "ci/cd", "threat intel", "crypto"
    )

    fun analyze(title: String, body: String): AnalysisResult {
        val text = (title + " " + body).lowercase()
        val techniques = techniqueKeywords
            .filter { pair -> text.contains(pair.first) }
            .map { pair -> pair.second }
            .distinct()
            .take(6)
        val contentType = classifyType(text)
        val topics = topicKeywords.filter { t -> text.contains(t) }.take(4)
        val confidence = scoreConfidence(techniques.size, contentType)
        return AnalysisResult(
            summary = summarize(title, body),
            techniques = techniques,
            contentType = contentType,
            topics = topics,
            confidence = confidence
        )
    }

    private fun classifyType(text: String): ContentType = when {
        text.contains("cve-") || text.contains("advisory") || text.contains("bulletin") -> ContentType.ADVISORY
        text.contains("writeup") || text.contains("write-up") || text.contains("hacktivity") -> ContentType.WRITEUP
        text.contains("tutorial") || text.contains("how to") || text.contains("guide") -> ContentType.TUTORIAL
        text.contains("research") || text.contains("paper") || text.contains("in-the-wild") -> ContentType.RESEARCH_PAPER
        text.contains("news") || text.contains("announc") -> ContentType.NEWS
        else -> ContentType.UNKNOWN
    }

    private fun summarize(title: String, body: String): String {
        val words = body.split(' ').filter { w -> w.isNotBlank() }
        if (words.isEmpty()) return title
        val head = words.take(28).joinToString(" ")
        return if (head.endsWith(".")) head else head + "."
    }

    private fun scoreConfidence(techniqueCount: Int, type: ContentType): Double {
        var score = 0.5 + techniqueCount * 0.08
        if (type != ContentType.UNKNOWN) score += 0.15
        return minOf(0.97, score)
    }
}

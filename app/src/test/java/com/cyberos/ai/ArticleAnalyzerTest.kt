package com.cyberos.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ArticleAnalyzerTest {

    private val analyzer = ArticleAnalyzer()

    @Test
    fun detectsSsrfAndClassifiesTutorial() {
        val r = analyzer.analyze(
            "SSRF deep-dive: from metadata to pivoting",
            "A tutorial on blind SSRF. We reach the cloud metadata endpoint and pivot to internal services."
        )
        assertTrue(r.techniques.contains("SSRF"))
        assertEquals(ContentType.TUTORIAL, r.contentType)
        assertTrue(r.confidence in 0.5..0.97)
    }

    @Test
    fun detectsAdvisoryByCve() {
        val r = analyzer.analyze(
            "CVE-2025-3692 WebView use-after-free",
            "Security bulletin for a WebView UAF with active exploitation."
        )
        assertEquals(ContentType.ADVISORY, r.contentType)
        assertTrue(r.techniques.contains("UAF"))
    }

    @Test
    fun emptyBodyFallsBackToTitle() {
        val r = analyzer.analyze("Open redirect via OAuth state", "")
        assertEquals("Open redirect via OAuth state", r.summary)
        assertEquals(ContentType.UNKNOWN, r.contentType)
    }
}

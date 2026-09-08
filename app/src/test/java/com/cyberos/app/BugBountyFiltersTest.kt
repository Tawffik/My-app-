package com.cyberos.app

import com.cyberos.app.data.BugBountyFilters
import com.cyberos.app.data.DailyWriteupParser
import com.cyberos.app.data.ResearchItem
import org.junit.Assert.*
import org.junit.Test

class BugBountyFiltersTest {

    @Test
    fun detects_xss() {
        assertEquals("XSS", BugBountyFilters.detectVulnType("Stored XSS in profile page", ""))
        assertEquals("XSS", BugBountyFilters.detectVulnType("Reflected Cross-Site Scripting", "payload"))
    }

    @Test
    fun detects_sqli() {
        assertEquals("SQLi", BugBountyFilters.detectVulnType("SQL injection in search", "union select"))
    }

    @Test
    fun detects_idor() {
        assertEquals("IDOR", BugBountyFilters.detectVulnType("From IDOR to Admin", "insecure direct object"))
    }

    @Test
    fun detects_ssrf() {
        assertEquals("SSRF", BugBountyFilters.detectVulnType("Blind SSRF via PDF renderer", ""))
    }

    @Test
    fun detects_oauth() {
        assertEquals("OAuth", BugBountyFilters.detectVulnType("OAuth redirect_uri Account Takeover", ""))
    }

    @Test
    fun detects_auth_bypass() {
        assertEquals("Auth", BugBountyFilters.detectVulnType("Password Reset Vulnerability to Full Account Takeover", ""))
    }

    @Test
    fun unknown_is_other() {
        assertEquals("Other", BugBountyFilters.detectVulnType("Random tech news", "nothing relevant"))
    }

    @Test
    fun looks_like_writeup() {
        assertTrue(BugBountyFilters.looksLikeWriteup("How I found a \$5000 bug", "bug bounty writeup"))
        assertFalse(BugBountyFilters.looksLikeWriteup("Weather forecast today", "sunny"))
    }

    @Test
    fun filter_by_vuln_type() {
        val items = listOf(
            ResearchItem(1, 1, "XSS writeup", "http://a", vulnerabilityType = "XSS"),
            ResearchItem(2, 1, "IDOR writeup", "http://b", vulnerabilityType = "IDOR"),
            ResearchItem(3, 1, "XSS again", "http://c", vulnerabilityType = "XSS")
        )
        val filtered = BugBountyFilters.filterByVulnType(items, "XSS")
        assertEquals(2, filtered.size)
        assertEquals(3, BugBountyFilters.filterByVulnType(items, "All").size)
    }

    @Test
    fun multi_label_detection() {
        val types = BugBountyFilters.detectAllVulnTypes("OAuth JWT confusion leading to IDOR", "")
        assertTrue(types.contains("OAuth") || types.contains("JWT") || types.contains("IDOR"))
    }
}

class DailyWriteupParserTest {

    @Test
    fun parses_securitycipher_readme_lines() {
        val md = """
            # Daily Bug Bounty Writeups
            - 💯September 7, 2026 - [Breaking Login with Google](https://medium.com/@t4nv1/example)
            - 💯September 6, 2026 - [From IDOR to Admin](https://example.com/idor)
            - junk line without link
        """.trimIndent()
        val parsed = DailyWriteupParser.parseReadme(md)
        assertEquals(2, parsed.size)
        assertEquals("Breaking Login with Google", parsed[0].title)
        assertTrue(parsed[0].link.contains("medium.com"))
        assertEquals("From IDOR to Admin", parsed[1].title)
        assertTrue(parsed[0].publishedAt > 0L)
    }

    @Test
    fun decodes_html_entities() {
        val md = """- 💯January 1, 2026 - [Who is a Hucker&lpar;hacker&rpar;](https://example.com/x)"""
        val parsed = DailyWriteupParser.parseReadme(md)
        assertEquals(1, parsed.size)
        assertEquals("Who is a Hucker(hacker)", parsed[0].title)
    }
}

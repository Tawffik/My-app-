package com.cyberos.app

import com.cyberos.app.data.ResearchCategorizer
import org.junit.Assert.*
import org.junit.Test

class ResearchCategorizerTest {

    @Test
    fun xss_is_web_security_or_bug_bounty() {
        val cat = ResearchCategorizer.categorize("New XSS technique found", "A stored XSS vulnerability.")
        assertTrue(cat == "Web Security" || cat == "Bug Bounty")
    }

    @Test
    fun s3_is_cloud() {
        assertEquals("Cloud", ResearchCategorizer.categorize("Cloud S3 bucket misconfiguration", "AWS S3 bucket exposed publicly."))
    }

    @Test
    fun idor_writeup_is_bug_bounty() {
        assertEquals(
            "Bug Bounty",
            ResearchCategorizer.categorize("IDOR bug bounty writeup", "An IDOR vulnerability report.")
        )
    }

    @Test
    fun unknown_is_general() {
        assertEquals("General", ResearchCategorizer.categorize("Random blog post", "Nothing security related here."))
    }

    @Test
    fun case_insensitive() {
        val cat = ResearchCategorizer.categorize("XSS ATTACK", "")
        assertTrue(cat == "Web Security" || cat == "Bug Bounty")
    }

    @Test
    fun tags_include_vuln_type() {
        val tags = ResearchCategorizer.buildTags("Stored XSS on profile", "reflected xss demo")
        assertTrue(tags.contains("XSS"))
    }
}

package com.cyberos.app

import com.cyberos.app.data.ResearchCategorizer
import org.junit.Assert.*
import org.junit.Test

class ResearchCategorizerTest {

    @Test fun xss_is_web_security() {
        assertEquals("Web Security", ResearchCategorizer.categorize("New XSS technique found", "A stored XSS vulnerability."))
    }

    @Test fun s3_is_cloud() {
        assertEquals("Cloud", ResearchCategorizer.categorize("Cloud S3 bucket misconfiguration", "AWS S3 bucket exposed publicly."))
    }

    @Test fun idor_is_bug_bounty() {
        assertEquals("Bug Bounty", ResearchCategorizer.categorize("IDOR bug bounty writeup", "An IDOR vulnerability report."))
    }

    @Test fun unknown_is_general() {
        assertEquals("General", ResearchCategorizer.categorize("Random blog post", "Nothing security related here."))
    }

    @Test fun case_insensitive() {
        assertEquals("Web Security", ResearchCategorizer.categorize("XSS ATTACK", ""))
    }
}

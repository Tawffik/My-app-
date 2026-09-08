package com.cyberos.app

import com.cyberos.app.data.BugBountyFinding
import com.cyberos.app.data.BugBountyReportGenerator
import org.junit.Assert.*
import org.junit.Test

class BugBountyReportGeneratorTest {
    @Test
    fun markdown_contains_core_sections() {
        val f = BugBountyFinding(
            id = 1,
            title = "IDOR on /api/profile",
            vulnerabilityType = "IDOR",
            severity = "High",
            status = "Draft",
            endpoint = "/api/profile",
            description = "Object id not authorized",
            stepsToReproduce = "1. Login\n2. Change id",
            impact = "Read other profiles",
            remediation = "Check ownership"
        )
        val md = BugBountyReportGenerator.toMarkdown(f, "Acme BB")
        assertTrue(md.contains("# IDOR on /api/profile"))
        assertTrue(md.contains("**Program:** Acme BB"))
        assertTrue(md.contains("## Steps to Reproduce"))
        assertTrue(md.contains("## Impact"))
        assertTrue(md.contains("CyberOS"))
    }
}

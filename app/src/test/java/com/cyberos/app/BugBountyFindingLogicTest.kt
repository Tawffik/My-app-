package com.cyberos.app

import com.cyberos.app.data.BugBountyFinding
import com.cyberos.app.data.FindingSeverities
import com.cyberos.app.data.FindingStatuses
import org.junit.Assert.*
import org.junit.Test

class BugBountyFindingLogicTest {

    @Test
    fun status_list_contains_workflow_steps() {
        assertTrue(FindingStatuses.ALL.contains("Idea"))
        assertTrue(FindingStatuses.ALL.contains("Investigating"))
        assertTrue(FindingStatuses.ALL.contains("Validated"))
        assertTrue(FindingStatuses.ALL.contains("Submitted"))
    }

    @Test
    fun severity_list_ordered() {
        assertEquals(listOf("Critical", "High", "Medium", "Low", "None"), FindingSeverities.ALL)
    }

    @Test
    fun filter_findings_by_status_and_vuln() {
        val items = listOf(
            BugBountyFinding(1, 1, "XSS on search", vulnerabilityType = "XSS", status = "Idea"),
            BugBountyFinding(2, 1, "IDOR profile", vulnerabilityType = "IDOR", status = "Validated"),
            BugBountyFinding(3, 2, "XSS stored", vulnerabilityType = "XSS", status = "Validated")
        )
        val validatedXss = items.filter { it.status == "Validated" && it.vulnerabilityType == "XSS" }
        assertEquals(1, validatedXss.size)
        assertEquals(3L, validatedXss[0].id)
    }

    @Test
    fun linked_research_ids_preserved() {
        val f = BugBountyFinding(1, 0, "From writeup", linkedResearchIds = listOf(10L, 20L))
        assertEquals(listOf(10L, 20L), f.linkedResearchIds)
    }
}

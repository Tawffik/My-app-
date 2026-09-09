package com.cyberos.app

import com.cyberos.app.data.Note
import com.cyberos.app.data.WikiLinks
import org.junit.Assert.*
import org.junit.Test

class WikiLinksTest {
    @Test
    fun extract_and_backlinks() {
        val a = Note(1, "IDOR", "See [[XSS]] and #web", emptyList(), 1, 1)
        val b = Note(2, "XSS", "Root", emptyList(), 1, 1)
        assertEquals(listOf("XSS"), WikiLinks.extractTargets(a.body))
        assertTrue(WikiLinks.extractInlineTags(a.body).contains("web"))
        assertEquals(1, WikiLinks.backlinksTo("XSS", listOf(a, b)).size)
        assertEquals(1, WikiLinks.graphEdges(listOf(a, b)).size)
    }
}

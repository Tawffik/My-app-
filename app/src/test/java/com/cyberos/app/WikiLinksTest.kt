package com.cyberos.app

import com.cyberos.app.data.Note
import com.cyberos.app.data.WikiLinks
import org.junit.Assert.*
import org.junit.Test

class WikiLinksTest {
    private val notes = listOf(
        Note(1, "JWT", "[[OAuth]]"),
        Note(2, "OAuth", "x"),
        Note(3, "R", "[[jwt]]")
    )
    @Test fun extracts() { assertEquals(listOf("JWT","OAuth"), WikiLinks.extractTargets("[[JWT]] [[OAuth]]")) }
    @Test fun distinct() { assertEquals(listOf("JWT"), WikiLinks.extractTargets("[[JWT]] [[JWT]]")) }
    @Test fun unclosed() { assertEquals(emptyList<String>(), WikiLinks.extractTargets("[[open")) }
    @Test fun strip() { assertEquals("a OAuth", WikiLinks.stripForPreview("a [[OAuth]]")) }
    @Test fun backlinks() { assertEquals(1L, WikiLinks.backlinksTo("OAuth", notes).first().id) }
    @Test fun case_insensitive() { assertTrue(WikiLinks.backlinksTo("JWT", notes).any { it.id == 3L }) }
    @Test fun self_excluded() { assertEquals(0, WikiLinks.backlinksTo("X", listOf(Note(9,"X","[[X]]")), 9L).size) }
    @Test fun resolve() { assertEquals(2L, WikiLinks.resolveTarget("oauth", notes)!!.id) }
    @Test fun resolve_null() { assertNull(WikiLinks.resolveTarget("k8s", notes)) }
}

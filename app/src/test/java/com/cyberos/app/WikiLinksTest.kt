package com.cyberos.app

import com.cyberos.app.data.Note
import com.cyberos.app.data.WikiLinks
import org.junit.Assert.*
import org.junit.Test

class WikiLinksTest {
    @Test
    fun extract_links_and_tags() {
        val body = "See [[IDOR Basics]] and #bug-bounty #lab"
        assertEquals(listOf("IDOR Basics"), WikiLinks.extractTargets(body))
        assertTrue(WikiLinks.extractInlineTags(body).contains("bug-bounty"))
        val tags = WikiLinks.mergeTags(listOf("security"), body)
        assertTrue(tags.contains("security"))
        assertTrue(tags.contains("lab"))
    }

    @Test
    fun backlinks() {
        val a = Note(1, "IDOR Basics", "root", emptyList(), 1, 1)
        val b = Note(2, "Lab 1", "Linked [[IDOR Basics]] here", emptyList(), 1, 1)
        val backs = WikiLinks.backlinksTo("IDOR Basics", listOf(a, b), 1)
        assertEquals(1, backs.size)
        assertEquals(2L, backs[0].id)
    }
}

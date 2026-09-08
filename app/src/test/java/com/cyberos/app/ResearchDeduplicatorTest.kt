package com.cyberos.app

import com.cyberos.app.data.ResearchDeduplicator
import com.cyberos.app.data.ResearchItem
import org.junit.Assert.*
import org.junit.Test

class ResearchDeduplicatorTest {

    @Test fun removes_duplicate_titles() {
        val items = listOf(
            ResearchItem(1, 1, "Same Title Here", "https://a.com/x"),
            ResearchItem(2, 1, "Same Title Here", "https://a.com/x-dup"),
            ResearchItem(3, 1, "Different Title", "https://a.com/y")
        )
        assertEquals(2, ResearchDeduplicator.dedupe(items).size)
    }

    @Test fun skips_already_existing() {
        val existing = listOf(ResearchItem(1, 1, "Known Title", "https://a.com/known"))
        val incoming = listOf(
            ResearchItem(2, 1, "Known Title", "https://a.com/known-2"),
            ResearchItem(3, 1, "Fresh Title", "https://a.com/fresh")
        )
        val result = ResearchDeduplicator.dedupe(incoming, existing)
        assertEquals(1, result.size)
        assertEquals("Fresh Title", result.first().title)
    }

    @Test fun blank_title_falls_back_to_link() {
        val a = ResearchDeduplicator.normalize("", "https://example.com/page/")
        val b = ResearchDeduplicator.normalize("", "https://example.com/page")
        assertEquals(a, b)
    }
}

package com.cyberos.app

import com.cyberos.app.data.ShareTextParser
import org.junit.Assert.*
import org.junit.Test

class ShareTextParserTest {

    @Test fun extracts_url_from_text() {
        val r = ShareTextParser.parse("Check this out: https://example.com/article Great read")
        assertEquals("https://example.com/article", r.url)
    }

    @Test fun title_excludes_url() {
        val r = ShareTextParser.parse("Check this out: https://example.com/article Great read")
        assertFalse(r.title.contains("https://"))
        assertTrue(r.title.contains("Check this out"))
    }

    @Test fun subject_overrides_title() {
        val r = ShareTextParser.parse("https://example.com/x", "Custom Subject")
        assertEquals("Custom Subject", r.title)
    }

    @Test fun url_only_falls_back_to_url_as_title() {
        val r = ShareTextParser.parse("https://example.com/only-link")
        assertEquals("https://example.com/only-link", r.title)
        assertEquals("https://example.com/only-link", r.url)
    }

    @Test fun no_url_returns_blank_url() {
        val r = ShareTextParser.parse("just some text with no link")
        assertEquals("", r.url)
    }

    @Test fun trims_trailing_punctuation_from_url() {
        val r = ShareTextParser.parse("Look at this (https://example.com/page).")
        assertEquals("https://example.com/page", r.url)
    }

    @Test fun blank_input_returns_untitled() {
        val r = ShareTextParser.parse("")
        assertEquals("Untitled", r.title)
        assertEquals("", r.url)
    }
}

package com.cyberos.app

import com.cyberos.app.data.CuratedWriteupLibrary
import com.cyberos.app.data.DailyWriteupParser
import org.junit.Assert.*
import org.junit.Test

class WriteupLibraryTest {
    @Test
    fun curated_has_writeups_and_tips() {
        assertTrue(CuratedWriteupLibrary.WRITEUPS.size >= 30)
        assertTrue(CuratedWriteupLibrary.WRITEUPS.any { it.category == "AI Security" })
        assertTrue(CuratedWriteupLibrary.WRITEUPS.any { it.category == "Bug Bounty" })
        assertTrue(CuratedWriteupLibrary.WRITEUPS.any { it.category == "Tips" })
        assertTrue(CuratedWriteupLibrary.CHANNEL_HINTS.size >= 5)
    }

    @Test
    fun parse_generic_markdown_links() {
        val md = """
            # List
            - [XSS without semicolon](https://blog.huli.tw/xss)
            - [PortSwigger research](https://portswigger.net/research/x)
            """.trimIndent()
        val parsed = DailyWriteupParser.parseGenericMarkdownLinks(md)
        assertEquals(2, parsed.size)
        assertTrue(parsed[0].link.startsWith("https://"))
    }

    @Test
    fun parse_plain_url_list() {
        val text = """
            https://hackerone.com/reports/1
            https://hackerone.com/reports/2
            """.trimIndent()
        val parsed = DailyWriteupParser.parsePlainUrlList(text)
        assertEquals(2, parsed.size)
    }
}

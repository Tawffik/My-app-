package com.cyberos.app

import com.cyberos.app.data.RssAtomParser
import org.junit.Assert.*
import org.junit.Test

class RssAtomParserTest {

    private val rssSample = """
        <?xml version="1.0"?>
        <rss version="2.0">
        <channel>
          <title>Test Feed</title>
          <item>
            <title>New XSS technique found</title>
            <link>https://example.com/xss-1</link>
            <pubDate>Wed, 02 Jan 2024 10:00:00 GMT</pubDate>
            <description>A stored XSS vulnerability was discovered.</description>
          </item>
          <item>
            <title>Cloud S3 bucket misconfiguration</title>
            <link>https://example.com/s3-1</link>
            <pubDate>Wed, 03 Jan 2024 10:00:00 GMT</pubDate>
            <description>AWS S3 bucket exposed publicly.</description>
          </item>
        </channel>
        </rss>
    """.trimIndent()

    private val atomSample = """
        <?xml version="1.0" encoding="utf-8"?>
        <feed xmlns="http://www.w3.org/2005/Atom">
          <title>Atom Test</title>
          <entry>
            <title>IDOR bug bounty writeup</title>
            <link href="https://example.com/idor-1"/>
            <updated>2024-01-05T10:00:00Z</updated>
            <summary>An IDOR vulnerability report.</summary>
          </entry>
        </feed>
    """.trimIndent()

    @Test fun rss_count() { assertEquals(2, RssAtomParser.parse(rssSample).size) }

    @Test fun rss_fields() {
        val item = RssAtomParser.parse(rssSample).first()
        assertEquals("New XSS technique found", item.title)
        assertEquals("https://example.com/xss-1", item.link)
        assertTrue(item.publishedAt > 0L)
    }

    @Test fun atom_count() { assertEquals(1, RssAtomParser.parse(atomSample).size) }

    @Test fun atom_fields() {
        val item = RssAtomParser.parse(atomSample).first()
        assertEquals("IDOR bug bounty writeup", item.title)
        assertEquals("https://example.com/idor-1", item.link)
        assertTrue(item.publishedAt > 0L)
    }

    @Test fun malformed_returns_empty() {
        assertTrue(RssAtomParser.parse("not xml at all").isEmpty())
    }

    @Test fun empty_returns_empty() {
        assertTrue(RssAtomParser.parse("").isEmpty())
    }
}

package com.cyberos.app.data

import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

object RssAtomParser {

    data class ParsedItem(
        val title: String,
        val link: String,
        val author: String,
        val publishedAt: Long,
        val summary: String
    )

    fun parse(xml: String): List<ParsedItem> {
        return try {
            val factory = DocumentBuilderFactory.newInstance()
            factory.isNamespaceAware = false
            val builder = factory.newDocumentBuilder()
            val doc = builder.parse(InputSource(StringReader(xml)))
            doc.documentElement?.normalize()

            val rssItems = doc.getElementsByTagName("item")
            if (rssItems.length > 0) {
                parseRss(rssItems)
            } else {
                val atomEntries = doc.getElementsByTagName("entry")
                parseAtom(atomEntries)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun parseRss(items: NodeList): List<ParsedItem> {
        val out = mutableListOf<ParsedItem>()
        for (i in 0 until items.length) {
            val el = items.item(i) as? Element ?: continue
            val title = text(el, "title")
            val link = text(el, "link")
            var author = text(el, "author")
            if (author.isBlank()) author = text(el, "dc:creator")
            val pubDate = text(el, "pubDate")
            val desc = text(el, "description")
            if (title.isBlank() && link.isBlank()) continue
            out.add(ParsedItem(title, link, author, parseRfc822Date(pubDate), desc))
        }
        return out
    }

    private fun parseAtom(entries: NodeList): List<ParsedItem> {
        val out = mutableListOf<ParsedItem>()
        for (i in 0 until entries.length) {
            val el = entries.item(i) as? Element ?: continue
            val title = text(el, "title")
            val link = linkHref(el)
            val author = text(el, "name")
            var updated = text(el, "updated")
            if (updated.isBlank()) updated = text(el, "published")
            var summary = text(el, "summary")
            if (summary.isBlank()) summary = text(el, "content")
            if (title.isBlank() && link.isBlank()) continue
            out.add(ParsedItem(title, link, author, parseIsoDate(updated), summary))
        }
        return out
    }

    private fun text(el: Element, tag: String): String {
        val nodes = el.getElementsByTagName(tag)
        if (nodes.length == 0) return ""
        return nodes.item(0)?.textContent?.trim() ?: ""
    }

    private fun linkHref(el: Element): String {
        val nodes = el.getElementsByTagName("link")
        for (i in 0 until nodes.length) {
            val n = nodes.item(i) as? Element ?: continue
            val href = n.getAttribute("href")
            if (href.isNotBlank()) return href
        }
        return text(el, "link")
    }

    private fun parseRfc822Date(raw: String): Long {
        if (raw.isBlank()) return 0L
        val patterns = listOf("EEE, dd MMM yyyy HH:mm:ss Z", "EEE, dd MMM yyyy HH:mm:ss zzz")
        for (p in patterns) {
            try {
                val sdf = SimpleDateFormat(p, Locale.US)
                val parsed = sdf.parse(raw)
                if (parsed != null) return parsed.time
            } catch (_: Exception) { }
        }
        return 0L
    }

    private fun parseIsoDate(raw: String): Long {
        if (raw.isBlank()) return 0L
        return try { Instant.parse(raw).toEpochMilli() } catch (_: Exception) { 0L }
    }
}

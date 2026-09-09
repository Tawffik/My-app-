package com.cyberos.app.data

/**
 * Parses the securitycipher/daily-bugbounty-writeups README markdown list.
 * Format example:
 *  - 💯September 7, 2026 - [Title here](https://medium.com/...)
 */
object DailyWriteupParser {

    private val LINE_RE = Regex(
        """[-*]\s*(?:💯)?\s*([A-Za-z]+\s+\d{1,2},\s+\d{4})\s*-\s*\[([^\]]+)\]\(([^)]+)\)"""
    )

    private val MONTHS = mapOf(
        "january" to 0, "february" to 1, "march" to 2, "april" to 3,
        "may" to 4, "june" to 5, "july" to 6, "august" to 7,
        "september" to 8, "october" to 9, "november" to 10, "december" to 11
    )

    data class ParsedWriteup(
        val title: String,
        val link: String,
        val publishedAt: Long,
        val dateLabel: String
    )

    fun parseReadme(markdown: String): List<ParsedWriteup> {
        val dated = parseDatedLines(markdown)
        if (dated.isNotEmpty()) return dated
        return parseGenericMarkdownLinks(markdown)
    }

    fun parseDatedLines(markdown: String): List<ParsedWriteup> {
        val out = mutableListOf<ParsedWriteup>()
        for (line in markdown.lines()) {
            val m = LINE_RE.find(line.trim()) ?: continue
            val dateLabel = m.groupValues[1].trim()
            val title = decodeEntities(m.groupValues[2].trim())
            val link = m.groupValues[3].trim()
            if (title.isBlank() || link.isBlank()) continue
            out.add(
                ParsedWriteup(
                    title = title,
                    link = link,
                    publishedAt = parseDate(dateLabel),
                    dateLabel = dateLabel
                )
            )
        }
        return out
    }

    private val MD_LINK_RE = Regex("""\[([^\]]{3,200})\]\((https?://[^)\s]+)\)""")
    private val PLAIN_URL_RE = Regex("""(?i)(https?://[^\s<>"']+)""")

    /** Awesome-list style: - [Title](url) */
    fun parseGenericMarkdownLinks(markdown: String): List<ParsedWriteup> {
        val out = mutableListOf<ParsedWriteup>()
        val seen = mutableSetOf<String>()
        for (line in markdown.lines()) {
            for (m in MD_LINK_RE.findAll(line)) {
                val title = decodeEntities(m.groupValues[1].trim())
                val link = m.groupValues[2].trim()
                if (title.isBlank() || link in seen) continue
                if (link.contains("github.com") && title.equals("http", true)) continue
                seen.add(link)
                out.add(ParsedWriteup(title, link, 0L, ""))
            }
        }
        return out.take(400)
    }

    /** Flat URL lists (e.g. HackerOne disclosed reports.txt). */
    fun parsePlainUrlList(text: String): List<ParsedWriteup> {
        val out = mutableListOf<ParsedWriteup>()
        val seen = mutableSetOf<String>()
        for (line in text.lines()) {
            val m = PLAIN_URL_RE.find(line.trim()) ?: continue
            val link = m.groupValues[1].trim().trimEnd('.', ',', ')', ']')
            if (link in seen) continue
            seen.add(link)
            val title = link.substringAfter("://").take(120)
            out.add(ParsedWriteup(title, link, 0L, ""))
        }
        return out.take(300)
    }

    private fun decodeEntities(s: String): String =
        s.replace("&lpar;", "(")
            .replace("&rpar;", ")")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")

    private fun parseDate(label: String): Long {
        return try {
            val parts = label.split(Regex("\\s+"))
            if (parts.size < 3) return 0L
            val month = MONTHS[parts[0].lowercase()] ?: return 0L
            val day = parts[1].trimEnd(',').toIntOrNull() ?: return 0L
            val year = parts[2].toIntOrNull() ?: return 0L
            val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
            cal.clear()
            cal.set(year, month, day, 12, 0, 0)
            cal.timeInMillis
        } catch (_: Exception) {
            0L
        }
    }
}

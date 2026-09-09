package com.cyberos.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class ResearchFetcher(
    private val sourceStore: ResearchSourceStore,
    private val itemStore: ResearchItemStore
) {
    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    suspend fun refreshAll(): Int = withContext(Dispatchers.IO) {
        var added = 0
        sourceStore.enabled().forEach { source -> added += refreshSource(source) }
        added
    }

    private fun refreshSource(source: ResearchSource): Int {
        return try {
            val req = Request.Builder()
                .url(source.url)
                .header("User-Agent", "CyberOS-Research/1.0")
                .build()
            val resp = http.newCall(req).execute()
            val body = resp.body?.string()
            resp.close()
            if (body.isNullOrBlank()) return 0

            val parsed: List<RssAtomParser.ParsedItem> = when (source.type.uppercase()) {
                "MARKDOWN" -> parseMarkdownWriteups(body, source.name)
                "LINKLIST" -> parseLinkList(body, source.name)
                else -> RssAtomParser.parse(body)
            }

            val now = System.currentTimeMillis()
            var nextId = itemStore.nextId()
            val existing = itemStore.all()
            val candidates = parsed.map { p ->
                val category = when (source.category) {
                    "Bug Bounty", "Tips", "AI Security", "Web Security", "Authentication" -> source.category
                    else -> ResearchCategorizer.categorize(p.title, p.summary)
                }
                val vulnType = BugBountyFilters.detectVulnType(p.title, p.summary)
                val tags = ResearchCategorizer.buildTags(p.title, p.summary)
                ResearchItem(
                    id = nextId++,
                    sourceId = source.id,
                    title = p.title,
                    link = p.link,
                    author = p.author.ifBlank { source.name },
                    summary = p.summary,
                    category = category,
                    tags = tags,
                    vulnerabilityType = vulnType,
                    publishedAt = if (p.publishedAt > 0) p.publishedAt else now,
                    retrievedAt = now
                )
            }
            val fresh = ResearchDeduplicator.dedupe(candidates, existing)
            if (fresh.isNotEmpty()) itemStore.addAll(fresh)
            fresh.size
        } catch (_: Exception) {
            0
        }
    }

    private fun parseMarkdownWriteups(markdown: String, sourceName: String): List<RssAtomParser.ParsedItem> {
        return DailyWriteupParser.parseReadme(markdown).map { w ->
            RssAtomParser.ParsedItem(
                title = w.title,
                link = w.link,
                author = sourceName,
                publishedAt = w.publishedAt,
                summary = if (w.dateLabel.isNotBlank()) "Writeup — ${w.dateLabel}" else "Curated writeup list"
            )
        }
    }

    private fun parseLinkList(text: String, sourceName: String): List<RssAtomParser.ParsedItem> {
        return DailyWriteupParser.parsePlainUrlList(text).map { w ->
            RssAtomParser.ParsedItem(
                title = w.title,
                link = w.link,
                author = sourceName,
                publishedAt = 0L,
                summary = "Disclosed report / writeup link"
            )
        }
    }
}

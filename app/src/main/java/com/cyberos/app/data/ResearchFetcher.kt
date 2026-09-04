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
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun refreshAll(): Int = withContext(Dispatchers.IO) {
        var added = 0
        sourceStore.enabled().forEach { source -> added += refreshSource(source) }
        added
    }

    private fun refreshSource(source: ResearchSource): Int {
        return try {
            val req = Request.Builder().url(source.url).build()
            val resp = http.newCall(req).execute()
            val body = resp.body?.string()
            resp.close()
            if (body.isNullOrBlank()) return 0

            val parsed = RssAtomParser.parse(body)
            val now = System.currentTimeMillis()
            var nextId = itemStore.nextId()
            val existing = itemStore.all()
            val candidates = parsed.map { p ->
                ResearchItem(
                    id = nextId++,
                    sourceId = source.id,
                    title = p.title,
                    link = p.link,
                    author = p.author,
                    summary = p.summary,
                    category = ResearchCategorizer.categorize(p.title, p.summary),
                    publishedAt = if (p.publishedAt > 0) p.publishedAt else now,
                    retrievedAt = now
                )
            }
            val fresh = ResearchDeduplicator.dedupe(candidates, existing)
            if (fresh.isNotEmpty()) itemStore.addAll(fresh)
            fresh.size
        } catch (_: Exception) { 0 }
    }
}

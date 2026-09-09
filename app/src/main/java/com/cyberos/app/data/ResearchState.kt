package com.cyberos.app.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.cyberos.app.ui.lang.Lang

class ResearchState(
    private val itemStore: ResearchItemStore,
    private val sourceStore: ResearchSourceStore,
    private val fetcher: ResearchFetcher,
    context: Context
) {
    private val syncPrefs = ResearchSyncPrefs(context.applicationContext)

    var items by mutableStateOf(itemStore.all())
        private set
    var category by mutableStateOf("All")
    /** Secondary filter for vulnerability class when viewing Bug Bounty / Writeups. */
    var vulnType by mutableStateOf("All")
    var refreshing by mutableStateOf(false)
        private set
    var lastError by mutableStateOf<String?>(null)
    var lastSyncAt by mutableStateOf(syncPrefs.lastSuccessAt())
        private set

    init {
        try { itemStore.seedCuratedIfNeeded() } catch (_: Exception) {}
        items = itemStore.all()
    }

    fun refresh() {
        items = itemStore.all()
        lastSyncAt = syncPrefs.lastSuccessAt()
    }

    suspend fun fetchLatest() {
        if (refreshing) return
        refreshing = true
        lastError = null
        try {
            val added = fetcher.refreshAll()
            syncPrefs.markSuccess(added)
            refresh()
        } catch (_: Exception) {
            lastError = Lang.t(
                "Could not refresh research — check connection.",
                "تعذر تحديث الأبحاث — تحقق من الاتصال."
            )
        } finally {
            refreshing = false
        }
    }

    /** Pull if never synced or last success older than [maxAgeMs]. */
    suspend fun fetchIfStale(maxAgeMs: Long = ResearchSyncPrefs.DEFAULT_STALE_MS) {
        if (syncPrefs.isStale(maxAgeMs) || items.isEmpty()) {
            fetchLatest()
        } else {
            refresh()
        }
    }

    fun toggleBookmark(id: Long) {
        itemStore.toggleBookmark(id)
        refresh()
    }

    fun markRead(id: Long) {
        itemStore.markRead(id)
        refresh()
    }

    fun filtered(): List<ResearchItem> {
        var base = when (category) {
            "All" -> items
            "Writeups" -> items.filter {
                it.category == "Bug Bounty" ||
                    it.tags.contains("writeup") ||
                    BugBountyFilters.looksLikeWriteup(it.title, it.summary)
            }
            else -> items.filter { it.category == category }
        }
        if (vulnType != "All") {
            base = BugBountyFilters.filterByVulnType(base, vulnType)
        }
        return base.sortedByDescending { it.publishedAt }
    }

    fun showVulnFilters(): Boolean =
        category == "Bug Bounty" || category == "Writeups"

    companion object {
        val CATEGORIES = listOf(
            "All",
            "Writeups",
            "Bug Bounty",
            "Web Security",
            "API Security",
            "Authentication",
            "Authorization",
            "Cloud",
            "Mobile",
            "AI Security",
            "Tips",
            "Vulnerabilities",
            "CVE",
            "Threat Intelligence",
            "Pentesting",
            "OSINT",
            "Supply Chain",
            "General"
        )
    }
}

package com.cyberos.app.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.cyberos.app.ui.lang.Lang

class ResearchState(
    private val itemStore: ResearchItemStore,
    private val sourceStore: ResearchSourceStore,
    private val fetcher: ResearchFetcher
) {
    var items by mutableStateOf(itemStore.all())
        private set
    var category by mutableStateOf("All")
    var refreshing by mutableStateOf(false)
        private set
    var lastError by mutableStateOf<String?>(null)

    fun refresh() { items = itemStore.all() }

    suspend fun fetchLatest() {
        if (refreshing) return
        refreshing = true
        lastError = null
        try {
            fetcher.refreshAll()
            refresh()
        } catch (_: Exception) {
            lastError = Lang.t("Could not refresh research — check connection.", "تعذر تحديث الأبحاث — تحقق من الاتصال.")
        } finally {
            refreshing = false
        }
    }

    fun toggleBookmark(id: Long) { itemStore.toggleBookmark(id); refresh() }
    fun markRead(id: Long) { itemStore.markRead(id); refresh() }

    fun filtered(): List<ResearchItem> {
        val base = if (category == "All") items else items.filter { it.category == category }
        return base.sortedByDescending { it.publishedAt }
    }

    companion object {
        val CATEGORIES = listOf(
            "All", "Bug Bounty", "Web Security", "API Security", "Authentication",
            "Authorization", "Cloud", "Mobile", "AI Security", "Vulnerabilities",
            "CVE", "Threat Intelligence", "Pentesting", "OSINT", "Supply Chain", "General"
        )
    }
}

package com.cyberos.app.data

data class ResearchItem(
    val id: Long,
    val sourceId: Long,
    val title: String,
    val link: String,
    val author: String = "",
    val summary: String = "",
    val category: String = "General",
    val tags: List<String> = emptyList(),
    val publishedAt: Long = 0L,
    val retrievedAt: Long = 0L,
    val read: Boolean = false,
    val bookmarked: Boolean = false
)

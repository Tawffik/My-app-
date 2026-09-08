package com.cyberos.app.data

data class ResearchSource(
    val id: Long,
    val name: String,
    val url: String,
    val type: String = "RSS",
    val enabled: Boolean = true,
    val trustLevel: String = "Tier2",
    val category: String = "General"
)

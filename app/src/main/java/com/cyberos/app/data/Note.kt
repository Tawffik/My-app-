package com.cyberos.app.data

data class Note(
    val id: Long,
    val title: String,
    val body: String,
    val tags: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    /** Notebook / folder path, e.g. "Bug Bounty/IDOR" */
    val folder: String = "",
    val pinned: Boolean = false
)

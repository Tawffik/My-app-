package com.cyberos.app.methodology

data class MethodologyStep(val id: Long, val objective: String, val done: Boolean = false)

data class Methodology(
    val id: Long, val title: String,
    val steps: List<MethodologyStep> = emptyList(),
    val createdAt: Long = 0L, val updatedAt: Long = 0L
)

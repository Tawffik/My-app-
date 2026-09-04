package com.cyberos.app.flashcards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ReviewState(private val store: FlashcardStore) {

    var queue by mutableStateOf<List<Flashcard>>(emptyList())
        private set
    var index by mutableStateOf(0)
        private set
    var revealed by mutableStateOf(false)
        private set
    var done by mutableStateOf(false)
        private set
    var reviewedCount by mutableStateOf(0)
        private set

    fun start() {
        val now = System.currentTimeMillis()
        val all = store.all().filter { it.dueAt <= now }
        val dueFirst = all.filter { it.dueAt > 0L }
        val newCards = all.filter { it.dueAt == 0L }
        queue = (dueFirst + newCards).take(20)
        index = 0; revealed = false; reviewedCount = 0
        done = queue.isEmpty()
    }

    fun current(): Flashcard? = queue.getOrNull(index)
    fun reveal() { revealed = true }

    fun grade(rating: Int) {
        val c = current() ?: return
        store.save(SrsScheduler.schedule(c, rating, System.currentTimeMillis()))
        reviewedCount += 1
        if (index + 1 >= queue.size) { done = true; revealed = false }
        else { index += 1; revealed = false }
    }
}

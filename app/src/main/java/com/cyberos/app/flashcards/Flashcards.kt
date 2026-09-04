package com.cyberos.app.flashcards

data class Flashcard(
    val id: Long,
    val topicId: String?,
    val question: String,
    val answer: String,
    val ease: Double = 2.5,
    val intervalDays: Double = 0.0,
    val reps: Int = 0,
    val dueAt: Long = 0L,
    val source: String = "demo"
)

object SrsScheduler {

    const val AGAIN = 0
    const val HARD = 1
    const val GOOD = 2
    const val EASY = 3

    private const val DAY_MS = 24L * 60 * 60 * 1000

    fun schedule(card: Flashcard, rating: Int, now: Long): Flashcard {
        var ease = card.ease
        var interval = card.intervalDays
        var reps = card.reps

        when (rating) {
            AGAIN -> { interval = 0.0; reps = 0; ease = maxOf(1.3, ease - 0.2) }
            HARD -> { interval = if (reps == 0) 1.0 else maxOf(1.0, interval * 1.2); reps += 1; ease = maxOf(1.3, ease - 0.15) }
            GOOD -> { interval = if (reps == 0) 1.0 else interval * ease; reps += 1 }
            EASY -> { interval = if (reps == 0) 3.0 else interval * ease * 1.3; reps += 1; ease = minOf(3.0, ease + 0.15) }
        }

        interval = minOf(interval, 180.0)
        val due = if (interval <= 0.0) now + 10 * 60 * 1000 else now + (interval * DAY_MS).toLong()
        return card.copy(ease = ease, intervalDays = interval, reps = reps, dueAt = due)
    }
}

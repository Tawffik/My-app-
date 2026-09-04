package com.cyberos.app

import com.cyberos.app.flashcards.Flashcard
import com.cyberos.app.flashcards.SrsScheduler
import org.junit.Assert.*
import org.junit.Test

class SrsSchedulerTest {
    private val now = 1_000_000_000_000L
    private fun card() = Flashcard(1, null, "q", "a")

    @Test fun good_grows() {
        val c1 = SrsScheduler.schedule(card(), SrsScheduler.GOOD, now)
        assertEquals(1.0, c1.intervalDays, 0.001)
    }
    @Test fun again_resets() {
        val c1 = SrsScheduler.schedule(card(), SrsScheduler.GOOD, now)
        val c2 = SrsScheduler.schedule(c1, SrsScheduler.AGAIN, now)
        assertEquals(0.0, c2.intervalDays, 0.001)
    }
    @Test fun ease_floor() {
        var c = card()
        repeat(40) { c = SrsScheduler.schedule(c, SrsScheduler.EASY, now) }
        assertTrue(c.ease >= 1.3)
    }
    @Test fun easy_future() { assertTrue(SrsScheduler.schedule(card(), SrsScheduler.EASY, now).dueAt > now) }
    @Test fun capped() {
        var c = card()
        repeat(40) { c = SrsScheduler.schedule(c, SrsScheduler.EASY, now) }
        assertTrue(c.intervalDays <= 180.0)
    }
}

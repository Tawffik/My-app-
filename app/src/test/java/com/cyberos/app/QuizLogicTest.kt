package com.cyberos.app

import com.cyberos.app.learning.QuizQuestion
import com.cyberos.app.learning.QuizState
import org.junit.Assert.*
import org.junit.Test

class QuizLogicTest {
    private fun q(correct: Int = 0) = QuizQuestion("q", listOf("a","b","c","d"), correct, "e")

    @Test fun select_correct() {
        val s = QuizState(); s.load("t", listOf("x" to q(1))); s.select(1)
        assertEquals(1, s.correctCount)
    }
    @Test fun select_wrong() {
        val s = QuizState(); s.load("t", listOf("x" to q(1))); s.select(0)
        assertEquals(0, s.correctCount)
    }
    @Test fun double_select() {
        val s = QuizState(); s.load("t", listOf("x" to q(0))); s.select(0); s.select(2)
        assertEquals(1, s.correctCount)
    }
    @Test fun next_requires_reveal() {
        val s = QuizState(); s.load("t", listOf("x" to q(), "x" to q())); s.next()
        assertEquals(0, s.index)
    }
    @Test fun next_advances() {
        val s = QuizState(); s.load("t", listOf("x" to q(), "x" to q())); s.select(0); s.next()
        assertEquals(1, s.index)
    }
    @Test fun last_finishes() {
        val s = QuizState(); s.load("t", listOf("x" to q())); s.select(0); s.next()
        assertTrue(s.finished)
    }
    @Test fun empty_finishes() { assertTrue(QuizState().let { it.load("t", emptyList()); it.finished }) }
    @Test fun shuffle_correct() {
        val o = q(2)
        repeat(10) {
            val sh = QuizState.shuffledForDisplay(o)
            assertEquals(o.options[o.correct], sh.options[sh.correct])
        }
    }
    @Test fun mixed() {
        val s = QuizState(); s.startMixed(10)
        assertTrue(s.questions.size <= 10 && s.questions.isNotEmpty())
    }
}

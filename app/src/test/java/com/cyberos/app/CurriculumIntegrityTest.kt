package com.cyberos.app

import com.cyberos.app.learning.CyberCurriculum
import org.junit.Assert.*
import org.junit.Test

class CurriculumIntegrityTest {
    @Test fun unique() {
        val ids = CyberCurriculum.paths.flatMap { it.topics }.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }
    @Test fun related() {
        val ids = CyberCurriculum.paths.flatMap { it.topics }.map { it.id }.toSet()
        CyberCurriculum.paths.flatMap { it.topics }.forEach { t ->
            t.related.forEach { r -> assertTrue(ids.contains(r)) }
        }
    }
    @Test fun content() {
        CyberCurriculum.paths.flatMap { it.topics }.forEach { t ->
            assertTrue(t.sections.isNotEmpty() && t.flashcards.isNotEmpty() && t.quiz.isNotEmpty())
        }
    }
    @Test fun quiz_valid() {
        CyberCurriculum.paths.flatMap { it.topics }.forEach { t ->
            t.quiz.forEach { q -> assertEquals(4, q.options.size); assertTrue(q.correct in 0..3) }
        }
    }
}

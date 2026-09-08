package com.cyberos.app

import com.cyberos.app.data.*
import com.cyberos.app.challenges.ChallengeVerdict
import com.cyberos.app.flashcards.Flashcard
import com.cyberos.app.methodology.*
import org.junit.Assert.*
import org.junit.Test

class JsonCodecTest {
    @Test fun notes() {
        val l = listOf(Note(1, "t", "b [[x]]", listOf("a")))
        assertEquals(l, JsonCodec.parseNotes(JsonCodec.notesToJson(l)))
    }
    @Test fun cards() {
        val l = listOf(Flashcard(1, "s", "q", "a"), Flashcard(2, null, "q", "a"))
        assertEquals(l, JsonCodec.parseFlashcards(JsonCodec.flashcardsToJson(l)))
    }
    @Test fun meths() {
        val l = listOf(Methodology(1, "t", listOf(MethodologyStep(10, "s", true))))
        assertEquals(l, JsonCodec.parseMethodologies(JsonCodec.methodologiesToJson(l)))
    }
    @Test fun tasks() {
        val l = listOf(Task(1, "t", "n", "DONE", "HIGH", 5L, 100L, 1L, 2L, 3L))
        assertEquals(l, JsonCodec.parseTasks(JsonCodec.tasksToJson(l)))
    }
    @Test fun projects() {
        val l = listOf(Project(7, "a", "x", 99L))
        assertEquals(l, JsonCodec.parseProjects(JsonCodec.projectsToJson(l)))
    }
    @Test fun throws() {
        var t = false
        try { JsonCodec.parseNotes("nope") } catch (e: Exception) { t = true }
        assertTrue(t)
    }
    @Test fun verdict() {
        val v = JsonCodec.parseChallengeVerdict("""{"verdict":"correct","feedback":"g"}""")
        assertEquals("correct", v.verdict)
    }
}

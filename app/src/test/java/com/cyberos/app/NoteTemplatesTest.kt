package com.cyberos.app

import com.cyberos.app.data.NoteTemplates
import com.cyberos.app.learning.CyberCurriculum
import org.junit.Assert.*
import org.junit.Test

class NoteTemplatesTest {
    @Test
    fun templates_cover_learning_loop() {
        val ids = NoteTemplates.ALL.map { it.id }.toSet()
        assertTrue(ids.containsAll(listOf("security", "writeup", "lab", "ai-security", "bb-session")))
        NoteTemplates.ALL.forEach {
            assertTrue(it.body.isNotBlank())
            assertTrue(it.title.isNotBlank())
        }
    }

    @Test
    fun ai_security_path_exists() {
        val path = CyberCurriculum.paths.firstOrNull { it.id == "ai-security" }
        assertNotNull(path)
        assertTrue(path!!.topics.any { it.id == "prompt-injection" })
        assertTrue(path.topics.any { it.id == "ai-for-bug-bounty" })
        assertNotNull(CyberCurriculum.findTopic("rag-security"))
    }
}

package com.cyberos.app

import com.cyberos.app.challenges.Challenges
import com.cyberos.app.learning.CyberCurriculum
import org.junit.Assert.*
import org.junit.Test

class ChallengesTest {
    @Test fun unique() {
        val ids = Challenges.all.map { it.id }
        assertEquals(ids.size, ids.distinct().size)
    }
    @Test fun nonblank() {
        Challenges.all.forEach { c ->
            assertTrue(c.title.isNotBlank() && c.scenario.isNotBlank() && c.expectedVuln.isNotBlank())
        }
    }
    @Test fun topics_resolve() {
        Challenges.all.forEach { assertNotNull(CyberCurriculum.findTopic(it.topicId)) }
    }
}

package com.cyberos.app

import com.cyberos.app.learning.CyberCurriculum
import com.cyberos.app.learning.LearningExtras
import org.junit.Assert.*
import org.junit.Test

class LearningExtrasTest {
    @Test
    fun extras_paths_present() {
        assertTrue(LearningExtras.paths.size >= 3)
        val ids = LearningExtras.paths.map { it.id }.toSet()
        assertTrue(ids.contains("web-bb-roadmap"))
        assertTrue(ids.contains("elite-access-tricks"))
        assertTrue(ids.contains("study-os"))
    }

    @Test
    fun curriculum_merges_extras() {
        val all = CyberCurriculum.allPaths()
        assertTrue(all.size > CyberCurriculum.paths.size)
        assertNotNull(CyberCurriculum.findTopic("rm-web-fundamentals"))
        assertNotNull(CyberCurriculum.findTopic("ea-hpp"))
        assertNotNull(CyberCurriculum.findTopic("http-fundamentals"))
    }
}

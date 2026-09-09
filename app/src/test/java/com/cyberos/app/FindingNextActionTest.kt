package com.cyberos.app

import com.cyberos.app.data.FindingNextAction
import org.junit.Assert.*
import org.junit.Test

class FindingNextActionTest {
    @Test
    fun needs_action_and_message() {
        assertTrue(FindingNextAction.needsActionToday("Idea"))
        assertFalse(FindingNextAction.needsActionToday("Closed"))
        assertTrue(FindingNextAction.forStatus("Draft").isNotBlank())
    }
}

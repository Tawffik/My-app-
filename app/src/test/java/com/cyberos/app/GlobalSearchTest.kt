package com.cyberos.app

import com.cyberos.app.data.*
import com.cyberos.app.methodology.Methodology
import com.cyberos.app.methodology.MethodologyStep
import org.junit.Assert.*
import org.junit.Test

class GlobalSearchTest {
    private val n = listOf(Note(1, "OAuth r", "t"))
    private val t = listOf(Task(2, "JWT", "", "TODO", "HIGH", null, null))
    private val p = listOf(Project(3, "bounty", "x"))
    private val m = listOf(Methodology(4, "api", listOf(MethodologyStep(10, "idor", false))))

    @Test fun topic() { assertTrue(GlobalSearch.search("oauth", n, t, p, m).any { it.kind == "topic" }) }
    @Test fun note() { assertTrue(GlobalSearch.search("t", n, t, p, m).any { it.kind == "note" }) }
    @Test fun task() { assertTrue(GlobalSearch.search("jwt", n, t, p, m).any { it.kind == "task" }) }
    @Test fun project() { assertTrue(GlobalSearch.search("bounty", n, t, p, m).any { it.kind == "project" }) }
    @Test fun meth() { assertTrue(GlobalSearch.search("idor", n, t, p, m).any { it.kind == "methodology" }) }
    @Test fun empty() { assertEquals(0, GlobalSearch.search("  ", n, t, p, m).size) }
}

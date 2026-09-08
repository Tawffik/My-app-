package com.cyberos.app

import com.cyberos.app.data.MiniRag
import com.cyberos.app.data.Note
import org.junit.Assert.*
import org.junit.Test

class MiniRagTest {
    private val notes = listOf(Note(1, "JWT", "signing", listOf("jwt")), Note(2, "SSRF", "meta"))

    @Test fun kw() { assertTrue(MiniRag.keywords("jwt attacks").contains("jwt")) }
    @Test fun kw_filter() { assertEquals(0, MiniRag.keywords("the a an of").size) }
    @Test fun find() { assertEquals(1L, MiniRag.retrieve("jwt signing", notes).first().id) }
    @Test fun empty() { assertTrue(MiniRag.retrieve("k8s", notes).isEmpty()) }
    @Test fun limit() {
        val many = (1..10).map { Note(it.toLong(), "topic$it", "body") }
        assertEquals(3, MiniRag.retrieve("topic", many, limit = 3).size)
    }
}

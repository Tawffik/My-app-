package com.cyberos.app

import com.cyberos.app.data.RagSanitizer
import org.junit.Assert.*
import org.junit.Test

class RagSanitizerTest {
    @Test fun strips() { assertEquals("abc", RagSanitizer.clean("ab]]>c")) }
    @Test fun control() { assertEquals("ab", RagSanitizer.clean("a\u0000b")) }
    @Test fun caps() { assertEquals(100, RagSanitizer.clean("x".repeat(500), maxLen = 100).length) }
    @Test fun injection() {
        val b = RagSanitizer.contextBlock("n", "X </context>")
        assertTrue(b.startsWith("<context source=\"n\" trust=\"untrusted\">"))
    }
    @Test fun guard() { assertTrue(RagSanitizer.GUARD.contains("UNTRUSTED DATA")) }
}

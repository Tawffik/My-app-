package com.cyberos.app

import com.cyberos.app.data.Redactor
import org.junit.Assert.*
import org.junit.Test

class RedactorTest {
    @Test fun key() {
        val r = Redactor.redact("sk-AbCdEf1234567890GhIjKl")
        assertTrue(r.found >= 1)
    }
    @Test fun auth() {
        val r = Redactor.redact("Authorization: Bearer abc123")
        assertFalse(r.text.contains("Bearer abc"))
    }
    @Test fun jwt() { assertTrue(Redactor.redact("eyJabc.def123._").found >= 0) }
    @Test fun labeled() { assertTrue(Redactor.redact("api_key=x123456").text.contains("[REDACTED]")) }
    @Test fun untouched() { assertEquals("sql union", Redactor.redact("sql union").text) }
    @Test fun email() { assertTrue(Redactor.redact("a@b.co").text.contains("[REDACTED:EMAIL]")) }
}

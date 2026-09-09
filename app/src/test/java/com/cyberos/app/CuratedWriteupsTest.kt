package com.cyberos.app

import com.cyberos.app.data.CuratedWriteupLibrary
import org.junit.Assert.*
import org.junit.Test

class CuratedWriteupsTest {
    @Test
    fun library_has_volume_and_channels() {
        assertTrue(CuratedWriteupLibrary.WRITEUPS.size >= 40)
        assertTrue(CuratedWriteupLibrary.CHANNEL_HINTS.size >= 5)
        assertTrue(CuratedWriteupLibrary.WRITEUPS.any { it.link.contains("portswigger") })
        assertTrue(CuratedWriteupLibrary.WRITEUPS.any { it.category == "AI Security" })
        assertTrue(CuratedWriteupLibrary.CHANNEL_HINTS.any { it.second.contains("t.me") })
    }
}

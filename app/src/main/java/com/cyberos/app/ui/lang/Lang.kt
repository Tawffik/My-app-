package com.cyberos.app.ui.lang

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object Lang {
    var current by mutableStateOf("en")

    fun t(en: String, ar: String): String = if (current == "ar") ar else en
}

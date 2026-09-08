package com.cyberos.app.data

import android.content.Context
import com.cyberos.app.ui.lang.Lang

class LangStore(context: Context) {
    private val prefs = context.getSharedPreferences("cyberos_prefs", Context.MODE_PRIVATE)

    fun load(): String = prefs.getString("lang", "en") ?: "en"

    fun save(lang: String) {
        prefs.edit().putString("lang", lang).apply()
        Lang.current = lang
    }
}

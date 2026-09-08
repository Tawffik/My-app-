package com.cyberos.app.data

import android.content.Context

data class AiSettings(val baseUrl: String, val model: String)

class AiSettingsStore(context: Context) {

    private val prefs = context.getSharedPreferences("cyberos_ai", Context.MODE_PRIVATE)

    fun load(): AiSettings = AiSettings(
        baseUrl = prefs.getString("base_url", DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL,
        model = prefs.getString("model", DEFAULT_MODEL) ?: DEFAULT_MODEL
    )

    fun save(s: AiSettings) {
        prefs.edit()
            .putString("base_url", s.baseUrl.trim().trimEnd('/'))
            .putString("model", s.model.trim())
            .apply()
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://api.openai.com/v1"
        const val DEFAULT_MODEL = "gpt-4o-mini"
    }
}

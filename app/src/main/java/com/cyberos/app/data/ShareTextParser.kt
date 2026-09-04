package com.cyberos.app.data

object ShareTextParser {

    private val URL_REGEX = Regex("(https?://\\S+)")

    data class ParsedShare(val title: String, val url: String)

    fun parse(sharedText: String, sharedSubject: String? = null): ParsedShare {
        val text = sharedText.trim()
        val match = URL_REGEX.find(text)
        val url = match?.value?.trimEnd('.', ',', ')', ']', '"', '\'') ?: ""
        val withoutUrl = if (url.isNotBlank()) text.replace(url, "").trim() else text

        val title = when {
            !sharedSubject.isNullOrBlank() -> sharedSubject.trim()
            withoutUrl.isNotBlank() -> withoutUrl.take(140)
            url.isNotBlank() -> url
            else -> "Untitled"
        }
        return ParsedShare(title, url)
    }
}

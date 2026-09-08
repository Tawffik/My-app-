package com.cyberos.app.data

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

object BrowserLauncher {

    fun open(context: Context, url: String) {
        if (url.isBlank()) return
        try {
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(context, Uri.parse(url))
        } catch (_: Exception) {
            // No browser available or invalid URL — fail silently, UI already validates blank links.
        }
    }
}

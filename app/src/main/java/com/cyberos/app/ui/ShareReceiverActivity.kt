package com.cyberos.app.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.cyberos.app.data.ResearchCategorizer
import com.cyberos.app.data.ResearchItem
import com.cyberos.app.data.ResearchItemStore
import com.cyberos.app.data.ShareTextParser
import com.cyberos.app.ui.lang.Lang

/**
 * Phase C — Research Browser bridge.
 * Lets the user "Share" a link from any browser/app straight into
 * the CyberOS Research feed as a bookmarked item, without needing
 * an in-app WebView.
 */
class ShareReceiverActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedText = intent?.getStringExtra(Intent.EXTRA_TEXT)
        val sharedSubject = intent?.getStringExtra(Intent.EXTRA_SUBJECT)

        if (intent?.action == Intent.ACTION_SEND && !sharedText.isNullOrBlank()) {
            val parsed = ShareTextParser.parse(sharedText, sharedSubject)
            if (parsed.url.isNotBlank()) {
                val store = ResearchItemStore(applicationContext)
                val now = System.currentTimeMillis()
                store.addAll(
                    listOf(
                        ResearchItem(
                            id = store.nextId(),
                            sourceId = 0L,
                            title = parsed.title,
                            link = parsed.url,
                            author = "",
                            summary = "",
                            category = ResearchCategorizer.categorize(parsed.title, ""),
                            publishedAt = now,
                            retrievedAt = now,
                            read = false,
                            bookmarked = true
                        )
                    )
                )
                Toast.makeText(
                    this,
                    Lang.t("Saved to CyberOS Research", "اتحفظ في أبحاث CyberOS"),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    Lang.t("No link found in shared content", "مفيش رابط في المشاركة"),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        finish()
    }
}

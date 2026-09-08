package com.cyberos.app.data

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * Background pull of research / daily writeup feeds.
 * Respects battery + network; does not run without connectivity.
 */
class ResearchSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val sourceStore = ResearchSourceStore(applicationContext).also { it.ensureSeeded() }
            val itemStore = ResearchItemStore(applicationContext)
            val fetcher = ResearchFetcher(sourceStore, itemStore)
            val added = fetcher.refreshAll()
            ResearchSyncPrefs(applicationContext).markSuccess(added)
            if (added > 0) {
                CyberNotifier.show(
                    applicationContext,
                    CyberNotificationChannels.RESEARCH,
                    2001,
                    "New research available",
                    "$added new item(s) pulled into CyberOS Research."
                )
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val UNIQUE_NAME = "cyberos_research_sync"
        /** Android minimum for PeriodicWork is 15 minutes; 6h is battery-friendly for feeds. */
        private const val INTERVAL_HOURS = 6L

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<ResearchSyncWorker>(
                INTERVAL_HOURS, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
                UNIQUE_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        /** One-shot refresh useful right after install / first open. */
        fun enqueueOnce(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = androidx.work.OneTimeWorkRequestBuilder<ResearchSyncWorker>()
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context.applicationContext).enqueue(request)
        }
    }
}

/** Lightweight prefs for last sync metadata (UI + stale checks). */
class ResearchSyncPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("research_sync", Context.MODE_PRIVATE)

    fun lastSuccessAt(): Long = prefs.getLong(KEY_LAST_SUCCESS, 0L)
    fun lastAddedCount(): Int = prefs.getInt(KEY_LAST_ADDED, 0)

    fun markSuccess(added: Int) {
        prefs.edit()
            .putLong(KEY_LAST_SUCCESS, System.currentTimeMillis())
            .putInt(KEY_LAST_ADDED, added)
            .apply()
    }

    fun isStale(maxAgeMs: Long = DEFAULT_STALE_MS): Boolean {
        val last = lastSuccessAt()
        if (last <= 0L) return true
        return System.currentTimeMillis() - last > maxAgeMs
    }

    companion object {
        private const val KEY_LAST_SUCCESS = "last_success_at"
        private const val KEY_LAST_ADDED = "last_added"
        /** Consider feed stale after 6 hours. */
        const val DEFAULT_STALE_MS = 6L * 60L * 60L * 1000L
    }
}

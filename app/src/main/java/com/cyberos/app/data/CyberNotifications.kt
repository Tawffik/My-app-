package com.cyberos.app.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.cyberos.app.MainActivity
import java.util.concurrent.TimeUnit

object CyberNotificationChannels {
    const val RESEARCH = "cyberos_research"
    const val DIGEST = "cyberos_digest"
    const val BUG_BOUNTY = "cyberos_bugbounty"

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        val channels = listOf(
            NotificationChannel(RESEARCH, "Research updates", NotificationManager.IMPORTANCE_DEFAULT),
            NotificationChannel(DIGEST, "Daily digest", NotificationManager.IMPORTANCE_DEFAULT),
            NotificationChannel(BUG_BOUNTY, "Bug Bounty", NotificationManager.IMPORTANCE_DEFAULT)
        )
        channels.forEach { nm.createNotificationChannel(it) }
    }
}

object CyberNotifier {
    fun show(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        body: String
    ) {
        CyberNotificationChannels.ensure(context)
        val launch = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pi = PendingIntent.getActivity(
            context,
            notificationId,
            launch,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notif = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(notificationId, notif)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS not granted on API 33+
        }
    }
}

/**
 * Builds a short daily briefing from local data (offline-friendly).
 */
object DailyDigestBuilder {
    fun build(context: Context): String {
        val findings = BugBountyFindingStore(context).all()
        val openFindings = findings.filter {
            it.status !in listOf("Closed", "Resolved", "Duplicate", "N/A", "Submitted", "Triaged")
        }
        val research = ResearchItemStore(context).all()
        val recentWriteups = research.filter {
            it.category == "Bug Bounty" || it.tags.contains("writeup")
        }.take(5)
        val cardsDue = try {
            // soft dependency — flashcards may exist
            com.cyberos.app.flashcards.FlashcardStore(context).countDue(System.currentTimeMillis())
        } catch (_: Exception) {
            0
        }

        val lines = mutableListOf<String>()
        lines += "CyberOS Daily Brief"
        lines += "• Open findings needing attention: ${openFindings.size}"
        if (openFindings.isNotEmpty()) {
            openFindings.take(3).forEach { lines += "  – ${it.title} (${it.status})" }
        }
        lines += "• Flashcards due: $cardsDue"
        lines += "• Recent writeups cached: ${recentWriteups.size}"
        recentWriteups.take(3).forEach { lines += "  – ${it.title}" }
        if (openFindings.isEmpty() && recentWriteups.isEmpty()) {
            lines += "• Stay curious — pull Research or log a new Idea."
        }
        return lines.joinToString("\n")
    }
}

class DailyDigestWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val body = DailyDigestBuilder.build(applicationContext)
            CyberNotifier.show(
                applicationContext,
                CyberNotificationChannels.DIGEST,
                NOTIF_ID,
                "CyberOS Daily Brief",
                body
            )
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val UNIQUE_NAME = "cyberos_daily_digest"
        private const val NOTIF_ID = 1001
        private const val INTERVAL_HOURS = 24L

        fun schedule(context: Context) {
            CyberNotificationChannels.ensure(context)
            val request = PeriodicWorkRequestBuilder<DailyDigestWorker>(
                INTERVAL_HOURS, TimeUnit.HOURS
            ).build()
            WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
                UNIQUE_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}

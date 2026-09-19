package com.kgsoft.favorsbank.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kgsoft.favorsbank.MainActivity
import com.kgsoft.favorsbank.R

/**
 * Replaces the Sketchware RemoteViews notification code in MainActivity /
 * SettingsActivity with a clean NotificationCompat helper. Keeps the original
 * channel id ("0") and channel name ("بنك الحسنات").
 */
object Notifier {
    const val CHANNEL_ID = "0"
    private const val CHANNEL_NAME = "بنك الحسنات"

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                manager.createNotificationChannel(
                    NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                        description = "مهمة جديدة "
                    }
                )
            }
        }
    }

    fun hasPermission(context: Context): Boolean =
        NotificationManagerCompat.from(context).areNotificationsEnabled()

    /**
     * Shows the "recommended mission" notification. Tapping it opens the tasks hub,
     * mirroring the original PendingIntent that launched TodoActivity.
     */
    fun showRecommendation(context: Context, jobName: String) {
        ensureChannel(context)
        if (!hasPermission(context)) return
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("route", "todo")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.money_icon)
            .setContentTitle("بنك الحسنات")
            .setContentText("مهمة مقترحة لك: $jobName")
            .setStyle(NotificationCompat.BigTextStyle().bigText("مهمة مقترحة لك: $jobName"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
        NotificationManagerCompat.from(context).notify(0, notification)
    }

    /** The "hurry to good deeds" reminder from SettingsActivity. */
    fun showReminder(context: Context) {
        ensureChannel(context)
        if (!hasPermission(context)) return
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("route", "todo")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.money_icon)
            .setContentTitle("بنك الحسنات")
            .setContentText("سارع للخيرات يا عبدالله")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
        NotificationManagerCompat.from(context).notify(1, notification)
    }
}

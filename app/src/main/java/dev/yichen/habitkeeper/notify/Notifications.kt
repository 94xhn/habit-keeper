package dev.yichen.habitkeeper.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/** Notification channel setup. Idempotent — safe to call on every app start / receiver fire. */
object Notifications {
    const val CHANNEL_ID = "habit_reminders"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(NotificationManager::class.java)
            if (mgr.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Habit reminders",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply { description = "Daily reminders to check off your habits" }
                mgr.createNotificationChannel(channel)
            }
        }
    }
}

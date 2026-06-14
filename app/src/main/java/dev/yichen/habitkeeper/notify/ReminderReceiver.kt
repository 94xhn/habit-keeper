package dev.yichen.habitkeeper.notify

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dev.yichen.habitkeeper.domain.model.Habit
import dev.yichen.habitkeeper.ui.MainActivity

/** Fires at a habit's reminder time: posts the notification, then re-arms for tomorrow. */
class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getLongExtra(EXTRA_HABIT_ID, -1L)
        val name = intent.getStringExtra(EXTRA_HABIT_NAME) ?: "Habit"
        val minute = intent.getIntExtra(EXTRA_MINUTE, -1)
        if (habitId < 0) return

        Notifications.ensureChannel(context)

        val openIntent = PendingIntent.getActivity(
            context,
            habitId.toInt(),
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, Notifications.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(name)
            .setContentText("Time to check off this habit")
            .setAutoCancel(true)
            .setContentIntent(openIntent)
            .build()

        // POST_NOTIFICATIONS is runtime-gated on Android 13+; skip silently if not granted.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(habitId.toInt(), notification)
        }

        // Re-arm for the same time tomorrow.
        if (minute >= 0) {
            HabitReminderScheduler(context).schedule(
                Habit(id = habitId, name = name, startEpochDay = 0L, reminderMinuteOfDay = minute),
            )
        }
    }

    companion object {
        const val EXTRA_HABIT_ID = "habit_id"
        const val EXTRA_HABIT_NAME = "habit_name"
        const val EXTRA_MINUTE = "minute"
    }
}

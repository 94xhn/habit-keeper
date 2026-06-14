package dev.yichen.habitkeeper.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.yichen.habitkeeper.domain.model.Habit
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Schedules one daily reminder per habit via AlarmManager.
 *
 * Uses setAndAllowWhileIdle (no SCHEDULE_EXACT_ALARM permission needed) and re-arms itself
 * for the next day inside [ReminderReceiver] after each fire — accurate enough for daily
 * habit nudges, and survives Doze.
 */
class HabitReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    /** Schedule (or replace) the next reminder for [habit]. No-op if the habit has no reminder. */
    fun schedule(habit: Habit) {
        val minute = habit.reminderMinuteOfDay ?: return
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextTriggerMillis(minute),
            pendingIntent(habit.id, habit.name, minute),
        )
    }

    /** Cancel a habit's reminder (on delete or when the user turns the reminder off). */
    fun cancel(habitId: Long) {
        alarmManager.cancel(pendingIntent(habitId, "", 0))
    }

    private fun nextTriggerMillis(minuteOfDay: Int): Long {
        val now = LocalDateTime.now()
        val time = LocalTime.of(minuteOfDay / 60, minuteOfDay % 60)
        var next = LocalDateTime.of(LocalDate.now(), time)
        if (!next.isAfter(now)) next = next.plusDays(1)
        return next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    // requestCode = habitId so each habit owns a distinct, replaceable alarm.
    private fun pendingIntent(habitId: Long, name: String, minute: Int): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_HABIT_ID, habitId)
            putExtra(ReminderReceiver.EXTRA_HABIT_NAME, name)
            putExtra(ReminderReceiver.EXTRA_MINUTE, minute)
        }
        return PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}

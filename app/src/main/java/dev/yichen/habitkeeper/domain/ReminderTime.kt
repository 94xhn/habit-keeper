package dev.yichen.habitkeeper.domain

/**
 * A daily reminder time stored as minute-of-day (0..1439). Pure, Android-free helpers so the
 * formatting/parsing is unit-tested independently of the time picker UI.
 */
object ReminderTime {
    fun of(hour: Int, minute: Int): Int = hour * 60 + minute

    fun hourOf(minuteOfDay: Int): Int = minuteOfDay / 60

    fun minuteOf(minuteOfDay: Int): Int = minuteOfDay % 60

    /** "08:05" style 24-hour label. */
    fun format(minuteOfDay: Int): String =
        "%02d:%02d".format(minuteOfDay / 60, minuteOfDay % 60)
}

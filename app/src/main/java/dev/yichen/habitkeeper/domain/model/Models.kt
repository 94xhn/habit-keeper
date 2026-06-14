package dev.yichen.habitkeeper.domain.model

/**
 * How often a habit is expected to be done.
 *
 * The flexible-schedule engine ([dev.yichen.habitkeeper.domain.Schedule]) interprets
 * these. This is the headline differentiator: the #1 competitor complaint cluster is
 * "functionality missing" (weighted 333 in review mining) — users begging for weekly /
 * N-times-per-week / custom-interval habits instead of daily-only.
 */
sealed interface Frequency {
    /** Every day. */
    data object Daily : Frequency

    /** Only on specific weekdays. [days] holds java.time DayOfWeek values 1..7 (Mon..Sun). */
    data class Weekly(val days: Set<Int>) : Frequency

    /** N completions per ISO week, any days the user chooses. */
    data class TimesPerWeek(val times: Int) : Frequency

    /** Every N days counted from the habit's start date (e.g. every 3 days). */
    data class EveryNDays(val n: Int) : Frequency
}

/** A habit the user wants to build. Pure domain model — no Android / Room types. */
data class Habit(
    val id: Long = 0,
    val name: String,
    val emoji: String = "",
    val frequency: Frequency = Frequency.Daily,
    val startEpochDay: Long,
    val groupId: Long? = null,
    val sortOrder: Int = 0,
    val archived: Boolean = false,
    /** Daily reminder time as minute-of-day (0..1439), or null for no reminder. */
    val reminderMinuteOfDay: Int? = null,
)

/** A completion mark for a habit on a given day (epochDay = days since 1970-01-01). */
data class HabitLog(
    val id: Long = 0,
    val habitId: Long,
    val epochDay: Long,
    val done: Boolean = true,
)

/** A user-defined grouping of habits — answers "no way to group habits" (3 upvotes). */
data class HabitGroup(
    val id: Long = 0,
    val name: String,
    val sortOrder: Int = 0,
)

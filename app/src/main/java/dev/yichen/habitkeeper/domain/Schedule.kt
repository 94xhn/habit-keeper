package dev.yichen.habitkeeper.domain

import dev.yichen.habitkeeper.domain.model.Frequency
import dev.yichen.habitkeeper.domain.model.Habit
import java.time.LocalDate

/**
 * Flexible scheduling engine — decides whether a habit is "due" on a given day.
 *
 * This is the product's headline differentiator. Review mining shows the biggest pain
 * cluster is "functionality missing" (weighted 333): 79 upvotes for rescheduling
 * recurring tasks, 11 for weekly habits, 5 for "repeat N times per week". Competitors
 * that only support daily habits get hammered here. We model four frequency types as
 * pure, fully testable logic with zero Android dependencies.
 */
object Schedule {

    /** Day-of-week as java.time gives it: Monday = 1 .. Sunday = 7. */
    private fun dayOfWeek(epochDay: Long): Int = LocalDate.ofEpochDay(epochDay).dayOfWeek.value

    /**
     * Is [habit] expected to be done on [epochDay]?
     *
     * - [Frequency.Daily] — always, on or after the start date.
     * - [Frequency.Weekly] — only on the chosen weekdays.
     * - [Frequency.TimesPerWeek] — every day is a candidate; the weekly *target* is what
     *   matters (see [weeklyTargetMet]), so any day is "due" until the target is hit.
     * - [Frequency.EveryNDays] — start, start+n, start+2n, ...
     *
     * Days before [Habit.startEpochDay] are never due.
     */
    fun isDue(habit: Habit, epochDay: Long): Boolean {
        if (epochDay < habit.startEpochDay) return false
        return when (val f = habit.frequency) {
            Frequency.Daily -> true
            is Frequency.Weekly -> dayOfWeek(epochDay) in f.days
            is Frequency.TimesPerWeek -> f.times > 0
            is Frequency.EveryNDays -> f.n > 0 && (epochDay - habit.startEpochDay) % f.n == 0L
        }
    }

    /** Monday-based start (epochDay) of the ISO week containing [epochDay]. */
    fun weekStart(epochDay: Long): Long {
        val d = LocalDate.ofEpochDay(epochDay)
        return d.minusDays((d.dayOfWeek.value - 1).toLong()).toEpochDay()
    }

    /** How many of [doneDays] fall in the same ISO week as [epochDay]. */
    fun completionsThisWeek(doneDays: Collection<Long>, epochDay: Long): Int {
        val start = weekStart(epochDay)
        val end = start + 6
        return doneDays.count { it in start..end }
    }

    /**
     * For a [Frequency.TimesPerWeek] habit, is the weekly target already met as of [epochDay]?
     * Lets the UI stop nagging once the user hit, say, 4/4 for the week.
     */
    fun weeklyTargetMet(habit: Habit, doneDays: Collection<Long>, epochDay: Long): Boolean {
        val f = habit.frequency
        return f is Frequency.TimesPerWeek && completionsThisWeek(doneDays, epochDay) >= f.times
    }
}

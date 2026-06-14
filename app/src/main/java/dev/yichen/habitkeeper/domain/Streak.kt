package dev.yichen.habitkeeper.domain

import dev.yichen.habitkeeper.domain.model.Habit

/**
 * Streak math — the dopamine core of any habit tracker.
 *
 * A streak counts consecutive *due* days that were completed. Non-due days (e.g. weekends
 * for a weekday-only habit) never break a streak — a correctness bug competitors are
 * criticized for. Today gets a grace window: if today is due but not done yet, the streak
 * isn't considered broken; we count up to the most recent completed due day.
 */
object Streak {

    /**
     * Current streak as of [today]: walk backwards over due days, counting completed ones
     * until the first due-but-missed day.
     */
    fun current(habit: Habit, doneDays: Set<Long>, today: Long): Int {
        var day = today
        // Grace for today: if today is due but not done yet, start counting from yesterday.
        if (Schedule.isDue(habit, day) && day !in doneDays) day -= 1
        var streak = 0
        while (day >= habit.startEpochDay) {
            if (Schedule.isDue(habit, day)) {
                if (day in doneDays) streak++ else break
            }
            day -= 1
        }
        return streak
    }

    /** Longest streak ever, scanning every due day from the start date through [today]. */
    fun longest(habit: Habit, doneDays: Set<Long>, today: Long): Int {
        var best = 0
        var run = 0
        var day = habit.startEpochDay
        while (day <= today) {
            if (Schedule.isDue(habit, day)) {
                if (day in doneDays) {
                    run++
                    if (run > best) best = run
                } else {
                    run = 0
                }
            }
            day++
        }
        return best
    }
}

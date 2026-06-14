package dev.yichen.habitkeeper.domain

import dev.yichen.habitkeeper.domain.model.Frequency
import dev.yichen.habitkeeper.domain.model.Habit
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class StreakTest {

    private val monday = LocalDate.parse("2026-06-15").with(DayOfWeek.MONDAY).toEpochDay()

    private fun daily(start: Long = monday) =
        Habit(id = 1, name = "x", frequency = Frequency.Daily, startEpochDay = start)

    @Test
    fun `current streak counts consecutive completed days through today`() {
        val h = daily()
        val done = setOf(monday, monday + 1, monday + 2)
        assertEquals(3, Streak.current(h, done, monday + 2))
    }

    @Test
    fun `today not done yet does not break the streak (grace window)`() {
        val h = daily()
        val done = setOf(monday, monday + 1) // today = monday+2, not done yet
        assertEquals(2, Streak.current(h, done, monday + 2))
    }

    @Test
    fun `a missed past day breaks the streak`() {
        val h = daily()
        val done = setOf(monday, monday + 2, monday + 3) // monday+1 missed
        assertEquals(2, Streak.current(h, done, monday + 3))
    }

    @Test
    fun `non-due days never break a weekly streak`() {
        // Mon/Wed/Fri habit: completing those keeps the streak across the off days.
        val h = Habit(
            id = 1, name = "x",
            frequency = Frequency.Weekly(setOf(1, 3, 5)),
            startEpochDay = monday,
        )
        val done = setOf(monday, monday + 2, monday + 4) // Mon, Wed, Fri
        assertEquals(3, Streak.current(h, done, monday + 4))
    }

    @Test
    fun `longest streak scans the whole history`() {
        val h = daily()
        val done = setOf(monday, monday + 1, /* gap at +2 */ monday + 3, monday + 4, monday + 5)
        assertEquals(3, Streak.longest(h, done, monday + 5))
    }

    @Test
    fun `empty history is zero`() {
        val h = daily()
        assertEquals(0, Streak.current(h, emptySet(), monday + 3))
        assertEquals(0, Streak.longest(h, emptySet(), monday + 3))
    }
}

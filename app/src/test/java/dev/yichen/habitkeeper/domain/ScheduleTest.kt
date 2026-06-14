package dev.yichen.habitkeeper.domain

import dev.yichen.habitkeeper.domain.model.Frequency
import dev.yichen.habitkeeper.domain.model.Habit
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScheduleTest {

    // Derive a guaranteed Monday so tests don't depend on what weekday a literal date is.
    private val monday = LocalDate.parse("2026-06-15").with(DayOfWeek.MONDAY).toEpochDay()

    private fun habit(freq: Frequency, start: Long = monday) =
        Habit(id = 1, name = "x", frequency = freq, startEpochDay = start)

    @Test
    fun `daily is always due on or after start`() {
        val h = habit(Frequency.Daily)
        assertTrue(Schedule.isDue(h, monday))
        assertTrue(Schedule.isDue(h, monday + 5))
    }

    @Test
    fun `nothing is due before start date`() {
        val h = habit(Frequency.Daily)
        assertFalse(Schedule.isDue(h, monday - 1))
    }

    @Test
    fun `weekly is due only on chosen weekdays`() {
        val h = habit(Frequency.Weekly(setOf(1, 3, 5))) // Mon, Wed, Fri
        assertTrue(Schedule.isDue(h, monday))       // Mon
        assertFalse(Schedule.isDue(h, monday + 1))  // Tue
        assertTrue(Schedule.isDue(h, monday + 2))   // Wed
        assertFalse(Schedule.isDue(h, monday + 3))  // Thu
    }

    @Test
    fun `every N days lands on multiples of N from start`() {
        val h = habit(Frequency.EveryNDays(3))
        assertTrue(Schedule.isDue(h, monday))       // +0
        assertFalse(Schedule.isDue(h, monday + 1))
        assertTrue(Schedule.isDue(h, monday + 3))   // +3
        assertTrue(Schedule.isDue(h, monday + 6))   // +6
    }

    @Test
    fun `week start is the monday of the iso week`() {
        // Sunday of the same week is monday + 6.
        assertEquals(monday, Schedule.weekStart(monday + 6))
        assertEquals(monday, Schedule.weekStart(monday))
    }

    @Test
    fun `times per week counts completions and detects target met`() {
        val h = habit(Frequency.TimesPerWeek(3))
        val done = setOf(monday, monday + 2, monday + 4) // 3 done this week
        assertEquals(3, Schedule.completionsThisWeek(done, monday + 4))
        assertTrue(Schedule.weeklyTargetMet(h, done, monday + 4))
        assertFalse(Schedule.weeklyTargetMet(h, setOf(monday), monday))
    }
}

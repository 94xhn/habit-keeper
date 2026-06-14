package dev.yichen.habitkeeper.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class ReminderTimeTest {

    @Test
    fun `of combines hour and minute into minute-of-day`() {
        assertEquals(8 * 60 + 30, ReminderTime.of(8, 30))
        assertEquals(0, ReminderTime.of(0, 0))
        assertEquals(23 * 60 + 59, ReminderTime.of(23, 59))
    }

    @Test
    fun `format pads to HH mm`() {
        assertEquals("08:05", ReminderTime.format(8 * 60 + 5))
        assertEquals("00:00", ReminderTime.format(0))
        assertEquals("23:59", ReminderTime.format(23 * 60 + 59))
    }

    @Test
    fun `hour and minute extraction round-trips`() {
        val mod = ReminderTime.of(8, 30)
        assertEquals(8, ReminderTime.hourOf(mod))
        assertEquals(30, ReminderTime.minuteOf(mod))
    }
}

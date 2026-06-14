package dev.yichen.habitkeeper.domain

import dev.yichen.habitkeeper.domain.model.Frequency
import kotlin.test.Test
import kotlin.test.assertEquals

class FrequencyFormTest {

    @Test
    fun `type 0 is daily`() {
        assertEquals(Frequency.Daily, FrequencyForm.build(0, emptySet(), "3", "2"))
    }

    @Test
    fun `weekly keeps the selected days`() {
        assertEquals(Frequency.Weekly(setOf(1, 5)), FrequencyForm.build(1, setOf(1, 5), "3", "2"))
    }

    @Test
    fun `times per week parses and clamps to 1 through 7`() {
        assertEquals(Frequency.TimesPerWeek(4), FrequencyForm.build(2, emptySet(), "4", "2"))
        assertEquals(Frequency.TimesPerWeek(7), FrequencyForm.build(2, emptySet(), "9", "2")) // clamped
        assertEquals(Frequency.TimesPerWeek(1), FrequencyForm.build(2, emptySet(), "", "2"))  // blank -> 1
    }

    @Test
    fun `every n days parses and floors at 1`() {
        assertEquals(Frequency.EveryNDays(3), FrequencyForm.build(3, emptySet(), "3", "3"))
        assertEquals(Frequency.EveryNDays(1), FrequencyForm.build(3, emptySet(), "3", "0"))  // floored
        assertEquals(Frequency.EveryNDays(1), FrequencyForm.build(3, emptySet(), "3", "x"))  // garbage -> 1
    }
}

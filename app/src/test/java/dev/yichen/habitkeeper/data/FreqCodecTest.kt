package dev.yichen.habitkeeper.data

import dev.yichen.habitkeeper.domain.model.Frequency
import kotlin.test.Test
import kotlin.test.assertEquals

/** The Frequency <-> (type, data) flattening is the one tricky bit of persistence; pin it down. */
class FreqCodecTest {

    private fun roundTrip(f: Frequency): Frequency {
        val (type, data) = HabitRepository.encodeFreq(f)
        return HabitRepository.decodeFreq(type, data)
    }

    @Test
    fun `daily round-trips`() {
        assertEquals(Frequency.Daily, roundTrip(Frequency.Daily))
    }

    @Test
    fun `weekly round-trips with sorted days`() {
        assertEquals(Frequency.Weekly(setOf(1, 3, 5)), roundTrip(Frequency.Weekly(setOf(5, 1, 3))))
    }

    @Test
    fun `times per week round-trips`() {
        assertEquals(Frequency.TimesPerWeek(4), roundTrip(Frequency.TimesPerWeek(4)))
    }

    @Test
    fun `every n days round-trips`() {
        assertEquals(Frequency.EveryNDays(3), roundTrip(Frequency.EveryNDays(3)))
    }

    @Test
    fun `unknown type decodes to daily, garbage data is tolerated`() {
        assertEquals(Frequency.Daily, HabitRepository.decodeFreq(99, "junk"))
        // Empty weekly day list is valid (no scheduled weekday yet).
        assertEquals(Frequency.Weekly(emptySet()), HabitRepository.decodeFreq(1, ""))
    }
}

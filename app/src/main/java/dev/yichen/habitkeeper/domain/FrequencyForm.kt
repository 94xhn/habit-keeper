package dev.yichen.habitkeeper.domain

import dev.yichen.habitkeeper.domain.model.Frequency

/**
 * Builds a [Frequency] from raw Add-Habit form inputs (a frequency-type index plus the
 * fields for each type). Kept pure and Android-free so the flexible-frequency feature —
 * the product's headline differentiator — is verified by unit tests, without UI tests.
 *
 * type: 0 = Daily, 1 = Weekly, 2 = TimesPerWeek, 3 = EveryNDays.
 */
object FrequencyForm {
    fun build(type: Int, weekdays: Set<Int>, timesPerWeek: String, everyNDays: String): Frequency =
        when (type) {
            1 -> Frequency.Weekly(weekdays)
            2 -> Frequency.TimesPerWeek(timesPerWeek.toIntOrNull()?.coerceIn(1, 7) ?: 1)
            3 -> Frequency.EveryNDays(everyNDays.toIntOrNull()?.coerceAtLeast(1) ?: 1)
            else -> Frequency.Daily
        }
}

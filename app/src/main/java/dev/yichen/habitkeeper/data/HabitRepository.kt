package dev.yichen.habitkeeper.data

import dev.yichen.habitkeeper.data.db.HabitDatabase
import dev.yichen.habitkeeper.data.db.HabitEntity
import dev.yichen.habitkeeper.data.db.HabitLogEntity
import dev.yichen.habitkeeper.domain.model.Frequency
import dev.yichen.habitkeeper.domain.model.Habit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Single source of truth between Room (data) and domain models.
 * Owns the Frequency <-> (freqType, freqData) flattening so neither layer leaks into the other.
 */
class HabitRepository(db: HabitDatabase) {
    private val habitDao = db.habitDao()
    private val logDao = db.habitLogDao()

    val activeHabits: Flow<List<Habit>> =
        habitDao.observeActive().map { list -> list.map { it.toDomain() } }

    suspend fun add(habit: Habit): Long = habitDao.insert(habit.toEntity())

    suspend fun update(habit: Habit) = habitDao.update(habit.toEntity())

    suspend fun setDone(habitId: Long, epochDay: Long, done: Boolean) {
        if (done) logDao.upsert(HabitLogEntity(habitId = habitId, epochDay = epochDay, done = true))
        else logDao.clear(habitId, epochDay)
    }

    suspend fun doneDays(habitId: Long): Set<Long> = logDao.doneDays(habitId).toSet()

    // --- mappers ---

    private fun HabitEntity.toDomain() = Habit(
        id = id,
        name = name,
        emoji = emoji,
        frequency = decodeFreq(freqType, freqData),
        startEpochDay = startEpochDay,
        groupId = groupId,
        sortOrder = sortOrder,
        archived = archived,
    )

    private fun Habit.toEntity(): HabitEntity {
        val (type, data) = encodeFreq(frequency)
        return HabitEntity(
            id = id,
            name = name,
            emoji = emoji,
            freqType = type,
            freqData = data,
            startEpochDay = startEpochDay,
            groupId = groupId,
            sortOrder = sortOrder,
            archived = archived,
        )
    }

    // Pure, side-effect-free codec kept in companion so it can be unit-tested without Android.
    companion object {
        fun encodeFreq(f: Frequency): Pair<Int, String> = when (f) {
            Frequency.Daily -> 0 to ""
            is Frequency.Weekly -> 1 to f.days.sorted().joinToString(",")
            is Frequency.TimesPerWeek -> 2 to f.times.toString()
            is Frequency.EveryNDays -> 3 to f.n.toString()
        }

        fun decodeFreq(type: Int, data: String): Frequency = when (type) {
            1 -> Frequency.Weekly(
                data.split(",").filter { it.isNotBlank() }.map { it.toInt() }.toSet()
            )
            2 -> Frequency.TimesPerWeek(data.toIntOrNull() ?: 1)
            3 -> Frequency.EveryNDays(data.toIntOrNull() ?: 1)
            else -> Frequency.Daily
        }
    }
}

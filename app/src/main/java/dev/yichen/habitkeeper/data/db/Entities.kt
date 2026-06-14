package dev.yichen.habitkeeper.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Room entities live in the data layer ONLY; domain models stay annotation-free (lesson A61).
// Frequency (a domain sealed type) is flattened to (freqType, freqData) here and rebuilt by
// the repository mapper — keeps the SQL schema simple and the domain layer pure.

@Entity(
    tableName = "habits",
    indices = [Index("groupId"), Index("sortOrder"), Index("archived")],
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String,
    val freqType: Int,      // 0=Daily 1=Weekly 2=TimesPerWeek 3=EveryNDays
    val freqData: String,   // Weekly:"1,3,5"  TimesPerWeek:"4"  EveryNDays:"3"  Daily:""
    val startEpochDay: Long,
    val groupId: Long?,
    val sortOrder: Int,
    val archived: Boolean,
)

@Entity(
    tableName = "habit_logs",
    indices = [Index(value = ["habitId", "epochDay"], unique = true), Index("epochDay")],
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val epochDay: Long,
    val done: Boolean,
)

@Entity(tableName = "habit_groups")
data class HabitGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sortOrder: Int,
)

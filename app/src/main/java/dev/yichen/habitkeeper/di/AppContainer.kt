package dev.yichen.habitkeeper.di

import android.content.Context
import dev.yichen.habitkeeper.data.HabitRepository
import dev.yichen.habitkeeper.data.db.HabitDatabase

/**
 * Manual DI — one tiny object graph, no framework.
 * Swap for Hilt only if/when module count or test seams demand it.
 */
class AppContainer(context: Context) {
    private val database: HabitDatabase by lazy { HabitDatabase.build(context) }
    val repository: HabitRepository by lazy { HabitRepository(database) }
}

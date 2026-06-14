package dev.yichen.habitkeeper.di

import android.content.Context
import dev.yichen.habitkeeper.data.HabitRepository
import dev.yichen.habitkeeper.data.db.HabitDatabase
import dev.yichen.habitkeeper.notify.HabitReminderScheduler

/**
 * Manual DI — one tiny object graph, no framework.
 * Swap for Hilt only if/when module count or test seams demand it.
 */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database: HabitDatabase by lazy { HabitDatabase.build(appContext) }
    val repository: HabitRepository by lazy { HabitRepository(database) }
    val reminderScheduler: HabitReminderScheduler by lazy { HabitReminderScheduler(appContext) }
}

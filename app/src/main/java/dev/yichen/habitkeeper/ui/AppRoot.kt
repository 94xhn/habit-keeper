package dev.yichen.habitkeeper.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.yichen.habitkeeper.data.HabitRepository
import dev.yichen.habitkeeper.notify.HabitReminderScheduler
import dev.yichen.habitkeeper.ui.add.AddHabitScreen
import dev.yichen.habitkeeper.ui.home.HomeScreen

/**
 * Tiny manual navigation — two screens, no navigation-compose dependency.
 */
@Composable
fun AppRoot(repo: HabitRepository, scheduler: HabitReminderScheduler) {
    var screen by rememberSaveable { mutableStateOf("home") }
    when (screen) {
        "add" -> AddHabitScreen(
            repo = repo,
            scheduler = scheduler,
            onDone = { screen = "home" },
            onCancel = { screen = "home" },
        )
        else -> HomeScreen(
            repo = repo,
            onAddClick = { screen = "add" },
        )
    }
}

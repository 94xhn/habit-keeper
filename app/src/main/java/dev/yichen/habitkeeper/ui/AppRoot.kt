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
 * Tiny manual navigation — home / add / edit, no navigation-compose dependency.
 */
@Composable
fun AppRoot(repo: HabitRepository, scheduler: HabitReminderScheduler) {
    var screen by rememberSaveable { mutableStateOf("home") }
    var editId by rememberSaveable { mutableStateOf(-1L) }

    when (screen) {
        "add" -> AddHabitScreen(
            repo = repo,
            scheduler = scheduler,
            editHabitId = null,
            onDone = { screen = "home" },
            onCancel = { screen = "home" },
        )
        "edit" -> AddHabitScreen(
            repo = repo,
            scheduler = scheduler,
            editHabitId = editId,
            onDone = { screen = "home" },
            onCancel = { screen = "home" },
        )
        else -> HomeScreen(
            repo = repo,
            onAddClick = { screen = "add" },
            onEditClick = { editId = it; screen = "edit" },
        )
    }
}

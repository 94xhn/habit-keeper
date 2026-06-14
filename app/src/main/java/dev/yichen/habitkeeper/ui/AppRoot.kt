package dev.yichen.habitkeeper.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import dev.yichen.habitkeeper.data.HabitRepository
import dev.yichen.habitkeeper.ui.add.AddHabitScreen
import dev.yichen.habitkeeper.ui.home.HomeScreen

/**
 * Tiny manual navigation — two screens, no navigation-compose dependency (same minimalism
 * as the sibling local-ledger project). Grows to a real nav graph only if screens multiply.
 */
@Composable
fun AppRoot(repo: HabitRepository) {
    var screen by rememberSaveable { mutableStateOf("home") }
    when (screen) {
        "add" -> AddHabitScreen(
            repo = repo,
            onDone = { screen = "home" },
            onCancel = { screen = "home" },
        )
        else -> HomeScreen(
            repo = repo,
            onAddClick = { screen = "add" },
        )
    }
}

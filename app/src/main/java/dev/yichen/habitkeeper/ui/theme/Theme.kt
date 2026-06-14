package dev.yichen.habitkeeper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Growth green — "completed / progress" energy, the right tone for a habit tracker.
private val Green = Color(0xFF1B6B50)

private val LightColors = lightColorScheme(primary = Green)
private val DarkColors = darkColorScheme(primary = Green)

@Composable
fun HabitKeeperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}

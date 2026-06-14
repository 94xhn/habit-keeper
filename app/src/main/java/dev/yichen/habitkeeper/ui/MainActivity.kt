package dev.yichen.habitkeeper.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.yichen.habitkeeper.HabitKeeperApp
import dev.yichen.habitkeeper.ui.theme.HabitKeeperTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = (application as HabitKeeperApp).container.repository
        setContent {
            HabitKeeperTheme {
                AppRoot(repo)
            }
        }
    }
}

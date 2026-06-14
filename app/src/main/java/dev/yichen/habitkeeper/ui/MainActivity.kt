package dev.yichen.habitkeeper.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import dev.yichen.habitkeeper.HabitKeeperApp
import dev.yichen.habitkeeper.notify.Notifications
import dev.yichen.habitkeeper.ui.theme.HabitKeeperTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* result ignored */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Notifications.ensureChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        val container = (application as HabitKeeperApp).container
        setContent {
            HabitKeeperTheme {
                AppRoot(container.repository, container.reminderScheduler)
            }
        }
    }
}

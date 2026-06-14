package dev.yichen.habitkeeper

import android.app.Application
import dev.yichen.habitkeeper.di.AppContainer

class HabitKeeperApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

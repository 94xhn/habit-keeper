package dev.yichen.habitkeeper.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.yichen.habitkeeper.data.HabitRepository
import dev.yichen.habitkeeper.domain.model.Habit
import dev.yichen.habitkeeper.notify.HabitReminderScheduler
import kotlinx.coroutines.launch

class AddHabitViewModel(
    private val repo: HabitRepository,
    private val scheduler: HabitReminderScheduler,
) : ViewModel() {

    /** Persist the habit, schedule its reminder if set, then notify caller to navigate back. */
    fun add(habit: Habit, onDone: () -> Unit) {
        viewModelScope.launch {
            val id = repo.add(habit)
            if (habit.reminderMinuteOfDay != null) {
                scheduler.schedule(habit.copy(id = id))
            }
            onDone()
        }
    }

    companion object {
        fun factory(repo: HabitRepository, scheduler: HabitReminderScheduler) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    AddHabitViewModel(repo, scheduler) as T
            }
    }
}

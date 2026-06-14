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

    /** Load an existing habit for editing (null when adding a new one). */
    suspend fun load(id: Long): Habit? = repo.getById(id)

    /** Insert a new habit, schedule its reminder if set, then navigate back. */
    fun add(habit: Habit, onDone: () -> Unit) {
        viewModelScope.launch {
            val id = repo.add(habit)
            if (habit.reminderMinuteOfDay != null) scheduler.schedule(habit.copy(id = id))
            onDone()
        }
    }

    /** Update an existing habit and re-sync its reminder (reschedule or cancel). */
    fun update(habit: Habit, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.update(habit)
            if (habit.reminderMinuteOfDay != null) scheduler.schedule(habit) else scheduler.cancel(habit.id)
            onDone()
        }
    }

    /** Delete a habit: cancel its reminder, drop the row and its logs, then navigate back. */
    fun delete(habitId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            scheduler.cancel(habitId)
            repo.delete(habitId)
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

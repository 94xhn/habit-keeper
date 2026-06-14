package dev.yichen.habitkeeper.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.yichen.habitkeeper.data.HabitRepository
import dev.yichen.habitkeeper.domain.model.Habit
import kotlinx.coroutines.launch

class AddHabitViewModel(private val repo: HabitRepository) : ViewModel() {

    /** Persist the habit, then notify the caller so it can navigate back. */
    fun add(habit: Habit, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.add(habit)
            onDone()
        }
    }

    companion object {
        fun factory(repo: HabitRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = AddHabitViewModel(repo) as T
        }
    }
}

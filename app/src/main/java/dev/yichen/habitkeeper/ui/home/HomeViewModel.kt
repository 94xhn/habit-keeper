package dev.yichen.habitkeeper.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.yichen.habitkeeper.data.HabitRepository
import dev.yichen.habitkeeper.domain.Streak
import dev.yichen.habitkeeper.domain.model.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/** A row on the home list: the habit plus its derived today/streak state. */
data class HabitRow(
    val habit: Habit,
    val doneToday: Boolean,
    val streak: Int,
)

class HomeViewModel(private val repo: HabitRepository) : ViewModel() {

    private val _rows = MutableStateFlow<List<HabitRow>>(emptyList())
    val rows: StateFlow<List<HabitRow>> = _rows.asStateFlow()

    init {
        // habits table is a Flow → adding a habit re-emits and rebuilds rows automatically.
        viewModelScope.launch {
            repo.activeHabits.collect { habits ->
                val today = LocalDate.now().toEpochDay()
                _rows.value = habits.map { it.toRow(today) }
            }
        }
    }

    /**
     * Toggle today's completion. Completion lives in the habit_logs table, which does NOT
     * re-emit the habits Flow — so after writing we re-derive just the affected row, otherwise
     * the checkbox and streak wouldn't update on screen.
     */
    fun toggleToday(habitId: Long, done: Boolean) {
        viewModelScope.launch {
            val today = LocalDate.now().toEpochDay()
            repo.setDone(habitId, today, done)
            val refreshed = _rows.value.map { row ->
                if (row.habit.id == habitId) row.habit.toRow(today) else row
            }
            _rows.value = refreshed
        }
    }

    private suspend fun Habit.toRow(today: Long): HabitRow {
        val done = repo.doneDays(id)
        return HabitRow(this, today in done, Streak.current(this, done, today))
    }

    companion object {
        fun factory(repo: HabitRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(repo) as T
        }
    }
}

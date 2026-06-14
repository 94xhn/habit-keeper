package dev.yichen.habitkeeper.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.yichen.habitkeeper.data.HabitRepository

@Composable
fun HomeScreen(repo: HabitRepository, onAddClick: () -> Unit) {
    val vm: HomeViewModel = viewModel(factory = HomeViewModel.factory(repo))
    val rows by vm.rows.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(16.dp)) {
            Text("HabitKeeper", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            if (rows.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("No habits yet. Tap \"+ Add habit\" to create one.")
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(rows, key = { it.habit.id }) { row ->
                        HabitItem(row, onToggle = { vm.toggleToday(row.habit.id, it) })
                    }
                }
            }

            Button(onClick = onAddClick, modifier = Modifier.fillMaxWidth()) {
                Text("+ Add habit")
            }
        }
    }
}

@Composable
private fun HabitItem(row: HabitRow, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = row.doneToday, onCheckedChange = onToggle)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${row.habit.emoji} ${row.habit.name}".trim(),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "🔥 ${row.streak} day streak",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

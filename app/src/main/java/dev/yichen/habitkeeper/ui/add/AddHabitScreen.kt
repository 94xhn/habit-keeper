package dev.yichen.habitkeeper.ui.add

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.yichen.habitkeeper.data.HabitRepository
import dev.yichen.habitkeeper.domain.FrequencyForm
import dev.yichen.habitkeeper.domain.ReminderTime
import dev.yichen.habitkeeper.domain.model.Habit
import dev.yichen.habitkeeper.notify.HabitReminderScheduler
import java.time.LocalDate

private val WEEKDAY_NAMES = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val FREQ_LABELS = listOf("Every day", "Specific weekdays", "N times per week", "Every N days")

@Composable
fun AddHabitScreen(
    repo: HabitRepository,
    scheduler: HabitReminderScheduler,
    onDone: () -> Unit,
    onCancel: () -> Unit,
) {
    val vm: AddHabitViewModel = viewModel(factory = AddHabitViewModel.factory(repo, scheduler))
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf("") }
    var emoji by rememberSaveable { mutableStateOf("") }
    var freqType by rememberSaveable { mutableStateOf(0) }
    var weekdays by remember { mutableStateOf(emptySet<Int>()) }
    var timesPerWeek by rememberSaveable { mutableStateOf("3") }
    var everyNDays by rememberSaveable { mutableStateOf("2") }
    var reminderEnabled by rememberSaveable { mutableStateOf(false) }
    var reminderMinute by rememberSaveable { mutableStateOf(8 * 60) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onCancel) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Text("New Habit", style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = emoji,
                onValueChange = { emoji = it },
                label = { Text("Emoji (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))

            Text("Frequency", style = MaterialTheme.typography.titleMedium)
            FREQ_LABELS.forEachIndexed { i, label ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { freqType = i },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = freqType == i, onClick = { freqType = i })
                    Text(label)
                }
            }

            when (freqType) {
                1 -> {
                    Spacer(Modifier.height(8.dp))
                    WEEKDAY_NAMES.forEachIndexed { idx, dayName ->
                        val day = idx + 1
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                weekdays = if (day in weekdays) weekdays - day else weekdays + day
                            },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = day in weekdays,
                                onCheckedChange = { checked ->
                                    weekdays = if (checked) weekdays + day else weekdays - day
                                },
                            )
                            Text(dayName)
                        }
                    }
                }
                2 -> {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = timesPerWeek,
                        onValueChange = { timesPerWeek = it.filter(Char::isDigit).take(1) },
                        label = { Text("Times per week (1-7)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                3 -> {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = everyNDays,
                        onValueChange = { everyNDays = it.filter(Char::isDigit).take(3) },
                        label = { Text("Every N days") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Daily reminder", style = MaterialTheme.typography.titleMedium)
                Switch(checked = reminderEnabled, onCheckedChange = { reminderEnabled = it })
            }
            if (reminderEnabled) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, h, m -> reminderMinute = ReminderTime.of(h, m) },
                            ReminderTime.hourOf(reminderMinute),
                            ReminderTime.minuteOf(reminderMinute),
                            true,
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Remind at ${ReminderTime.format(reminderMinute)}")
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    val habit = Habit(
                        name = name.trim(),
                        emoji = emoji.trim(),
                        frequency = FrequencyForm.build(freqType, weekdays, timesPerWeek, everyNDays),
                        startEpochDay = LocalDate.now().toEpochDay(),
                        reminderMinuteOfDay = if (reminderEnabled) reminderMinute else null,
                    )
                    vm.add(habit, onDone)
                },
                enabled = name.isNotBlank() && (freqType != 1 || weekdays.isNotEmpty()),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save")
            }
        }
    }
}

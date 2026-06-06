package ru.netology.habittracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.netology.habittracker.R
import ru.netology.habittracker.ui.components.HabitCard
import ru.netology.habittracker.viewmodel.HabitListViewModel
import ru.netology.habittracker.viewmodel.WeekDay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HabitListScreen(
    onAddHabit: () -> Unit,
    onEditHabit: (String) -> Unit,
    onViewHabit: (String) -> Unit,
    viewModel: HabitListViewModel = viewModel()
) {
    val habits by viewModel.habits.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showDeleteConfirmation by viewModel.showDeleteConfirmation.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentWeekDays by viewModel.currentWeekDays.collectAsState()
    val currentWeekStart by viewModel.currentWeekStart.collectAsState()

    val todayDate = remember { viewModel.getTodayDate() }
    val isTodaySelected = selectedDate == todayDate

    // Форматируем отображение текущей недели без парсинга дат
    val weekRange = remember(currentWeekStart) {
        val sdf = SimpleDateFormat("dd.MM", Locale.getDefault())
        try {
            val calendar = Calendar.getInstance()
            val startDate = sdf.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(currentWeekStart) ?: Date())
            val endCalendar = Calendar.getInstance()
            endCalendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(currentWeekStart) ?: Date()
            endCalendar.add(Calendar.DAY_OF_MONTH, 6)
            val endDate = sdf.format(endCalendar.time)
            "$startDate - $endDate"
        } catch (e: Exception) {
            ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        },
        floatingActionButton = {
            if (isTodaySelected) {
                FloatingActionButton(
                    onClick = onAddHabit,
                    backgroundColor = MaterialTheme.colors.secondary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить привычку")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Навигация по неделям
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goToPreviousWeek() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Предыдущая неделя")
                    }

                    Text(
                        text = weekRange,
                        style = MaterialTheme.typography.subtitle1
                    )

                    IconButton(onClick = { viewModel.goToNextWeek() }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Следующая неделя")
                    }

                    IconButton(onClick = { viewModel.goToCurrentWeek() }) {
                        Icon(Icons.Default.Today, contentDescription = "Текущая неделя")
                    }
                }
            }

            // Календарь недели
            WeekCalendar(
                weekDays = currentWeekDays,
                onDateSelected = { viewModel.updateSelectedDate(it) }
            )

            // Информация о режиме
            if (!isTodaySelected) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Режим просмотра. Для редактирования и отметки выполнения выберите сегодняшний день.",
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colors.primary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    habits.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.no_habits),
                                style = MaterialTheme.typography.body1
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.add_first_habit),
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(habits) { habit ->
                                val isCompleted = viewModel.getCompletionStatus(habit.id, selectedDate)
                                val progressPercent = viewModel.getProgressPercent(habit)

                                HabitCard(
                                    habit = habit,
                                    priorityColor = viewModel.getPriorityColor(habit.priority),
                                    isCompleted = isCompleted,
                                    progressPercent = progressPercent,
                                    isEditable = isTodaySelected,
                                    onCardClick = { onViewHabit(habit.id) },
                                    onEditClick = {
                                        if (isTodaySelected) onEditHabit(habit.id)
                                    },
                                    onDeleteClick = {
                                        if (isTodaySelected) viewModel.showDeleteConfirmation(habit)
                                    },
                                    onToggleCompletion = {
                                        if (isTodaySelected) viewModel.toggleCompletion(habit, selectedDate)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmation != null && isTodaySelected) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteConfirmation() },
            title = { Text("Удаление привычки") },
            text = { Text("Вы уверены, что хотите удалить привычку \"${showDeleteConfirmation?.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation?.let { viewModel.deleteHabit(it) }
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteConfirmation() }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun WeekCalendar(
    weekDays: List<WeekDay>,
    onDateSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDays.forEach { day ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (day.isSelected) MaterialTheme.colors.primary
                            else Color.Transparent
                        )
                        .clickable { onDateSelected(day.date) }
                        .padding(8.dp)
                ) {
                    Text(
                        text = day.dayName,
                        style = MaterialTheme.typography.caption,
                        color = when {
                            day.isSelected -> MaterialTheme.colors.onPrimary
                            day.isToday -> MaterialTheme.colors.primary
                            else -> MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = day.dayNumber,
                        style = MaterialTheme.typography.body2,
                        color = when {
                            day.isSelected -> MaterialTheme.colors.onPrimary
                            day.isToday -> MaterialTheme.colors.primary
                            else -> MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        }
                    )
                }
            }
        }
    }
}
package ru.netology.habittracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.netology.habittracker.data.Frequency  // Добавьте этот импорт
import ru.netology.habittracker.data.Priority
import ru.netology.habittracker.viewmodel.HabitDetailViewModel
import ru.netology.habittracker.viewmodel.HabitListViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale

@Composable
fun HabitDetailScreen(
    navController: androidx.navigation.NavController,
    habitId: String,
    onEdit: () -> Unit,
    habitDetailViewModel: HabitDetailViewModel,
    habitListViewModel: HabitListViewModel
) {
    val habit by habitDetailViewModel.habit.collectAsState()
    val isLoading by habitDetailViewModel.isLoading.collectAsState()
    val weeklyProgress by habitDetailViewModel.weeklyProgress.collectAsState()
    val weeklyProgressPercent by habitDetailViewModel.weeklyProgressPercent.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(habit?.title ?: "Детали привычки") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                habit == null -> {
                    Text(
                        text = "Привычка не найдена",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = habit!!.title,
                                    style = MaterialTheme.typography.h6
                                )

                                Divider()

                                Text(
                                    text = habit!!.description,
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }

                        // Прогресс по дням недели
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Прогресс за неделю",
                                    style = MaterialTheme.typography.subtitle1
                                )

                                WeeklyProgressGrid(
                                    weeklyProgress = weeklyProgress,
                                    habitFrequency = habit!!.frequency,
                                    progressPercent = weeklyProgressPercent,
                                    onDayClick = { date ->
                                        habitListViewModel.updateSelectedDate(date)
                                        navController.navigateUp()
                                    }
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DetailRow(
                                    label = "Частота",
                                    value = habitDetailViewModel.getFrequencyText(habit!!.frequency)
                                )

                                DetailRow(
                                    label = "Приоритет",
                                    value = habitDetailViewModel.getPriorityText(habit!!.priority)
                                )

                                DetailRow(
                                    label = "Создана",
                                    value = habitDetailViewModel.getFormattedDate(habit!!.createdAt)
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 4.dp,
                            backgroundColor = when (habit!!.priority) {
                                Priority.LOW -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                                Priority.MEDIUM -> Color(0xFFFFC107).copy(alpha = 0.1f)
                                Priority.HIGH -> Color(0xFFF44336).copy(alpha = 0.1f)
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = when (habit!!.priority) {
                                        Priority.LOW -> "Низкий приоритет"
                                        Priority.MEDIUM -> "Средний приоритет"
                                        Priority.HIGH -> "Высокий приоритет"
                                    },
                                    style = MaterialTheme.typography.subtitle1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun WeeklyProgressGrid(
    weeklyProgress: Map<String, Boolean>,
    habitFrequency: Frequency,
    progressPercent: Float,
    onDayClick: (String) -> Unit
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", LocalLocale.current.platformLocale)
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Пояснение
        Text(
            text = if (habitFrequency == Frequency.DAILY) {
                "Отмечайте дни выполнения привычки"
            } else {
                "Достаточно отметить один день в неделе"
            },
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )

        // Прогресс
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (habitFrequency == Frequency.DAILY) "Прогресс за неделю" else "Выполнено за неделю",
                style = MaterialTheme.typography.body2
            )
            Text(
                text = if (habitFrequency == Frequency.DAILY) {
                    "${progressPercent.toInt()}%"
                } else {
                    if (progressPercent == 100f) "✓ Выполнено" else "✗ Не выполнено"
                },
                style = MaterialTheme.typography.body2,
                color = if (progressPercent == 100f) Color(0xFF4CAF50) else Color.Gray
            )
        }

        // Дни недели
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС").forEach { dayName ->
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // Даты с статусами
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0..6) {
                val date = sdf.format(calendar.time)
                val isCompleted = weeklyProgress[date] ?: false

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isCompleted) Color(0xFF4CAF50)
                            else Color.LightGray.copy(alpha = 0.3f)
                        )
                        .clickable { onDayClick(date) }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = calendar.get(Calendar.DAY_OF_MONTH).toString(),
                        style = MaterialTheme.typography.body2,
                        color = if (isCompleted) Color.White else Color.Black
                    )
                }

                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }
}
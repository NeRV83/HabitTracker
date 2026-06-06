package ru.netology.habittracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.netology.habittracker.data.Priority
import ru.netology.habittracker.viewmodel.HabitDetailViewModel
import ru.netology.habittracker.viewmodel.HabitDetailViewModelFactory

@Composable
fun HabitDetailScreen(
    navController: NavController,
    habitId: String,
    onEdit: () -> Unit
) {
    val viewModel: HabitDetailViewModel = viewModel(
        key = habitId,
        factory = HabitDetailViewModelFactory(habitId)
    )

    val habit by viewModel.habit.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
                                    value = viewModel.getFrequencyText(habit!!.frequency)
                                )

                                DetailRow(
                                    label = "Приоритет",
                                    value = viewModel.getPriorityText(habit!!.priority)
                                )

                                DetailRow(
                                    label = "Создана",
                                    value = viewModel.getFormattedDate(habit!!.createdAt)
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
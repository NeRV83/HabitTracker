package ru.netology.habittracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.netology.habittracker.R
import ru.netology.habittracker.data.Habit
import ru.netology.habittracker.ui.components.HabitCard
import ru.netology.habittracker.viewmodel.HabitListViewModel

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit,
                backgroundColor = MaterialTheme.colors.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить привычку")
            }
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
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(habits) { habit ->
                            HabitCard(
                                habit = habit,
                                priorityColor = viewModel.getPriorityColor(habit.priority),
                                onCardClick = { onViewHabit(habit.id) },
                                onEditClick = { onEditHabit(habit.id) },
                                onDeleteClick = { viewModel.showDeleteConfirmation(habit) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmation != null) {
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
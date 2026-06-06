package ru.netology.habittracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.netology.habittracker.R
import ru.netology.habittracker.data.Frequency
import ru.netology.habittracker.data.Priority
import ru.netology.habittracker.viewmodel.AddEditHabitViewModel

@Composable
fun AddEditHabitScreen(
    navController: NavController,
    habitId: String?,
    viewModel: AddEditHabitViewModel = viewModel()
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val frequency by viewModel.frequency.collectAsState()
    val priority by viewModel.priority.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val canSave by viewModel.canSave.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) stringResource(R.string.edit_habit) else stringResource(R.string.add_habit)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveHabit {
                                navController.navigateUp()
                            }
                        },
                        enabled = canSave
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Сохранить")
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.frequency),
                        style = MaterialTheme.typography.subtitle1
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Frequency.values().forEach { freq ->
                            val text = when (freq) {
                                Frequency.DAILY -> stringResource(R.string.daily)
                                Frequency.WEEKLY -> stringResource(R.string.weekly)
                            }

                            Button(
                                onClick = { viewModel.updateFrequency(freq) },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (frequency == freq)
                                        MaterialTheme.colors.primary
                                    else
                                        MaterialTheme.colors.surface,
                                    contentColor = if (frequency == freq)
                                        MaterialTheme.colors.onPrimary
                                    else
                                        MaterialTheme.colors.onSurface
                                )
                            ) {
                                Text(text)
                            }
                        }
                    }
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
                    Text(
                        text = stringResource(R.string.priority),
                        style = MaterialTheme.typography.subtitle1
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Priority.values().forEach { prio ->
                            val text = when (prio) {
                                Priority.LOW -> stringResource(R.string.low)
                                Priority.MEDIUM -> stringResource(R.string.medium)
                                Priority.HIGH -> stringResource(R.string.high)
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                RadioButton(
                                    selected = priority == prio,
                                    onClick = { viewModel.updatePriority(prio) }
                                )
                                Text(
                                    text = text,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
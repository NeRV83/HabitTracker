package ru.netology.habittracker.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.habittracker.App
import ru.netology.habittracker.data.Frequency
import ru.netology.habittracker.data.Habit
import ru.netology.habittracker.data.Priority

class AddEditHabitViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val repository = App.repository

    private val _habitId = MutableStateFlow<String?>(savedStateHandle["habitId"])
    val habitId: StateFlow<String?> = _habitId.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _frequency = MutableStateFlow(Frequency.DAILY)
    val frequency: StateFlow<Frequency> = _frequency.asStateFlow()

    private val _priority = MutableStateFlow(Priority.MEDIUM)
    val priority: StateFlow<Priority> = _priority.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    private val _canSave = MutableStateFlow(false)
    val canSave: StateFlow<Boolean> = _canSave.asStateFlow()

    init {
        loadHabitIfNeeded()
    }

    private fun loadHabitIfNeeded() {
        viewModelScope.launch {
            _habitId.value?.let { id ->
                val habit = repository.getHabitById(id)
                if (habit != null) {
                    _title.value = habit.title
                    _description.value = habit.description
                    _frequency.value = habit.frequency
                    _priority.value = habit.priority
                    _isEditMode.value = true
                    updateCanSave()
                }
            }
        }
    }

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
        updateCanSave()
    }

    fun updateDescription(newDescription: String) {
        _description.value = newDescription
        updateCanSave()
    }

    fun updateFrequency(newFrequency: Frequency) {
        _frequency.value = newFrequency
        updateCanSave()
    }

    fun updatePriority(newPriority: Priority) {
        _priority.value = newPriority
        updateCanSave()
    }

    private fun updateCanSave() {
        _canSave.value = _title.value.isNotBlank() && _description.value.isNotBlank()
    }

    fun saveHabit(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val habit = Habit(
                id = _habitId.value ?: java.util.UUID.randomUUID().toString(),
                title = _title.value,
                description = _description.value,
                frequency = _frequency.value,
                priority = _priority.value
            )

            if (_isEditMode.value) {
                repository.updateHabit(habit)
            } else {
                repository.addHabit(habit)
            }
            onSuccess()
        }
    }
}
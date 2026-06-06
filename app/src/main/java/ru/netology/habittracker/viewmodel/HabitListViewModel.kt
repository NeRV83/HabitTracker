package ru.netology.habittracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.habittracker.App
import ru.netology.habittracker.data.Habit
import ru.netology.habittracker.data.Priority

class HabitListViewModel : ViewModel() {
    private val repository = App.repository

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showDeleteConfirmation = MutableStateFlow<Habit?>(null)
    val showDeleteConfirmation: StateFlow<Habit?> = _showDeleteConfirmation.asStateFlow()

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllHabits().collect { habitList ->
                _habits.value = habitList
                _isLoading.value = false
            }
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit.id)
            _showDeleteConfirmation.value = null
        }
    }

    fun showDeleteConfirmation(habit: Habit) {
        _showDeleteConfirmation.value = habit
    }

    fun hideDeleteConfirmation() {
        _showDeleteConfirmation.value = null
    }

    fun getPriorityColor(priority: Priority): Int {
        return when (priority) {
            Priority.LOW -> android.graphics.Color.parseColor("#4CAF50")
            Priority.MEDIUM -> android.graphics.Color.parseColor("#FFC107")
            Priority.HIGH -> android.graphics.Color.parseColor("#F44336")
        }
    }
}
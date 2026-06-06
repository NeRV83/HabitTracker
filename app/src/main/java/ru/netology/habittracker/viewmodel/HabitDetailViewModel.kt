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
import java.text.SimpleDateFormat
import java.util.Locale

class HabitDetailViewModel(
    private val habitId: String
) : ViewModel() {
    private val repository = App.repository

    private val _habit = MutableStateFlow<Habit?>(null)
    val habit: StateFlow<Habit?> = _habit.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadHabit()
    }

    private fun loadHabit() {
        viewModelScope.launch {
            _isLoading.value = true
            _habit.value = repository.getHabitById(habitId)
            _isLoading.value = false
        }
    }

    fun getFormattedDate(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return format.format(date)
    }

    fun getPriorityText(priority: Priority): String {
        return when (priority) {
            Priority.LOW -> "Низкий"
            Priority.MEDIUM -> "Средний"
            Priority.HIGH -> "Высокий"
        }
    }

    fun getFrequencyText(frequency: Frequency): String {
        return when (frequency) {
            Frequency.DAILY -> "Ежедневно"
            Frequency.WEEKLY -> "Еженедельно"
        }
    }
}
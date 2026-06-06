package ru.netology.habittracker.viewmodel

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
import java.util.*

class HabitDetailViewModel(
    private val habitId: String
) : ViewModel() {
    private val repository = App.repository

    private val _habit = MutableStateFlow<Habit?>(null)
    val habit: StateFlow<Habit?> = _habit.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _weeklyProgress = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val weeklyProgress: StateFlow<Map<String, Boolean>> = _weeklyProgress.asStateFlow()

    private val _weeklyProgressPercent = MutableStateFlow(0f)
    val weeklyProgressPercent: StateFlow<Float> = _weeklyProgressPercent.asStateFlow()

    init {
        loadHabit()
        loadWeeklyProgress()
    }

    private fun loadHabit() {
        viewModelScope.launch {
            _isLoading.value = true
            _habit.value = repository.getHabitById(habitId)
            _isLoading.value = false
        }
    }

    private fun loadWeeklyProgress() {
        viewModelScope.launch {
            repository.getAllHabits().collect { habits ->
                val habit = habits.find { it.id == habitId }
                if (habit != null) {
                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val weekDays = mutableMapOf<String, Boolean>()

                    for (i in 0..6) {
                        val date = sdf.format(calendar.time)
                        weekDays[date] = habit.completions[date] ?: false
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                    }
                    _weeklyProgress.value = weekDays

                    // Вычисляем прогресс в процентах
                    val progressPercent = when (habit.frequency) {
                        Frequency.DAILY -> {
                            val completedCount = weekDays.values.count { it }
                            (completedCount.toFloat() / 7f) * 100f
                        }
                        Frequency.WEEKLY -> {
                            if (weekDays.values.any { it }) 100f else 0f
                        }
                    }
                    _weeklyProgressPercent.value = progressPercent
                }
            }
        }
    }

    fun getFormattedDate(timestamp: Long): String {
        val date = Date(timestamp)
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
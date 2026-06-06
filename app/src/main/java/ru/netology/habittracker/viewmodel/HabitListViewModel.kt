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

class HabitListViewModel : ViewModel() {
    private val repository = App.repository

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showDeleteConfirmation = MutableStateFlow<Habit?>(null)
    val showDeleteConfirmation: StateFlow<Habit?> = _showDeleteConfirmation.asStateFlow()

    private val _selectedDate = MutableStateFlow(getTodayDate())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _currentWeekStart = MutableStateFlow(getStartOfCurrentWeek())
    val currentWeekStart: StateFlow<String> = _currentWeekStart.asStateFlow()

    private val _currentWeekDays = MutableStateFlow<List<WeekDay>>(emptyList())
    val currentWeekDays: StateFlow<List<WeekDay>> = _currentWeekDays.asStateFlow()

    init {
        loadHabits()
        updateWeekDays()
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

    fun toggleCompletion(habit: Habit, date: String) {
        viewModelScope.launch {
            val currentStatus = repository.getCompletionStatus(habit.id, date)
            repository.toggleCompletion(habit.id, date, !currentStatus)
        }
    }

    fun getCompletionStatus(habitId: String, date: String): Boolean {
        val habit = _habits.value.find { it.id == habitId }
        return habit?.completions?.get(date) ?: false
    }

    fun getProgressPercent(habit: Habit): Float {
        val weeklyCompletions = getWeeklyCompletions(habit)

        return when (habit.frequency) {
            Frequency.DAILY -> {
                val completedCount = weeklyCompletions.values.count { it }
                (completedCount.toFloat() / 7f) * 100f
            }
            Frequency.WEEKLY -> {
                if (weeklyCompletions.values.any { it }) 100f else 0f
            }
        }
    }

    private fun getWeeklyCompletions(habit: Habit): Map<String, Boolean> {
        val calendar = getCalendarForWeekStart(_currentWeekStart.value)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val weekDays = mutableMapOf<String, Boolean>()

        for (i in 0..6) {
            val date = sdf.format(calendar.time)
            weekDays[date] = habit.completions[date] ?: false
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return weekDays
    }

    fun updateSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Calendar.getInstance().time)
    }

    private fun getStartOfCurrentWeek(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }

    private fun getCalendarForWeekStart(weekStart: String): Calendar {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(weekStart) ?: Calendar.getInstance().time
        return calendar
    }

    private fun updateWeekDays() {
        val calendar = getCalendarForWeekStart(_currentWeekStart.value)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = getTodayDate()

        val weekDays = mutableListOf<WeekDay>()
        for (i in 0..6) {
            val date = sdf.format(calendar.time)
            val dayName = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "ПН"
                Calendar.TUESDAY -> "ВТ"
                Calendar.WEDNESDAY -> "СР"
                Calendar.THURSDAY -> "ЧТ"
                Calendar.FRIDAY -> "ПТ"
                Calendar.SATURDAY -> "СБ"
                Calendar.SUNDAY -> "ВС"
                else -> ""
            }
            weekDays.add(
                WeekDay(
                    date = date,
                    dayName = dayName,
                    dayNumber = date.substring(8, 10),
                    isToday = date == todayDate,
                    isSelected = date == _selectedDate.value
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        _currentWeekDays.value = weekDays
    }

    fun goToPreviousWeek() {
        val calendar = getCalendarForWeekStart(_currentWeekStart.value)
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        _currentWeekStart.value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        updateWeekDays()
    }

    fun goToNextWeek() {
        val calendar = getCalendarForWeekStart(_currentWeekStart.value)
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        _currentWeekStart.value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        updateWeekDays()
    }

    fun goToCurrentWeek() {
        _currentWeekStart.value = getStartOfCurrentWeek()
        updateWeekDays()
        _selectedDate.value = getTodayDate()
    }

    fun getPriorityColor(priority: Priority): Int {
        return when (priority) {
            Priority.LOW -> android.graphics.Color.parseColor("#4CAF50")
            Priority.MEDIUM -> android.graphics.Color.parseColor("#FFC107")
            Priority.HIGH -> android.graphics.Color.parseColor("#F44336")
        }
    }
}

data class WeekDay(
    val date: String,
    val dayName: String,
    val dayNumber: String,
    val isToday: Boolean,
    val isSelected: Boolean
)
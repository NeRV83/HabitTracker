package ru.netology.habittracker.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryHabitRepository : HabitRepository {
    private val _habits = MutableStateFlow<Map<String, Habit>>(emptyMap())

    override fun getAllHabits(): Flow<List<Habit>> = _habits.asStateFlow()
        .map { it.values.toList() }

    override suspend fun getHabitById(id: String): Habit? = _habits.value[id]

    override suspend fun addHabit(habit: Habit) {
        _habits.update { currentMap ->
            currentMap.toMutableMap().apply {
                put(habit.id, habit)
            }
        }
    }

    override suspend fun updateHabit(habit: Habit) {
        _habits.update { currentMap ->
            if (currentMap.containsKey(habit.id)) {
                currentMap.toMutableMap().apply {
                    put(habit.id, habit)
                }
            } else {
                currentMap
            }
        }
    }

    override suspend fun deleteHabit(id: String) {
        _habits.update { currentMap ->
            currentMap.toMutableMap().apply {
                remove(id)
            }
        }
    }

    override suspend fun toggleCompletion(habitId: String, date: String, completed: Boolean) {
        _habits.update { currentMap ->
            val habit = currentMap[habitId] ?: return@update currentMap
            val newCompletions = habit.completions.toMutableMap().apply {
                put(date, completed)
            }
            val updatedHabit = habit.copy(completions = newCompletions)
            currentMap.toMutableMap().apply {
                put(habitId, updatedHabit)
            }
        }
    }

    override suspend fun getCompletionStatus(habitId: String, date: String): Boolean {
        return _habits.value[habitId]?.completions?.get(date) ?: false
    }

    override suspend fun getWeeklyProgress(habitId: String): Map<String, Boolean> {
        val habit = _habits.value[habitId] ?: return emptyMap()
        val calendar = java.util.Calendar.getInstance()
        val currentDate = java.util.Calendar.getInstance()

        val weekDays = mutableMapOf<String, Boolean>()

        // Получаем начало недели (понедельник)
        calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)

        for (i in 0..6) {
            val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(calendar.time)
            weekDays[date] = habit.completions[date] ?: false
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }

        return weekDays
    }
}
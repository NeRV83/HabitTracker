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
}
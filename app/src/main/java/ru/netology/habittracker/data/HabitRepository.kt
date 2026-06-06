package ru.netology.habittracker.data

import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getAllHabits(): Flow<List<Habit>>
    suspend fun getHabitById(id: String): Habit?
    suspend fun addHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(id: String)
    suspend fun toggleCompletion(habitId: String, date: String, completed: Boolean)
    suspend fun getCompletionStatus(habitId: String, date: String): Boolean
    suspend fun getWeeklyProgress(habitId: String): Map<String, Boolean>
}
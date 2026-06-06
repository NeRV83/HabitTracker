package ru.netology.habittracker.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.*

class SharedPrefsHabitRepository(private val context: Context) : HabitRepository {

    companion object {
        private const val PREF_NAME = "habits_prefs"
        private const val HABITS_KEY = "habits_list"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun getAllHabits(): Flow<List<Habit>> {
        return callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == HABITS_KEY) {
                    trySend(getHabitsFromPrefs())
                }
            }
            prefs.registerOnSharedPreferenceChangeListener(listener)
            trySend(getHabitsFromPrefs())

            awaitClose {
                prefs.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }

    override suspend fun getHabitById(id: String): Habit? {
        return getHabitsFromPrefs().find { it.id == id }
    }

    override suspend fun addHabit(habit: Habit) {
        val currentHabits = getHabitsFromPrefs().toMutableList()
        currentHabits.add(habit)
        saveHabits(currentHabits)
    }

    override suspend fun updateHabit(habit: Habit) {
        val currentHabits = getHabitsFromPrefs().toMutableList()
        val index = currentHabits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            currentHabits[index] = habit
            saveHabits(currentHabits)
        }
    }

    override suspend fun deleteHabit(id: String) {
        val currentHabits = getHabitsFromPrefs().toMutableList()
        currentHabits.removeAll { it.id == id }
        saveHabits(currentHabits)
    }

    override suspend fun toggleCompletion(habitId: String, date: String, completed: Boolean) {
        val habit = getHabitById(habitId) ?: return
        val newCompletions = habit.completions.toMutableMap().apply {
            put(date, completed)
        }
        val updatedHabit = habit.copy(completions = newCompletions)
        updateHabit(updatedHabit)
    }

    override suspend fun getCompletionStatus(habitId: String, date: String): Boolean {
        return getHabitById(habitId)?.completions?.get(date) ?: false
    }

    override suspend fun getWeeklyProgress(habitId: String): Map<String, Boolean> {
        val habit = getHabitById(habitId) ?: return emptyMap()
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val weekDays = mutableMapOf<String, Boolean>()
        for (i in 0..6) {
            val date = sdf.format(calendar.time)
            weekDays[date] = habit.completions[date] ?: false
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return weekDays
    }

    private fun getHabitsFromPrefs(): List<Habit> {
        val json = prefs.getString(HABITS_KEY, null)
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            try {
                val type = object : TypeToken<List<Habit>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    private fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString(HABITS_KEY, json).apply()
    }
}
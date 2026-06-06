package ru.netology.habittracker

import android.app.Application
import ru.netology.habittracker.data.HabitRepository
import ru.netology.habittracker.data.InMemoryHabitRepository

class App : Application() {
    companion object {
        lateinit var instance: App
            private set
        val repository: HabitRepository by lazy {
            InMemoryHabitRepository()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
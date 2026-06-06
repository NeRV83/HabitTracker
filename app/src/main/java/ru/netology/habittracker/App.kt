package ru.netology.habittracker

import android.app.Application
import ru.netology.habittracker.data.HabitRepository
import ru.netology.habittracker.data.SharedPrefsHabitRepository

class App : Application() {
    companion object {
        lateinit var instance: App
            private set

        lateinit var repository: HabitRepository
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        repository = SharedPrefsHabitRepository(this)
    }
}
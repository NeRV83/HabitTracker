package ru.netology.habittracker.data

import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val frequency: Frequency,
    val priority: Priority,
    val createdAt: Long = System.currentTimeMillis()
)

enum class Frequency {
    DAILY,
    WEEKLY
}

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}
package com.ktunailab.ailabapp.data.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val detayAciklamasi: String = "",
    val takimKaptani: String = "",
    val dueDate: String,
    val dueTime: String,
    val status: TaskStatus
)

enum class TaskStatus {
    TO_DO,
    IN_PROGRESS,
    DONE
}

enum class TaskFilter {
    ALL,
    TO_DO,
    IN_PROGRESS,
    DONE
}
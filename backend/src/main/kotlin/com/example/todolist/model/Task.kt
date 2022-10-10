package com.example.todolist.model

import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document
@Serializable
data class Task(
    @Id
    var id: String = "",
    var message: String = "",
    var completed: Completed = Completed.N,
    var priority: Priority = Priority.MEDIUM,
    var reminderTime: String = ""
)

enum class Completed {
    Y, N
}

enum class Priority {
    HIGH, MEDIUM, LOW
}
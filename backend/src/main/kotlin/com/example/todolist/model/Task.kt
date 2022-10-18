package com.example.todolist.model

import com.example.todolist.service.util.DateSerializer
import com.example.todolist.service.util.UuidSerializer
import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
@Serializable
data class Task(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val completed: Completed,
    val priority: Priority,

    @Serializable(with = DateSerializer::class)
    val reminderTime: Date
)

@Serializable
@JvmInline
value class TaskId(
    @Serializable(with = UuidSerializer::class)
    val value: UUID
)

@JvmInline
@Serializable
value class Message(val value: String)

@JvmInline
@Serializable
value class ReminderTime(@Serializable(DateSerializer::class) val raw: Date)

enum class Completed {
    Y, N
}

enum class Priority {
    HIGH, MEDIUM, LOW
}
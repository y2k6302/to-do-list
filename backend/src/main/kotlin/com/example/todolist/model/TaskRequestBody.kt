package com.example.todolist.model

import com.example.todolist.service.util.DateSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class TaskRequestBody(
    val message: String,
    val completed: Completed,
    val priority: Priority,
    val reminderTime: String
)
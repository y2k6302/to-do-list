package com.example.todolist.model

import kotlinx.serialization.Serializable

@Serializable
data class CustomResponse(
    var message: String = "",
)

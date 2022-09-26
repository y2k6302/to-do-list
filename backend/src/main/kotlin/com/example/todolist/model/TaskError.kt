package com.example.todolist.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.http.ResponseEntity

sealed class TaskError {
    data class NoSuchElementError(val t: Throwable) : TaskError()
    data class DatabaseError(val t: Throwable) : TaskError()

    companion object {
        fun toResponse(taskError: TaskError): ResponseEntity<String> = when (taskError) {
            is DatabaseError -> {
                ResponseEntity.internalServerError().body(Json.encodeToString(ErrorResponse("Internal server error.")))
            }

            is NoSuchElementError -> {
                ResponseEntity.badRequest().body(Json.encodeToString(ErrorResponse("Value not present or request was malformed.")))
            }
        }
    }

}

@Serializable
private data class ErrorResponse(
    var message: String = ""
)


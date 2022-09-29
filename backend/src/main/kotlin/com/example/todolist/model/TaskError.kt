package com.example.todolist.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.http.ResponseEntity

sealed class TaskError {
    data class NoSuchElementError(val t: Throwable) : TaskError()
    data class DatabaseError(val t: Throwable) : TaskError()

    companion object {
        fun toResponse(taskError: TaskError): ResponseEntity<String> = when (taskError) {
            is DatabaseError -> {
                ResponseEntity.internalServerError().body(Json.encodeToString(CustomResponse("Internal server error.")))
            }

            is NoSuchElementError -> {
                ResponseEntity.badRequest()
                    .body(Json.encodeToString(CustomResponse("Value not present or request was malformed.")))
            }
        }
    }
}

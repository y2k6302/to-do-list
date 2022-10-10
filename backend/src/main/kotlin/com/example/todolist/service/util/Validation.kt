package com.example.todolist.service.util

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.todolist.model.Task
import com.example.todolist.model.TaskError
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Validation {
    companion object {
        fun checkTaskReqBody(task: Task): Either<TaskError, Task> {
            return if (task.message.isEmpty()) {
                TaskError.InvalidRequestError(Throwable("Field must not be empty(ex. message)")).left()
            } else {
                task.right()
            }
        }

        fun checkDecodeJson(json: String): Either<TaskError, Task> {
            return Either.catch {
                Json.decodeFromString<Task>(json)
            }.mapLeft {
                TaskError.InvalidRequestError(it)
            }
        }
    }
}
package com.example.todolist.service.util

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.todolist.model.TaskError
import com.example.todolist.model.TaskRequestBody

class Validation {
    companion object {
        fun checkTaskReqBody(task: TaskRequestBody): Either<TaskError, TaskRequestBody> {
            return if (task.message.isEmpty()) {
                TaskError.InvalidRequestError(Throwable("Field must not be empty(ex. message)")).left()
            } else {
                task.right()
            }
        }
    }
}
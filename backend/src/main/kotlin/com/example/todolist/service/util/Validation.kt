package com.example.todolist.service.util

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.example.todolist.model.Task
import com.example.todolist.model.TaskError

class Validation {
    companion object {
        fun checkTaskReqBody(task: Task): Either<TaskError, Task> {
            return if (task.message.isEmpty() || task.completed.isEmpty() || task.priority.isEmpty()) {
                TaskError.InvalidRequestError(Throwable("Field must not be empty(ex. message, completed, priority)")).left()
            } else {
                task.right()
            }
        }
    }
}
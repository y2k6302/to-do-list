package com.example.todolist.service

import arrow.core.Either
import com.example.todolist.model.*
import com.example.todolist.repository.TaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat

@Service
class TaskService {

    @Autowired
    private lateinit var taskRepository: TaskRepository

    private val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    fun getTasks(): Either<TaskError.DatabaseError, List<Task>> {
        return Either.catch {
            taskRepository.findAll()
        }.mapLeft {
            TaskError.DatabaseError(it)
        }
    }

    fun getTaskById(id: String): Either<TaskError, Task> {
        return Either.catch {
            taskRepository.findById(id).get()
        }.mapLeft {
            if (it is NoSuchElementException) {
                TaskError.NoSuchElementError(it)
            } else {
                TaskError.DatabaseError(it)
            }
        }
    }

    fun createTask(task: TaskRequestBody): Either<TaskError.DatabaseError, Task> {
        return Either.catch {
            val newTask = Task(
                message = task.message,
                priority = task.priority,
                completed = task.completed,
                reminderTime = formatter.parse(task.reminderTime)
            )
            taskRepository.save(newTask)
        }.mapLeft {
            TaskError.DatabaseError(it)
        }
    }

    fun completeTask(id: String): Either<TaskError, Task> {
        return Either.catch {
            val task = taskRepository.findById(id).get()
            val newTask = Task(
                id = task.id,
                message = task.message,
                priority = task.priority,
                completed = Completed.Y,
                reminderTime = task.reminderTime
            )
            taskRepository.save(newTask)
        }.mapLeft {
            if (it is NoSuchElementException) {
                TaskError.NoSuchElementError(it)
            } else {
                TaskError.DatabaseError(it)
            }
        }
    }

    fun reopenTask(id: String): Either<TaskError, Task> {
        return Either.catch {
            val task = taskRepository.findById(id).get()
            val newTask = Task(
                id = task.id,
                message = task.message,
                completed = Completed.N,
                priority = task.priority,
                reminderTime = task.reminderTime
            )
            taskRepository.save(newTask)
        }.mapLeft {
            if (it is NoSuchElementException) {
                TaskError.NoSuchElementError(it)
            } else {
                TaskError.DatabaseError(it)
            }
        }
    }

    fun updateTask(id: String, reqTask: TaskRequestBody): Either<TaskError, Task> {
        return Either.catch {
            val originalTask = taskRepository.findById(id).get()
            val updateTask = Task(
                id = originalTask.id,
                message = reqTask.message,
                priority = reqTask.priority,
                completed = reqTask.completed,
                reminderTime = formatter.parse(reqTask.reminderTime)
            )
            taskRepository.save(updateTask)
        }.mapLeft {
            if (it is NoSuchElementException) {
                TaskError.NoSuchElementError(it)
            } else {
                TaskError.DatabaseError(it)
            }
        }
    }

    fun deleteTask(id: String): Either<TaskError, CustomResponse> {
        return Either.catch {
            val deleteTask = taskRepository.findById(id).get()
            taskRepository.delete(deleteTask)
            CustomResponse(id)
        }.mapLeft {
            if (it is NoSuchElementException) {
                TaskError.NoSuchElementError(it)
            } else {
                TaskError.DatabaseError(it)
            }
        }
    }
}
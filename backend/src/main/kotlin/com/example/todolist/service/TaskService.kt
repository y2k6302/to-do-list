package com.example.todolist.service

import arrow.core.Either
import com.example.todolist.model.Completed
import com.example.todolist.model.CustomResponse
import com.example.todolist.model.Task
import com.example.todolist.model.TaskError
import com.example.todolist.repository.TaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.NoSuchElementException

@Service
class TaskService {

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @Autowired
    private lateinit var sequenceGeneratorService: SequenceGeneratorService

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

    fun createTask(task: Task): Either<TaskError.DatabaseError, Task> {
        return Either.catch {
            val seq = sequenceGeneratorService.generateSequence("task_seq")
            task.id = seq.toString()
            taskRepository.save(task)
        }.mapLeft {
            TaskError.DatabaseError(it)
        }
    }

    fun completeTask(id: String): Either<TaskError, Task> {
        return Either.catch {
            val task = taskRepository.findById(id).get()
            task.completed = Completed.Y
            taskRepository.save(task)
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
            task.completed = Completed.N
            taskRepository.save(task)
        }.mapLeft {
            if (it is NoSuchElementException) {
                TaskError.NoSuchElementError(it)
            } else {
                TaskError.DatabaseError(it)
            }
        }
    }

    fun updateTask(id: String, task: Task): Either<TaskError, Task> {
        return Either.catch {
            val updateTask = taskRepository.findById(id).get()
            updateTask.message = task.message
            updateTask.priority = task.priority
            updateTask.reminderTime = task.reminderTime
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
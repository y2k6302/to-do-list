package com.example.todolist.service

import arrow.core.Either
import com.example.todolist.model.Task
import com.example.todolist.model.TaskError
import com.example.todolist.repository.TaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

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

    fun getTaskById(id: String): Either<TaskError.DatabaseError, Optional<Task>> {
        return Either.catch {
            taskRepository.findById(id)
        }.mapLeft {
            TaskError.DatabaseError(it)
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

    fun completeTask(id: String): Either<TaskError.DatabaseError, Task> {
        return Either.catch {
            val findById = taskRepository.findById(id)
            val task = findById.get()
            task.completed = "Y"
            taskRepository.save(task)
        }.mapLeft {
            TaskError.DatabaseError(it)
        }
    }

    fun redoTask(id: String): Either<TaskError.DatabaseError, Task> {
        return Either.catch {
            val findById = taskRepository.findById(id)
            val task = findById.get()
            task.completed = "N"
            taskRepository.save(task)
        }.mapLeft {
            TaskError.DatabaseError(it)
        }
    }

    fun updateTask(id: String, task: Task): Either<TaskError.DatabaseError, Task> {
        return Either.catch {
            val findById = taskRepository.findById(id).get()
            findById.message = task.message
            findById.priority = task.priority
            findById.reminderTime = task.reminderTime
            taskRepository.save(findById)
        }.mapLeft {
            TaskError.DatabaseError(it)
        }
    }

    fun deleteTask(id: String): Either<TaskError.DatabaseError, String> {
        return Either.catch {
            taskRepository.deleteById(id)
            id
        }.mapLeft {
            TaskError.DatabaseError(it)
        }
    }
}
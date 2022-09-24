package com.example.todolist.controller

import com.example.todolist.model.Task
import com.example.todolist.model.TaskError
import com.example.todolist.service.TaskService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin("http://127.0.0.1:4200")
@RestController
class TaskController {

    @Autowired
    private lateinit var taskService: TaskService

    @GetMapping("/v1/tasks")
    fun getTasks(): ResponseEntity<Any> {
        return taskService.getTasks().fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
             },
            ifRight = { tasks ->
                ResponseEntity.ok(tasks)
            }
        )
    }

    @GetMapping("/v1/tasks/{id}")
    fun getTaskById(@PathVariable id: String): ResponseEntity<Any> {
        return taskService.getTaskById(id).fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = { optTask ->
                if (optTask.isPresent) {
                    ResponseEntity.ok(optTask.get())
                } else {
                    ResponseEntity.noContent().build()
                }
            }
        )
    }

    @PutMapping("/v1/tasks/{id}/complete")
    fun completeTask(@PathVariable id: String): ResponseEntity<Any> {
        return taskService.completeTask(id).fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = { task ->
                ResponseEntity.ok(task)
            }
        )
    }

    @PutMapping("/v1/tasks/{id}/redo")
    fun redoTask(@PathVariable id: String): ResponseEntity<Any> {
        return taskService.redoTask(id).fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = { task ->
                ResponseEntity.ok(task)
            }
        )
    }
    @PostMapping("/v1/tasks")
    fun createTask(@RequestBody task: Task): ResponseEntity<Any> {
        return taskService.createTask(task).fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = { task ->
                ResponseEntity.ok(task)
            }
        )
    }

    @PutMapping("/v1/tasks/{id}")
    fun updateTask(@PathVariable id: String, @RequestBody task: Task): ResponseEntity<Any> {
        return taskService.updateTask(id, task).fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = { task ->
                ResponseEntity.ok(task)
            }
        )
    }

    @DeleteMapping("/v1/tasks/{id}")
    fun deleteTask(@PathVariable id: String): ResponseEntity<Any> {
        return taskService.deleteTask(id).fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = { id ->
                ResponseEntity.ok(id)
            }
        )
    }

}
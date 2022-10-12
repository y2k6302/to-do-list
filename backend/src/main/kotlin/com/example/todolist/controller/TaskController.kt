package com.example.todolist.controller

import arrow.core.flatMap
import com.example.todolist.model.Task
import com.example.todolist.model.TaskError
import com.example.todolist.service.TaskService
import com.example.todolist.service.util.CustomJson
import com.example.todolist.service.util.Validation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin("http://localhost:4200")
@RestController
class TaskController {

    @Autowired
    private lateinit var taskService: TaskService

    @GetMapping("/v1/tasks", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTasks(): ResponseEntity<String> {
        return taskService.getTasks().flatMap {
            CustomJson.encodeToString(it)
        }.fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.ok(it)
            }
        )
    }

    @GetMapping("/v1/tasks/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTaskById(@PathVariable id: String): ResponseEntity<String> {
        return taskService.getTaskById(id).flatMap {
            CustomJson.encodeToString(it)
        }.fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.ok(it)
            }
        )
    }

    @PutMapping("/v1/tasks/{id}/complete", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun completeTask(@PathVariable id: String): ResponseEntity<String> {
        return taskService.completeTask(id).flatMap {
            CustomJson.encodeToString(it)
        }.fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.ok(it)
            }
        )
    }

    @PutMapping("/v1/tasks/{id}/reopen", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun reopenTask(@PathVariable id: String): ResponseEntity<String> {
        return taskService.reopenTask(id).flatMap {
            CustomJson.encodeToString(it)
        }.fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.ok(it)
            }
        )
    }

    @PostMapping("/v1/tasks", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createTask(@RequestBody taskJson: String): ResponseEntity<String> {
        return CustomJson.decodeFromString<Task>(taskJson).flatMap {
            Validation.checkTaskReqBody(it).flatMap {
                taskService.createTask(it).flatMap {
                    CustomJson.encodeToString(it)
                }
            }
        }.fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.status(HttpStatus.CREATED).body(it)
            }
        )
    }

    @PutMapping("/v1/tasks/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateTask(@PathVariable id: String, @RequestBody taskJson: String): ResponseEntity<String> {
        return CustomJson.decodeFromString<Task>(taskJson).flatMap {
            Validation.checkTaskReqBody(it).flatMap { task ->
                taskService.updateTask(id, task).flatMap {
                    CustomJson.encodeToString(it)
                }
            }
        }.fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.ok(it)
            }
        )
    }

    @DeleteMapping("/v1/tasks/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteTask(@PathVariable id: String): ResponseEntity<String> {
        return taskService.deleteTask(id).flatMap {
            CustomJson.encodeToString(it)
        }.fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.ok(it)
            }
        )
    }

}
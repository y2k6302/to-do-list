package com.example.todolist.controller

import arrow.core.flatMap
import com.example.todolist.model.TaskError
import com.example.todolist.service.TaskService
import com.example.todolist.service.util.Validation
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

    private val json = Json { encodeDefaults = true }

    @GetMapping("/v1/tasks", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTasks(): ResponseEntity<String> {
        return taskService.getTasks().fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.ok(json.encodeToString(it))
            }
        )
    }

    @GetMapping("/v1/tasks/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTaskById(@PathVariable id: String): ResponseEntity<String> {
        return taskService.getTaskById(id).fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.ok(json.encodeToString(it))
            }
        )
    }

    @PutMapping("/v1/tasks/{id}/complete", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun completeTask(@PathVariable id: String): ResponseEntity<String> {
        return taskService.completeTask(id).fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.ok(json.encodeToString(it))
            }
        )
    }

    @PutMapping("/v1/tasks/{id}/reopen", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun reopenTask(@PathVariable id: String): ResponseEntity<String> {
        return taskService.reopenTask(id).fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.ok(json.encodeToString(it))
            }
        )
    }

    @PostMapping("/v1/tasks", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createTask(@RequestBody taskJson: String): ResponseEntity<String> {
        return Validation.checkDecodeJson(taskJson).flatMap {
            Validation.checkTaskReqBody(it).flatMap { task ->
                taskService.createTask(task)
            }
        }.fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.status(HttpStatus.CREATED).body(Json.encodeToString(it))
            }
        )
    }

    @PutMapping("/v1/tasks/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateTask(@PathVariable id: String, @RequestBody taskJson: String): ResponseEntity<String> {
        return Validation.checkDecodeJson(taskJson).flatMap {
            Validation.checkTaskReqBody(it).flatMap { task ->
                taskService.updateTask(id, task)
            }
        }.fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.ok(json.encodeToString(it))
            }
        )
    }

    @DeleteMapping("/v1/tasks/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteTask(@PathVariable id: String): ResponseEntity<String> {
        return taskService.deleteTask(id).fold(
            ifLeft = { err ->
                TaskError.toResponse(err)
            },
            ifRight = {
                ResponseEntity.ok(json.encodeToString(it))
            }
        )
    }

}
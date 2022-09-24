package com.example.todolist.controller

import com.example.todolist.model.Task
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
    fun getTasks(): ResponseEntity<List<Task>> {
        return ResponseEntity.ok(taskService.getTasks())
    }

    @GetMapping("/v1/tasks/{id}")
    fun getTaskById(@PathVariable id: String): ResponseEntity<Task> {
        return ResponseEntity.ok(taskService.getTaskById(id))
    }

    @PutMapping("/v1/tasks/{id}/complete")
    fun completeTask(@PathVariable id: String): ResponseEntity<Task> {
        return ResponseEntity.ok(taskService.completeTask(id));
    }

    @PutMapping("/v1/tasks/{id}/redo")
    fun redoTask(@PathVariable id: String): ResponseEntity<Task> {
        return ResponseEntity.ok(taskService.redoTask(id));
    }
    @PostMapping("/v1/tasks")
    fun createTask(@RequestBody task: Task): ResponseEntity<Task> {
        val saved = taskService.createTask(task)
        return ResponseEntity.ok(saved)
    }

    @PutMapping("/v1/tasks/{id}")
    fun updateTask(@PathVariable id: String, @RequestBody task: Task): ResponseEntity<Task> {
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    @DeleteMapping("/v1/tasks/{id}")
    fun deleteTask(@PathVariable id: String): ResponseEntity<String> {
        return ResponseEntity.ok(taskService.deleteTask(id))
    }

}
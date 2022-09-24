package com.example.todolist.controller

import com.example.todolist.model.Task
import com.example.todolist.repository.TaskRepository
import com.example.todolist.service.TaskService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin("http://127.0.0.1:4200")
@RestController
class TaskController {

    @Autowired
    private lateinit var taskService: TaskService

    @Autowired
    private lateinit var taskRepository: TaskRepository

//    @GetMapping("/v1/tasks")
//    fun getTasks(): ResponseEntity<List<Task>> {
//        val findAll = taskRepository.findAll()
//        return ResponseEntity.ok(findAll)
//    }

    @GetMapping("/v1/tasks")
    fun getTasksByCompleted(@RequestParam completed: String): ResponseEntity<List<Task>> {
        val findAll = taskService.getTasksByCompleted(completed)
        return ResponseEntity.ok(findAll)
    }

    @GetMapping("/v1/tasks/{id}")
    fun getTaskById(@PathVariable id: String): ResponseEntity<Task> {
        return ResponseEntity.ok(taskRepository.findById(id).orElse(null));
    }

    @PutMapping("/v1/tasks/{id}/completed")
    fun taskCompleted(@PathVariable id: String): ResponseEntity<Task> {
        val findById = taskRepository.findById(id)
        val task = findById.get()
        task.completed = "true"
        taskRepository.save(task)
        return ResponseEntity.ok(task);
    }

    @PostMapping("/v1/tasks")
    fun createTask(@RequestBody task: Task): ResponseEntity<Task> {
        val saved = taskService.createTask(task)
        return ResponseEntity.ok(saved)
    }

    @PutMapping("/v1/tasks/{id}")
    fun updateTask(@PathVariable id: String, @RequestBody task: Task): ResponseEntity<Task> {
        println("ee");
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @DeleteMapping("/v1/tasks/{id}")
    fun deleteTask(@PathVariable id: String): ResponseEntity<String> {
        taskRepository.deleteById(id)
        return ResponseEntity.ok(id)
    }

}
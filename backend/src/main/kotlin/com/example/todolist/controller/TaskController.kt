package com.example.todolist.controller

import com.example.todolist.service.SequenceGeneratorService
import com.example.todolist.model.Task
import com.example.todolist.repository.TaskRepository
import com.example.todolist.service.TaskService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin("http://127.0.0.1:4200")
@RestController
@RequestMapping("/tasks")
class TaskController {

    @Autowired
    private lateinit var taskService: TaskService

    @Autowired
    private lateinit var taskRepository: TaskRepository

//    @Autowired
//    private lateinit var sequenceGeneratorService: SequenceGeneratorService

    @GetMapping("/to-do", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getToDoTasks(): ResponseEntity<List<Task>> {
        val findAll = taskService.getTodoTasks()
        return ResponseEntity.ok(findAll)
    }

    @PutMapping("/{id}/be-done")
    fun getBeDoneTasks(@PathVariable id: String): ResponseEntity<Task> {
        val findById = taskRepository.findById(id)
        val task = findById.get()
        task.completed = "true"
        taskRepository.save(task)
        return ResponseEntity.ok(task);
    }

    @GetMapping
    fun getTasks(): ResponseEntity<List<Task>> {
        val findAll = taskRepository.findAll()
        return ResponseEntity.ok(findAll)
    }

    @GetMapping("/be-done")
    fun getBeDone(): ResponseEntity<List<Task>> {
        val findAll = taskRepository.findAll().filter { it -> it.completed == "true" }
        return ResponseEntity.ok(findAll);
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: String): ResponseEntity<Task> {
        return ResponseEntity.ok(taskRepository.findById(id).orElse(null));
    }

    @PostMapping
    fun createTask(@RequestBody task: Task): ResponseEntity<Task> {
        val saved = taskService.createTask(task)
        return ResponseEntity.ok(saved)
    }

    @PutMapping("/{id}")
    fun updateTask(@PathVariable id: String, @RequestBody task: Task): ResponseEntity<Task> {
        println("ee");
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: String): ResponseEntity<String> {
        taskRepository.deleteById(id)
        return ResponseEntity.ok(id)
    }

}
package com.example.todolist.controller

import com.example.todolist.service.SequenceGeneratorService
import com.example.todolist.model.Task
import com.example.todolist.repository.TaskRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin("http://127.0.0.1:4200")
@RestController
@RequestMapping("/tasks")
class TaskController(private val taskRepository: TaskRepository,
                     private val sequenceGeneratorService: SequenceGeneratorService
) {

    @GetMapping("/to-do")
    fun getToDoTasks(): ResponseEntity<List<Task>> {
        val findAll = taskRepository.findAll().filter { it.completed == "" }
        return ResponseEntity.ok(findAll);
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
        return ResponseEntity.ok(findAll);
    }

    @GetMapping("/be-done")
    fun getBeDone(): ResponseEntity<List<Task>> {
        val findAll = taskRepository.findAll().filter { it -> it.completed =="true" }
        return ResponseEntity.ok(findAll);
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id : String): ResponseEntity<Task> {
        return ResponseEntity.ok(taskRepository.findById(id).orElse(null));
    }

    @PostMapping
    fun createTask(@RequestBody task: Task): ResponseEntity<Task> {

        val seq = sequenceGeneratorService.generateSequence("task_seq")
        task.id = seq.toString()
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PutMapping("/{id}")
    fun updateTask(@PathVariable id: String, @RequestBody task: Task): ResponseEntity<Task> {
        println("ee");
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id : String): ResponseEntity<String> {
        taskRepository.deleteById(id);
        return ResponseEntity.ok(id);
    }

}
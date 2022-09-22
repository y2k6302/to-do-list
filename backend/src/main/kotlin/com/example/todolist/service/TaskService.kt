package com.example.todolist.service

import com.example.todolist.model.Task
import com.example.todolist.repository.TaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class TaskService {

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @Autowired
    private lateinit var sequenceGeneratorService: SequenceGeneratorService

    fun getTodoTasks(): List<Task> {
        return  taskRepository.findAll().filter { it.completed == "" }
    }

    fun createTask(task: Task): Task {
        val seq = sequenceGeneratorService.generateSequence("task_seq")
        task.id = seq.toString()
        val saved = taskRepository.save(task)
        return saved
    }
}
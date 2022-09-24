package com.example.todolist.service

import com.example.todolist.model.Task
import com.example.todolist.repository.TaskRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TaskService {

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @Autowired
    private lateinit var sequenceGeneratorService: SequenceGeneratorService

    fun getTasks(): List<Task> {
        return taskRepository.findAll();
    }

    fun getTaskById(id: String): Task {
        return taskRepository.findById(id).orElse(null);
    }

    fun createTask(task: Task): Task {
        val seq = sequenceGeneratorService.generateSequence("task_seq")
        task.id = seq.toString()
        return taskRepository.save(task)
    }

    fun completeTask(id: String): Task {
        val findById = taskRepository.findById(id)
        val task = findById.get()
        task.completed = "Y"
        return taskRepository.save(task)
    }

    fun redoTask(id: String): Task {
        val findById = taskRepository.findById(id)
        val task = findById.get()
        task.completed = "N"
        return taskRepository.save(task)
    }

    fun updateTask(id: String, task: Task): Task {
        val findById = taskRepository.findById(id).get()
        findById.message = task.message
        findById.priority = task.priority
        findById.reminderTime = task.reminderTime
        return taskRepository.save(findById)
    }

    fun deleteTask(id: String): String {
        taskRepository.deleteById(id)
        return id;
    }
}
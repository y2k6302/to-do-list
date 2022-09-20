package com.example.todolist.repository

import com.example.todolist.model.Task
import org.springframework.data.mongodb.repository.MongoRepository

interface TaskRepository: MongoRepository<Task, String> {
}
package com.example.todolist.controller

import arrow.core.getOrElse
import arrow.core.toOption
import com.example.todolist.CommonTest
import com.example.todolist.model.Task
import com.example.todolist.service.TaskService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

class TaskControllerTest : CommonTest() {

    @Autowired
    private lateinit var taskService: TaskService

    private val restTemplate = RestTemplate()

    @Test
    fun testGetTaskById() {
        // Arrange
        val task = Task()
        task.message = "testGetTaskById"
        val orElse = taskService.createTask(task).getOrElse { Task() }

        // Act
        val response = restTemplate.exchange<Task>(
            "http://localhost:$port/v1/tasks/${orElse.id}",
            HttpMethod.GET,
            null,
            Task::class
        )

        // Assert
        response.body.toOption().fold(
            ifEmpty = { Assertions.fail("response is null") },
            ifSome = {
                Assertions.assertEquals("testGetTaskById", it.message)
            }
        )
    }

    @Test
    fun testCompleteTask() {
        val task = Task()
        task.message = "testCompleteTask"
        task.completed = "N"
        val orElse = taskService.createTask(task).getOrElse { Task() }

        val response = restTemplate.exchange<Task>(
            "http://localhost:$port/v1/tasks/${orElse.id}/complete",
            HttpMethod.PUT,
            null,
            Task::class
        )

        response.body.toOption().fold(
            ifEmpty = { Assertions.fail("response is null") },
            ifSome = {
                Assertions.assertEquals("Y", it.completed)
            }
        )
    }

    @Test
    fun testRedo() {
        val task = Task()
        task.message = "testRedo"
        task.completed = "Y"
        val orElse = taskService.createTask(task).getOrElse { Task() }

        val response = restTemplate.exchange<Task>(
            "http://localhost:$port/v1/tasks/${orElse.id}/redo",
            HttpMethod.PUT,
            null,
            Task::class
        )

        response.body.toOption().fold(
            ifEmpty = { Assertions.fail("response is null") },
            ifSome = {
                Assertions.assertEquals("N", it.completed)
            }
        )
    }

    @Test
    fun testCreateTask() {
        val task = Task()
        task.message = "testCreateTask"

        val response = restTemplate.exchange<Task>(
            "http://localhost:$port/v1/tasks",
            HttpMethod.POST,
            HttpEntity(task),
            Task::class
        )

        response.body.toOption().fold(
            ifEmpty = { Assertions.fail("response is null") },
            ifSome = {
                Assertions.assertEquals("testCreateTask", it.message)
            }
        )
    }

    @Test
    fun testUpdateTask() {
        val beforeTask = Task()
        beforeTask.message = "testUpdateTask-before"
        val updateTask = taskService.createTask(beforeTask).getOrElse { Task() }

        updateTask.message = "testUpdateTask-update"
        updateTask.priority = "High"

        val response = restTemplate.exchange<Task>(
            "http://localhost:$port/v1/tasks/${updateTask.id}",
            HttpMethod.PUT,
            HttpEntity(updateTask),
            Task::class
        )

        response.body.toOption().fold(
            ifEmpty = { Assertions.fail("response is null") },
            ifSome = {
                Assertions.assertEquals("testUpdateTask-update", it.message)
                Assertions.assertEquals("High", it.priority)
            }
        )
    }

    @Test
    fun testDeleteTask() {
        val task = Task()
        task.message = "testDeleteTask"
        val orElse = taskService.createTask(task).getOrElse { Task() }

        val response = restTemplate.exchange<String>(
            "http://localhost:$port/v1/tasks/${orElse.id}",
            HttpMethod.DELETE,
            null,
            String::class
        )
        val optTask = taskService.getTaskById(orElse.id)

        response.body.toOption().fold(
            ifEmpty = { Assertions.fail("response is null") },
            ifSome = {
                Assertions.assertEquals(orElse.id, it)
                Assertions.assertTrue(optTask.isEmpty())
            }
        )
    }

    @Test
    fun testGetTasks() {
        val task = Task()
        task.message = "testGetTasks"
        taskService.createTask(task)

        val response = restTemplate.exchange<List<Task>>(
            "http://localhost:$port/v1/tasks",
            HttpMethod.GET,
            null,
            List::class
        )

        response.body.toOption().fold(
            ifEmpty = { Assertions.fail("response is null") },
            ifSome = { tasks ->
                Assertions.assertEquals(1, tasks.size)
                Assertions.assertEquals("testGetTasks", tasks[0].message)
            }
        )
    }
}
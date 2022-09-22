package com.example.todolist.service

import arrow.core.toOption
import com.example.todolist.model.Task
import com.example.todolist.CommonTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

class TaskServiceTest: CommonTest() {

    @Autowired
    private lateinit var taskService: TaskService

    @BeforeEach
    fun testArrange() {
        var task = Task()
        task.message = "to do"
        task.completed = ""
        task.priority = "Middle"
        task.reminderTime = ""
        taskService.createTask(task)
    }

    @Test
    fun testGetTodoTasks() {
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange<List<Task>>(
            "http://localhost:$port/tasks/to-do",
            HttpMethod.GET,
            null,
            List::class
        )

        response.body.toOption().fold(
            ifEmpty = { Assertions.fail("response is null") },
            ifSome = {tasks ->
                Assertions.assertEquals(1, tasks.size)
                Assertions.assertEquals("to do", tasks[0].message)
            }
        )
    }
}
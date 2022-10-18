package com.example.todolist.controller

import com.example.todolist.CommonTest
import com.example.todolist.model.Completed
import com.example.todolist.model.Priority
import com.example.todolist.model.Task
import com.example.todolist.model.TaskRequestBody
import com.example.todolist.service.TaskService
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import java.text.SimpleDateFormat
import java.util.*

class TaskControllerTest : CommonTest() {

    @Autowired
    private lateinit var taskService: TaskService

    private lateinit var reqTask: FrontendTaskRequestBody

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    private val defaultDate = dateFormat.format(Date())

    @BeforeEach
    fun test() {
        reqTask = FrontendTaskRequestBody("", Completed.N, Priority.MEDIUM, defaultDate)
    }

    @Test
    fun testGetTasks() {
        Given {
            reqTask = FrontendTaskRequestBody("testGetTasks", Completed.N, Priority.MEDIUM, defaultDate)
            createTask(reqTask)
            contentType(ContentType.JSON)
        } When {
            get("http://localhost:$port/v1/tasks")
        } Then {
            statusCode(HttpStatus.OK.value())
            body("size()", `is`(1))
            body("[0].message", equalTo("testGetTasks"))
        }
    }

    @Test
    fun testGetTaskById() {
        var id = ""
        Given {
            reqTask = FrontendTaskRequestBody("testGetTaskById", Completed.N, Priority.MEDIUM, defaultDate)
            id = createTask(reqTask).id
            contentType(ContentType.JSON)
        } When {
            get("http://localhost:$port/v1/tasks/${id}")
        } Then {
            statusCode(HttpStatus.OK.value())
            body("message", equalTo("testGetTaskById"))
        }
    }

    @Test
    fun testGetTaskByIdＷhenIdNotExist() {
        Given {
            contentType(ContentType.JSON)
        } When {
            get("http://localhost:$port/v1/tasks/-1")
        } Then {
            statusCode(HttpStatus.BAD_REQUEST.value())
            body("message", containsString("Value not present or request was malformed"))
        }
    }

    @Test
    fun testCompleteTask() {
        var id = ""
        Given {
            reqTask = FrontendTaskRequestBody("testCompleteTask", Completed.N, Priority.MEDIUM, defaultDate)
            id = createTask(reqTask).id
            contentType(ContentType.JSON)
        } When {
            put("http://localhost:$port/v1/tasks/${id}/complete")
        } Then {
            statusCode(HttpStatus.OK.value())
            body("completed", equalTo(Completed.Y.name))
        }
    }

    @Test
    fun testCompleteTaskＷhenIdNotExist() {
        Given {
            contentType(ContentType.JSON)
        } When {
            put("http://localhost:$port/v1/tasks/-1/complete")
        } Then {
            statusCode(HttpStatus.BAD_REQUEST.value())
            body("message", containsString("Value not present or request was malformed"))
        }
    }

    @Test
    fun testReopen() {
        var id = ""
        Given {
            reqTask = FrontendTaskRequestBody("testReopen", Completed.Y, Priority.MEDIUM, defaultDate)
            id = createTask(reqTask).id
            contentType(ContentType.JSON)
        } When {
            put("http://localhost:$port/v1/tasks/${id}/reopen")
        } Then {
            statusCode(HttpStatus.OK.value())
            body("completed", equalTo(Completed.N.name))
        }
    }

    @Test
    fun testRedoＷhenIdNotExist() {
        Given {
            contentType(ContentType.JSON)
        } When {
            put("http://localhost:$port/v1/tasks/-1/reopen")
        } Then {
            statusCode(HttpStatus.BAD_REQUEST.value())
            body("message", containsString("Value not present or request was malformed"))
        }
    }

    @Test
    fun testCreateTask() {
        Given {
            reqTask = FrontendTaskRequestBody("testCreateTask", Completed.Y, Priority.MEDIUM, defaultDate)
            body(reqTask)
            contentType(ContentType.JSON)
        } When {
            post("http://localhost:$port/v1/tasks")
        } Then {
            statusCode(HttpStatus.CREATED.value())
            body("message", equalTo("testCreateTask"))
        }
    }

    @Test
    fun testCreateTaskWhenReqBodyInvalid() {
        Given {
            reqTask = FrontendTaskRequestBody("", Completed.Y, Priority.MEDIUM, defaultDate)
            body(reqTask)
            contentType(ContentType.JSON)
        } When {
            post("http://localhost:$port/v1/tasks")
        } Then {
            statusCode(HttpStatus.BAD_REQUEST.value())
            body("message", containsString("Invalid request"))
        }
    }

    @Test
    fun testCreateTaskWhenJsonSerializationError() {
        Given {
            body("{ message }")
            contentType(ContentType.JSON)
        } When {
            post("http://localhost:$port/v1/tasks")
        } Then {
            statusCode(HttpStatus.BAD_REQUEST.value())
            body("message", containsString("Internal json serialization error"))
        }
    }

    @Test
    fun testUpdateTask() {
        var id = ""
        Given {
            reqTask = FrontendTaskRequestBody("testUpdateTask-before", Completed.Y, Priority.MEDIUM, defaultDate)
            val updateTask = createTask(reqTask)
            id = updateTask.id

            reqTask = FrontendTaskRequestBody("testUpdateTask-update", Completed.N, Priority.HIGH, defaultDate)

            body(reqTask)
            contentType(ContentType.JSON)
        } When {
            put("http://localhost:$port/v1/tasks/${id}")
        } Then {
            statusCode(HttpStatus.OK.value())
            body("message", equalTo("testUpdateTask-update"))
            body("priority", equalTo("HIGH"))
        }
    }

    @Test
    fun testUpdateTaskＷhenIdNotExist() {
        Given {
            reqTask = FrontendTaskRequestBody("testCreateTask", Completed.N, Priority.LOW, defaultDate)
            body(reqTask)
            contentType(ContentType.JSON)
        } When {
            put("http://localhost:$port/v1/tasks/-1")
        } Then {
            statusCode(HttpStatus.BAD_REQUEST.value())
            body("message", containsString("Value not present or request was malformed"))
        }
    }

    @Test
    fun testUpdateTaskＷhenReqBodyInvalid() {
        Given {
            body(reqTask)
            contentType(ContentType.JSON)
        } When {
            put("http://localhost:$port/v1/tasks/-1")
        } Then {
            statusCode(HttpStatus.BAD_REQUEST.value())
            body("message", containsString("Invalid request"))
        }
    }

    @Test
    fun testDeleteTask() {
        var id = ""
        Given {
            reqTask = FrontendTaskRequestBody("testDeleteTask", Completed.N, Priority.MEDIUM, defaultDate)
            id = createTask(reqTask).id
            contentType(ContentType.JSON)
        } When {
            delete("http://localhost:$port/v1/tasks/${id}")
        } Then {
            statusCode(HttpStatus.OK.value())
            body("message", equalTo(id))
            Assertions.assertTrue(taskService.getTaskById(id).isEmpty())
        }
    }

    @Test
    fun testDeleteTaskＷhenIdNotExist() {
        Given {
            contentType(ContentType.JSON)
        } When {
            delete("http://localhost:$port/v1/tasks/-1")
        } Then {
            statusCode(HttpStatus.BAD_REQUEST.value())
            body("message", containsString("Value not present or request was malformed"))
        }
    }

    private fun createTask(task: FrontendTaskRequestBody): Task {
        val newTask = TaskRequestBody(
            message = task.message,
            completed = task.completed,
            priority = task.priority,
            reminderTime = dateFormat.parse(task.reminderTime)
        )
        taskService.createTask(newTask).fold(
            ifLeft = { Assertions.fail("create task fail.") },
            ifRight = { return it }
        )
    }

    private data class FrontendTaskRequestBody(
        val message: String,
        val completed: Completed,
        val priority: Priority,
        val reminderTime: String
    )
}
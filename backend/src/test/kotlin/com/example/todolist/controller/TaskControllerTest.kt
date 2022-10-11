package com.example.todolist.controller

import com.example.todolist.CommonTest
import com.example.todolist.model.Completed
import com.example.todolist.model.Priority
import com.example.todolist.model.Task
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

class TaskControllerTest : CommonTest() {

    @Autowired
    private lateinit var taskService: TaskService

    private lateinit var task: Task

    @BeforeEach
    fun test() {
        task = Task()
    }

    @Test
    fun testGetTasks() {
        Given {
            task.message = "testGetTasks"
            createTask(task)
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
            task.message = "testGetTaskById"
            id = createTask(task).id
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
            task.message = "testCompleteTask"
            task.completed = Completed.N
            id = createTask(task).id
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
            task.message = "testReopen"
            task.completed = Completed.Y
            id = createTask(task).id
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
            task.message = "testCreateTask"
            task.completed = Completed.N
            task.priority = Priority.MEDIUM
            body(task)
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
            task.message = ""
            body(task)
            contentType(ContentType.JSON)
        } When {
            post("http://localhost:$port/v1/tasks")
        } Then {
            statusCode(HttpStatus.BAD_REQUEST.value())
            body("message", containsString("Invalid request"))
        }
    }

    @Test
    fun testUpdateTask() {
        var id = ""
        Given {
            task.message = "testUpdateTask-before"
            val updateTask = createTask(task)
            id = updateTask.id

            updateTask.message = "testUpdateTask-update"
            updateTask.completed = Completed.N
            updateTask.priority = Priority.HIGH

            body(updateTask)
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
            task.id = "-1"
            task.message = "testCreateTask"
            task.priority = Priority.LOW
            task.completed = Completed.N

            body(task)
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
            body(task)
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
            task.message = "testDeleteTask"
            id = createTask(task).id
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

    private fun createTask(task: Task): Task {
        taskService.createTask(task).fold(
            ifLeft = { Assertions.fail("create task fail.") },
            ifRight = { return it }
        )
    }
}
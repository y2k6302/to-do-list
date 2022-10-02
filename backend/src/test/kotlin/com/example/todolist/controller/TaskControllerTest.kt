package com.example.todolist.controller

import arrow.core.getOrElse
import com.example.todolist.CommonTest
import com.example.todolist.model.Task
import com.example.todolist.service.TaskService
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class TaskControllerTest : CommonTest() {

    @Autowired
    private lateinit var taskService: TaskService

    @Test
    fun testGetTasks() {
        Given {
            val task = Task()
            task.message = "testGetTasks"
            taskService.createTask(task)
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
            val task = Task()
            task.message = "testGetTaskById"
            id = taskService.createTask(task).getOrElse { Task() }.id
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
            val task = Task()
            task.message = "testCompleteTask"
            task.completed = "N"
            id = taskService.createTask(task).getOrElse { Task() }.id
            contentType(ContentType.JSON)
        } When {
            put("http://localhost:$port/v1/tasks/${id}/complete")
        } Then {
            statusCode(HttpStatus.OK.value())
            body("completed", equalTo("Y"))
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
    fun testRedo() {
        var id = ""
        Given {
            val task = Task()
            task.message = "testRedo"
            task.completed = "Y"
            id = taskService.createTask(task).getOrElse { Task() }.id
            contentType(ContentType.JSON)
        } When {
            put("http://localhost:$port/v1/tasks/${id}/redo")
        } Then {
            statusCode(HttpStatus.OK.value())
            body("completed", equalTo("N"))
        }
    }

    @Test
    fun testRedoＷhenIdNotExist() {
        Given {
            contentType(ContentType.JSON)
        } When {
            put("http://localhost:$port/v1/tasks/-1/redo")
        } Then {
            statusCode(HttpStatus.BAD_REQUEST.value())
            body("message", containsString("Value not present or request was malformed"))
        }
    }

    @Test
    fun testCreateTask() {
        Given {
            val task = Task()
            task.message = "testCreateTask"
            task.completed = "N"
            task.priority = "Medium"
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
            val task = Task()
            task.message = "testCreateTask"
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
            val beforeTask = Task()
            beforeTask.message = "testUpdateTask-before"
            val updateTask = taskService.createTask(beforeTask).getOrElse { Task() }
            id = updateTask.id

            updateTask.message = "testUpdateTask-update"
            updateTask.completed = "N"
            updateTask.priority = "High"

            body(updateTask)
            contentType(ContentType.JSON)
        } When {
            put("http://localhost:$port/v1/tasks/${id}")
        } Then {
            statusCode(HttpStatus.OK.value())
            body("message", equalTo("testUpdateTask-update"))
            body("priority", equalTo("High"))
        }
    }

    @Test
    fun testUpdateTaskＷhenIdNotExist() {
        Given {
            val updateTask = Task()
            updateTask.id = "-1"
            updateTask.message = "testCreateTask"
            updateTask.priority = "Low"
            updateTask.completed = "N"

            body(updateTask)
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
            body(Task())
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
            val task = Task()
            task.message = "testDeleteTask"
            id = taskService.createTask(task).getOrElse { Task() }.id
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
}
package com.example.todolist

import com.example.todolist.util.MongodbTestContainers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [TodolistApplication::class])
class CommonTest {

    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var mongoOperations: MongoOperations

    companion object {

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.host"){
                "localhost"
            }

            registry.add("spring.data.mongodb.port"){
                MongodbTestContainers.mongodbContainers.getMappedPort(27017)
            }
        }

        @BeforeAll
        @JvmStatic
        fun setUp() {
            MongodbTestContainers.start()
        }

        @AfterAll
        @JvmStatic
        fun complete() {
            MongodbTestContainers.stop()
        }
    }

    @BeforeEach
    fun clearUp() {
        this.mongoOperations.dropCollection("task")
    }

}
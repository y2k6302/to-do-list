package com.example.todolist.util

import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

object MongodbTestContainers {

    var mongodbContainers: MongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))
        .withExposedPorts(27017)

    fun start() {
        mongodbContainers.start()
    }

    fun stop() {
        mongodbContainers.stop()
    }


}

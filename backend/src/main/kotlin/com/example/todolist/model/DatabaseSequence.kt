package com.example.todolist.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class DatabaseSequence {

    @Id
    var id = ""
    var seq: Long = 0

}
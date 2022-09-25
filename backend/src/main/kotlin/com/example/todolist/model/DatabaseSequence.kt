package com.example.todolist.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class DatabaseSequence(
    @Id
    var id: String = "",
    var seq: Long = 0
)
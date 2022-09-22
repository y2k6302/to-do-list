package com.example.todolist.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document
class Task {
    @Id
    var id = ""
    var message = ""
    var completed = ""
    var priority = ""
    var reminderTime = ""

}
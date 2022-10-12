package com.example.todolist.service.util

import arrow.core.Either
import com.example.todolist.model.TaskError
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CustomJson {
    val json = Json { encodeDefaults = true }

    inline fun <reified T>  decodeFromString(json: String): Either<TaskError, T> {
        return Either.catch {
            this.json.decodeFromString<T>(json)
        }.mapLeft {
            TaskError.JsonSerializationError(it)
        }
    }

    inline fun <reified T> encodeToString(modelObject: T): Either<TaskError, String> {
        return Either.catch {
            this.json.encodeToString(modelObject)
        }.mapLeft {
            TaskError.JsonSerializationError(it)
        }
    }
}
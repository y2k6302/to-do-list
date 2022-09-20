package com.example.todolist.service

import com.example.todolist.model.DatabaseSequence
import org.springframework.data.mongodb.core.FindAndModifyOptions.options
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import java.util.*


@Service
class SequenceGeneratorService(val mongoOperations: MongoOperations) {

    fun generateSequence(seqName: String?): Long {

        val counter: DatabaseSequence? = mongoOperations.findAndModify(
            query(where("_id").`is`(seqName)),
            Update().inc("seq", 1), options().returnNew(true).upsert(true),
            DatabaseSequence::class.java
        )
        return if (!Objects.isNull(counter)) counter!!.seq else 1
    }
}
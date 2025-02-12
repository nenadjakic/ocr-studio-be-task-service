package com.github.nenadjakic.ocrstudio.task.repository

import com.github.nenadjakic.ocrstudio.task.dto.StatusCount
import com.github.nenadjakic.ocrstudio.task.entity.OcrConfig
import com.github.nenadjakic.ocrstudio.task.entity.SchedulerConfig
import com.github.nenadjakic.ocrstudio.task.entity.Task
import org.springframework.data.mongodb.repository.Aggregation
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import java.util.*

interface TaskRepository : MongoRepository<Task, UUID> {

    @Query(value = "{ 'id': ?0 }")
    @Update("{ 'ocrConfig': ?1 }")
    fun updateOcrConfigById(id: UUID, ocrConfig: OcrConfig): Int

    @Query(value = "{ 'id': ?0 }")
    @Update("{ 'schedulerConfig': ?1 }")
    fun updateSchedulerConfigById(id: UUID, schedulerConfig: SchedulerConfig): Int

    @Query(value = "{ 'id': ?0 }")
    @Update("{ 'ocrConfig.language': ?1 }")
    fun updateLanguageById(id: UUID, language: String): Int

    @Aggregation(pipeline = [
        "{ '\$group': { '_id': '\$ocrProgress.status', 'count': { '\$sum': 1 } } }",
        "{ '\$project': { 'status': '\$_id', 'count': 1, '_id': 0 } }"
    ])
    fun countTasksByStatus(): List<StatusCount>

    @Aggregation(pipeline = [
        "{ '\$project': { 'numInDocuments': { '\$size': '\$inDocuments' } } }",
        "{ '\$group': { '_id': null, 'averageCount': { '\$avg': '\$numInDocuments' } } }",
        "{ '\$project': { '_id': 0, 'averageCount': 1 } }"
    ])
    fun averageInDocuments(): Long
}
package com.github.nenadjakic.ocrstudio.task.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "ocr_collection")
class Task(
    @Id @Field(name = "_id") var id: UUID? = null
) : Auditable<UUID>() {

    lateinit var name: String
    var ocrConfig: OcrConfig = OcrConfig()
    var schedulerConfig: SchedulerConfig = SchedulerConfig()
    var ocrProgress: OcrProgress = OcrProgress()
    var inDocuments: DocumentMutableList = DocumentMutableList()
        set(value) {
            inDocuments.clear()
            inDocuments.addAll(value)
        }

    fun addInDocument(document: com.github.nenadjakic.ocrstudio.task.entity.Document): Boolean = inDocuments.add(document)
}
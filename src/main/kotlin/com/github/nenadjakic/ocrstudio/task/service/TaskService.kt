package com.github.nenadjakic.ocrstudio.task.service

import com.github.nenadjakic.ocrstudio.task.config.MessageConst
import com.github.nenadjakic.ocrstudio.task.entity.*
import com.github.nenadjakic.ocrstudio.task.exception.MissingDocumentException
import com.github.nenadjakic.ocrstudio.task.repository.TaskRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.InvalidPathException
import java.util.*

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val fileStorageService: FileStorageService
) {
    fun findAll(): List<Task> =
        taskRepository.findAll(Sort.by(Sort.Order.asc("id")))

    fun findById(id: UUID): Task? =
        taskRepository.findById(id).orElse(null)

    fun findPage(pageNumber: Int, pageSize: Int): Page<Task> =
        taskRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("id"))))

    fun insert(task: Task, files: Collection<MultipartFile>? = emptyList()): Task {
        val createdEntity = insert(task)
        if (!files.isNullOrEmpty()) {
            upload(createdEntity.id!!, files)
        }
        return createdEntity
    }

    private fun insert(task: Task): Task =
        task.apply {
            id = UUID.randomUUID()
            fileStorageService.createDirectories(id!!)
        }.let(taskRepository::insert)

    fun delete(task: Task) {
        if (task.ocrProgress.status != Status.CREATED) {
            throw IllegalStateException(MessageConst.ILLEGAL_STATUS.description)
        }

        removeAllFiles(task)
        taskRepository.delete(task)
    }

    fun deleteById(id: UUID) =
        taskRepository.findById(id)
            .orElseThrow { MissingDocumentException(MessageConst.MISSING_DOCUMENT.description) }
            .also { delete(it) }

    fun upload(id: UUID, multipartFiles: Collection<MultipartFile>): List<Document> {
        val task = taskRepository.findById(id).orElseThrow { MissingDocumentException(MessageConst.MISSING_DOCUMENT.description) }

        val createdDocuments = multipartFiles.map { multipartFile ->
            Document(multipartFile.originalFilename!!, UUID.randomUUID().toString()).apply {
                type = FileStorageService.getContentType(multipartFile)
            }.also { document ->
                fileStorageService.uploadFile(multipartFile, id, document.randomizedFileName)
                task.addInDocument(document)
            }
        }

        taskRepository.save(task)
        return createdDocuments
    }

    fun removeFile(id: UUID, originalFileName: String) {
        taskRepository.findById(id)
            .orElseThrow { MissingDocumentException(MessageConst.MISSING_DOCUMENT.description) }
            .let { task ->
                if (task.ocrProgress.status != Status.CREATED) {
                    throw IllegalStateException(MessageConst.ILLEGAL_STATUS.description)
                }

                task.inDocuments.find { it.originalFileName == originalFileName }?.let {
                    fileStorageService.deleteFile(fileStorageService.getInputFile(id, it.randomizedFileName).toPath())
                    task.inDocuments.remove(it)
                }
                taskRepository.save(task)
            }
    }

    fun removeAllFiles(task: Task) {
        if (task.ocrProgress.status != Status.CREATED) {
            throw IllegalStateException(MessageConst.ILLEGAL_STATUS.description)
        }
        task.inDocuments.forEach { fileStorageService.deleteFile(fileStorageService.getInputFile(task.id!!, it.randomizedFileName).toPath()) }
        task.inDocuments.clear()
        taskRepository.save(task)
    }

    fun removeAllFiles(id: UUID) =
        taskRepository.findById(id)
            .orElseThrow { MissingDocumentException(MessageConst.MISSING_DOCUMENT.description) }
            .also { removeAllFiles(it) }

    fun update(id: UUID, language: String): Int =
        taskRepository.updateLanguageById(id, language)

    fun update(id: UUID, ocrConfig: OcrConfig): Int =
        taskRepository.updateOcrConfigById(id, ocrConfig)

    fun update(id: UUID, schedulerConfig: SchedulerConfig): Int =
        taskRepository.updateSchedulerConfigById(id, schedulerConfig)
}
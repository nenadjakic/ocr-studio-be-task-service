package com.github.nenadjakic.ocrstudio.task.service

import com.github.nenadjakic.ocrstudio.task.entity.OcrConfig
import com.github.nenadjakic.ocrstudio.task.entity.SchedulerConfig
import com.github.nenadjakic.ocrstudio.task.entity.Task
import com.github.nenadjakic.ocrstudio.task.repository.TaskRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(
    private val taskRepository: TaskRepository
) {
    fun findAll(): List<Task> =
        taskRepository.findAll(Sort.by(Sort.Order.asc("id")))

    fun findById(id: UUID): Task? =
        taskRepository.findById(id).orElse(null)

    fun findPage(pageNumber: Int, pageSize: Int): Page<Task> =
        taskRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("id"))))

    fun update(id: UUID, language: String): Int =
        taskRepository.updateLanguageById(id, language)

    fun update(id: UUID, ocrConfig: OcrConfig): Int =
        taskRepository.updateOcrConfigById(id, ocrConfig)

    fun update(id: UUID, schedulerConfig: SchedulerConfig): Int =
        taskRepository.updateSchedulerConfigById(id, schedulerConfig)
}
package com.github.nenadjakic.ocrstudio.task.dto

import jakarta.validation.constraints.NotEmpty

class TaskAddRequest {
    @NotEmpty
    lateinit var name: String
    val ocrConfig = OcrConfigRequest()
    val schedulerConfig = SchedulerConfigRequest()
}
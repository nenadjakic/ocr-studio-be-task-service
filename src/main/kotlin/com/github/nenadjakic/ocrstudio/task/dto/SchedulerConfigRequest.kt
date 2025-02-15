package com.github.nenadjakic.ocrstudio.task.dto

import java.time.LocalDateTime

data class SchedulerConfigRequest(var startDateTime: LocalDateTime? = null)
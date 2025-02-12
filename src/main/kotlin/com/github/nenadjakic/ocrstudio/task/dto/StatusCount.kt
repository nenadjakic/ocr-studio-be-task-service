package com.github.nenadjakic.ocrstudio.task.dto

import com.github.nenadjakic.ocrstudio.task.entity.Status

data class StatusCount(
    val status: Status,
    val count: Long
)
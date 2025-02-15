package com.github.nenadjakic.ocrstudio.task.dto

import jakarta.validation.constraints.NotEmpty

data class TaskDraftRequest (
    @NotEmpty
    var name: String
)
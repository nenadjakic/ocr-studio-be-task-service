package com.github.nenadjakic.ocrstudio.task.config

import com.github.nenadjakic.ocrstudio.task.entity.Status

enum class MessageConst(val description: String) {
    MISSING_DOCUMENT("Cannot find task with specified id."),
    ILLEGAL_STATUS("Cannot remove file for task, because status is different than ${Status.CREATED}.");
}
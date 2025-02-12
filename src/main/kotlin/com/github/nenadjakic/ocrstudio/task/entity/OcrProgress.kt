package com.github.nenadjakic.ocrstudio.task.entity

class OcrProgress(
    var status: Status = Status.CREATED,
    var progress: String =  "N/A",
    var description: String? = null
)
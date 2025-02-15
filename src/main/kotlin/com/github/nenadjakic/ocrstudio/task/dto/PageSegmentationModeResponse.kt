package com.github.nenadjakic.ocrstudio.task.dto

data class PageSegmentationModeResponse(
    override val value: String,
    override val description: String
) : ValueDescriptionResponse
package com.github.nenadjakic.ocrstudio.task.dto

data class OcrEngineModeResponse(
    override val value: String,
    override val description: String
) : ValueDescriptionResponse
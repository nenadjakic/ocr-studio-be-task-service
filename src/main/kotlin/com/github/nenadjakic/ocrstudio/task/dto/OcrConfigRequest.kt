package com.github.nenadjakic.ocrstudio.task.dto

import com.github.nenadjakic.ocrstudio.task.entity.OcrConfig

class OcrConfigRequest {
    lateinit var ocrEngineMode: OcrConfig.OcrEngineMode
    lateinit var pageSegmentationMode: OcrConfig.PageSegmentationMode
    lateinit var language: String
    var tessVariables: Map<String, String>? = null
    var preProcessing: Boolean = false
    lateinit var fileFormat: OcrConfig.FileFormat
    var mergeDocuments: Boolean = false
}
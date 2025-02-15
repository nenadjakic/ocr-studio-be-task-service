package com.github.nenadjakic.ocrstudio.task.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "ocr")
class OcrProperties {

    lateinit var rootPath: String
}
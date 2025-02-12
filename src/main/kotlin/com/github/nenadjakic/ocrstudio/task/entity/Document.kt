package com.github.nenadjakic.ocrstudio.task.entity

class Document(
    val originalFileName: String,
    val randomizedFileName: String
) {
    var type: String? = null
    var outDocument: OutDocument? = null
}
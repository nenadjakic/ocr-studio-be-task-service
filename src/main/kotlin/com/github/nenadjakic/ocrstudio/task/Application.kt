package com.github.nenadjakic.ocrstudio.task

import org.modelmapper.ModelMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    @Bean
    fun modelMapper(): ModelMapper {
        val modelMapper = ModelMapper()

        return modelMapper
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
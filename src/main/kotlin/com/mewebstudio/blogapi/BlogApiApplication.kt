package com.mewebstudio.blogapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class BlogApiApplication

fun main(args: Array<String>) {
    runApplication<BlogApiApplication>(*args)
}

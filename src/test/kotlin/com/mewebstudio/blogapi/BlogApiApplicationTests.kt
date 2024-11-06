package com.mewebstudio.blogapi

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BlogApiApplicationTests {
    @Test
    @DisplayName("Context loads")
    fun contextLoads() {
    }

    @Test
    @DisplayName("Main should start application")
    fun `main should start application`() {
        main(emptyArray())
    }
}

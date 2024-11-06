package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.dto.request.user.CreateUserRequest
import com.mewebstudio.blogapi.util.Enums
import com.mewebstudio.blogapi.util.logger
import org.slf4j.Logger
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class DummyDataService(private val userService: UserService) {
    private val log: Logger by logger()

    @EventListener(ApplicationReadyEvent::class)
    fun applicationReadyEvent(event: ApplicationReadyEvent) {
        log.info("ApplicationReadyEvent took ${event.timeTaken.seconds} seconds")
        createUsers()
    }

    private fun createUsers() {
        if (userService.count() < 1) {
            log.info("Creating dummy users")

            userService.create(CreateUserRequest(
                email = "john@example.com",
                password = "Secret1.",
                firstname = "John",
                lastname = "Doe",
                gender = Enums.GenderEnum.MALE.name,
                roles = listOf(Enums.RoleEnum.ADMIN.name, Enums.RoleEnum.USER.name)
            ))

            userService.create(CreateUserRequest(
                email = "jane@example.com",
                password = "Secret1.",
                firstname = "Jane",
                lastname = "Doe",
                gender = Enums.GenderEnum.FEMALE.name,
                roles = listOf(Enums.RoleEnum.USER.name)
            ))
        }
    }
}

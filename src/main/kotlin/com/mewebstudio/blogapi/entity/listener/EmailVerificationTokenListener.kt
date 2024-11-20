package com.mewebstudio.blogapi.entity.listener

import com.mewebstudio.blogapi.entity.EmailVerificationToken
import com.mewebstudio.blogapi.service.MailSenderService
import com.mewebstudio.blogapi.util.logger
import jakarta.persistence.PostPersist
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class EmailVerificationTokenListener(private val mailSenderService: MailSenderService) {
    private val log: Logger by logger()

    @Async
    @PostPersist
    fun onPostPersist(emailVerificationToken: EmailVerificationToken) {
        log.info(
            "[Email verification token post persist listener] " +
                "${emailVerificationToken.token} - ${emailVerificationToken.user} - ${emailVerificationToken.id}"
        )
        mailSenderService.sendUserEmailVerification(emailVerificationToken)
    }
}

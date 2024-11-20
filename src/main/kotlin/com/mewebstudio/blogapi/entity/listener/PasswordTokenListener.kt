package com.mewebstudio.blogapi.entity.listener

import com.mewebstudio.blogapi.entity.PasswordResetToken
import com.mewebstudio.blogapi.service.MailSenderService
import com.mewebstudio.blogapi.util.logger
import jakarta.persistence.PostPersist
import org.slf4j.Logger
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class PasswordTokenListener(private val mailSenderService: MailSenderService) {
    private val log: Logger by logger()

    @Async
    @PostPersist
    fun onPostPersist(passwordResetToken: PasswordResetToken) {
        log.info(
            "[Password reset token post persist listener] " +
                "${passwordResetToken.token} - ${passwordResetToken.user} - ${passwordResetToken.id}"
        )
        mailSenderService.sendUserPasswordReset(passwordResetToken)
    }
}

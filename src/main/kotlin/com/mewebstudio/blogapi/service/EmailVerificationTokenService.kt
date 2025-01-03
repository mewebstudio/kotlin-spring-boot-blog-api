package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.entity.EmailVerificationToken
import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.exception.NotFoundException
import com.mewebstudio.blogapi.exception.TokenExpiredException
import com.mewebstudio.blogapi.repository.EmailVerificationTokenRepository
import com.mewebstudio.blogapi.util.Constants.EMAIL_VERIFICATION_TOKEN_LENGTH
import com.mewebstudio.blogapi.util.RandomStringGenerator
import com.mewebstudio.blogapi.util.logger
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class EmailVerificationTokenService(
    private val emailVerificationTokenRepository: EmailVerificationTokenRepository,
    private val messageSourceService: MessageSourceService
) {
    private val log: Logger by logger()

    @Value("\${spring.application.registration.token.expires-in}")
    private lateinit var tokenExpiresIn: String

    /**
     * Create email verification token.
     *
     * @param user User
     * @return EmailVerificationToken
     */
    fun create(user: User): EmailVerificationToken = EmailVerificationToken(
        user = user,
        token = RandomStringGenerator(EMAIL_VERIFICATION_TOKEN_LENGTH).next(),
        expirationDate = Date(System.currentTimeMillis() + tokenExpiresIn.toLong())
    )

    /**
     * Find email verification token by token.
     *
     * @param token String
     * @return User
     */
    fun getUserByToken(token: String): User {
        emailVerificationTokenRepository.findByToken(token)?.let {
            return if (isEmailVerificationTokenExpired(it)) {
                log.info("[EmailVerificationService] Email verification token expired.")
                throw TokenExpiredException(messageSourceService.get("token_expired"))
            } else {
                it.user!!
            }
        } ?: run {
            log.error("[EmailVerificationService] Token not found: $token")
            throw NotFoundException(
                messageSourceService.get(
                    "not_found_with_param",
                    arrayOf(messageSourceService.get("token"))
                )
            )
        }
    }

    /**
     * Is e-mail verification token expired?
     *
     * @param token EmailVerificationToken
     * @return boolean
     */
    private fun isEmailVerificationTokenExpired(token: EmailVerificationToken): Boolean =
        token.expirationDate?.before(Date()) ?: false
}

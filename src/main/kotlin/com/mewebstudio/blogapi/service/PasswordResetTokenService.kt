package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.entity.PasswordResetToken
import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.exception.NotFoundException
import com.mewebstudio.blogapi.exception.TokenExpiredException
import com.mewebstudio.blogapi.repository.PasswordResetTokenRepository
import com.mewebstudio.blogapi.util.Constants.PASSWORD_RESET_TOKEN_LENGTH
import com.mewebstudio.blogapi.util.RandomStringGenerator
import com.mewebstudio.blogapi.util.logger
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class PasswordResetTokenService(
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val messageSourceService: MessageSourceService
) {
    private val log: Logger by logger()

    @Value("\${spring.application.password.token.expires-in}")
    private lateinit var tokenExpiresIn: String

    /**
     * Create password reset token.
     *
     * @param user User
     * @return PasswordResetToken
     */
    fun create(user: User): PasswordResetToken = PasswordResetToken(
        user = user,
        token = RandomStringGenerator(PASSWORD_RESET_TOKEN_LENGTH).next(),
        expirationDate = Date(System.currentTimeMillis() + tokenExpiresIn.toLong())
    )

    /**
     * Find user by password reset token.
     *
     * @param token String
     * @return User
     */
    fun getUserByToken(token: String): User {
        passwordResetTokenRepository.findByToken(token)?.let {
            return if (isPasswordTokenExpired(it)) {
                log.info("[PasswordResetTokenService] Password token expired.")
                throw TokenExpiredException(messageSourceService.get("token_expired"))
            } else {
                it.user!!
            }
        } ?: run {
            log.error("[PasswordResetTokenService] Token not found: $token")
            throw NotFoundException(
                messageSourceService.get(
                    "not_found_with_param",
                    arrayOf(messageSourceService.get("token"))
                )
            )
        }
    }

    /**
     * Is password token expired?
     *
     * @param token PasswordResetToken
     * @return boolean
     */
    private fun isPasswordTokenExpired(token: PasswordResetToken): Boolean =
        token.expirationDate?.before(Date()) ?: false
}

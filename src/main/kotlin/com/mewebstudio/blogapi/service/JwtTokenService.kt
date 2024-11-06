package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.entity.JwtToken
import com.mewebstudio.blogapi.exception.NotFoundException
import com.mewebstudio.blogapi.repository.redis.JwtTokenRepository
import com.mewebstudio.blogapi.util.logger
import org.slf4j.Logger
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class JwtTokenService(
    private val jwtTokenRepository: JwtTokenRepository,
    private val messageSourceService: MessageSourceService
) {
    private val log: Logger by logger()

    /**
     * Find a JWT token by user ID and refresh token.
     *
     * @param userId UUID
     * @param refreshToken String
     * @return JwtToken
     * @throws NotFoundException
     */
    fun findByUserIdAndRefreshToken(userId: UUID, refreshToken: String): JwtToken {
        return jwtTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken) ?: throw NotFoundException(
            messageSourceService.get("not_found_with_param", arrayOf(messageSourceService.get("token")))
        )
    }

    /**
     * Find a JWT token by token or refresh token.
     *
     * @param token String
     * @return JwtToken
     * @throws NotFoundException
     */
    fun findByTokenOrRefreshToken(token: String): JwtToken {
        return jwtTokenRepository.findByTokenOrRefreshToken(token, token) ?: throw NotFoundException(
            messageSourceService.get("not_found_with_param", arrayOf(messageSourceService.get("token")))
        )
    }

    /**
     * Save a JWT token.
     *
     * @param jwtToken JwtToken
     */
    fun save(jwtToken: JwtToken) {
        jwtTokenRepository.save(jwtToken)
    }

    /**
     * Delete a JWT token.
     *
     * @param jwtToken JwtToken
     */
    fun delete(jwtToken: JwtToken) {
        jwtTokenRepository.delete(jwtToken)
        log.info("Deleted token: {}", jwtToken)
    }
}

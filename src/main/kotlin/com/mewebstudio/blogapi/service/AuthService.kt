package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.dto.request.auth.LoginRequest
import com.mewebstudio.blogapi.dto.response.auth.TokenExpiresInResponse
import com.mewebstudio.blogapi.dto.response.auth.TokenResponse
import com.mewebstudio.blogapi.entity.JwtToken
import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.exception.NotFoundException
import com.mewebstudio.blogapi.exception.TokenExpiredException
import com.mewebstudio.blogapi.security.JwtTokenProvider
import com.mewebstudio.blogapi.security.JwtUserDetails
import com.mewebstudio.blogapi.util.Constants.TOKEN_HEADER
import com.mewebstudio.blogapi.util.logger
import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    private val userService: UserService,
    private val messageSourceService: MessageSourceService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtTokenService: JwtTokenService,
    private val authenticationManager: AuthenticationManager,
    private val httpServletRequest: HttpServletRequest
) {
    private val log: Logger by logger()

    /**
     * Login user.
     * @param request LoginRequest
     */
    fun login(request: LoginRequest): TokenResponse {
        val badCredentialsMessage = messageSourceService.get("bad_credentials")
        try {
            val user = userService.findByEmail(request.email!!)
            if (user.blockedAt != null) {
                log.error("User is blocked with email: ${request.email}")
                throw AuthenticationCredentialsNotFoundException(badCredentialsMessage)
            }
        } catch (e: NotFoundException) {
            log.error("User not found with email: ${request.email} - ${e.message}")
            throw AuthenticationCredentialsNotFoundException(badCredentialsMessage)
        }
        val authenticationToken =
            UsernamePasswordAuthenticationToken(request.email, request.password)
        val authentication = authenticationManager.authenticate(authenticationToken)
        val jwtUserDetails = jwtTokenProvider.getPrincipal(authentication)

        return generateTokens(UUID.fromString(jwtUserDetails.id), request.rememberMe)
    }

    /**
     * Refresh token from the bearer string.
     *
     * @param bearer String
     * @return TokenResponse
     * @throws TokenExpiredException
     * @throws BadCredentialsException
     */
    fun refreshFromBearerString(bearer: String): TokenResponse? {
        return refresh(jwtTokenProvider.extractJwtFromBearerString(bearer)!!)
    }

    /**
     * Getting username from the security context.
     *
     * @param roles -- roles that user must have
     * @return boolean -- username or null
     * @throws AccessDeniedException -- if user does not have required roles
     */
    @Throws(AccessDeniedException::class)
    fun isAuthorized(vararg roles: String?): Boolean {
        val jwtUserDetails = getPrincipal() ?: throw AccessDeniedException(messageSourceService.get("access_denied"))

        // Check if any of the user's authorities matches the provided roles
        return roles.any { role ->
            jwtUserDetails.authorities.any { authority ->
                authority.authority.equals(role, ignoreCase = true)
            }
        }
    }

    /**
     * Get principal from security context.
     *
     * @return JwtUserDetails
     */
    fun getPrincipal(): JwtUserDetails? {
        val authentication = SecurityContextHolder.getContext().authentication
        try {
            return authentication.principal as JwtUserDetails
        } catch (e: ClassCastException) {
            log.error("Exception while casting principal to JwtUserDetails, ${ExceptionUtils.getStackTrace(e)}")
            return null
        }
    }

    /**
     * Logout user.
     *
     * @param user  User
     * @param token String
     */
    fun logout(user: User, token: String) {
        val jwtToken = jwtTokenService.findByTokenOrRefreshToken(token)
        if (user.id?.equals(jwtToken.userId)?.not() == true) {
            log.error("User id: {} is not equal to token user id: {}", user.id, jwtToken.userId)
            throw AuthenticationCredentialsNotFoundException(messageSourceService.get("bad_credentials"))
        }

        jwtTokenService.delete(jwtToken)
    }

    /**
     * Logout user.
     */
    fun logout() {
        val bearer = httpServletRequest.getHeader(TOKEN_HEADER)
        if (bearer == null) {
            log.error("Bearer token is null")
            throw AuthenticationCredentialsNotFoundException(messageSourceService.get("bad_credentials"))
        }

        logout(userService.getUser(), jwtTokenProvider.extractJwtFromBearerString(bearer)!!)
    }

    /**
     * Generate both access and refresh tokens.
     *
     * @param id         user identifier to set the subject for token and value for the expiring map
     * @param rememberMe Boolean option to set the expiration time for refresh token
     * @return an object of TokenResponse
     */
    private fun generateTokens(id: UUID, rememberMe: Boolean?): TokenResponse {
        val token: String = jwtTokenProvider.generateJwt(id.toString())
        val refreshToken: String = jwtTokenProvider.generateRefresh(id.toString())
        val isRememberMe = rememberMe?.equals(true) == true

        if (isRememberMe) jwtTokenProvider.setRememberMe()

        jwtTokenService.save(
            JwtToken(
                userId = id,
                token = token,
                refreshToken = refreshToken,
                rememberMe = isRememberMe,
                ipAddress = httpServletRequest.remoteAddr,
                userAgent = httpServletRequest.getHeader("User-Agent"),
                tokenTimeToLive = jwtTokenProvider.getRefreshTokenExpiresIn()
            )
        )

        log.info("Token generated for user: {}", id)

        return TokenResponse(
            token = token,
            refreshToken = refreshToken,
            expiresIn = TokenExpiresInResponse(
                token = jwtTokenProvider.getTokenExpiresIn(),
                refreshToken = jwtTokenProvider.getRefreshTokenExpiresIn()
            )
        )
    }

    /**
     * Refresh token.
     *
     * @param refreshToken String
     * @return TokenResponse
     * @throws TokenExpiredException
     * @throws BadCredentialsException
     */
    private fun refresh(refreshToken: String): TokenResponse {
        log.info("Refresh request received: {}", refreshToken)

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.error("Refresh token is expired.")
            throw TokenExpiredException()
        }

        val user = jwtTokenProvider.getUserFromToken(refreshToken)
            ?: throw BadCredentialsException(messageSourceService.get("user_not_found"))
        try {
            val oldToken = jwtTokenService.findByUserIdAndRefreshToken(user.id!!, refreshToken)
            if (oldToken.rememberMe) {
                jwtTokenProvider.setRememberMe()
            }

            jwtTokenService.delete(oldToken)
            return generateTokens(user.id!!, oldToken.rememberMe)
        } catch (e: NotFoundException) {
            log.error("Token not found with refresh token: ${e.message}")
            throw BadCredentialsException(messageSourceService.get("bad_credentials"))
        }
    }
}

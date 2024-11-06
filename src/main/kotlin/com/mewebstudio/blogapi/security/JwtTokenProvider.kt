package com.mewebstudio.blogapi.security

import com.mewebstudio.blogapi.entity.JwtToken
import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.exception.NotFoundException
import com.mewebstudio.blogapi.service.JwtTokenService
import com.mewebstudio.blogapi.service.UserService
import com.mewebstudio.blogapi.util.Constants.TOKEN_HEADER
import com.mewebstudio.blogapi.util.Constants.TOKEN_TYPE
import com.mewebstudio.blogapi.util.logger
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtTokenProvider(
    private val userService: UserService,
    private val jwtTokenService: JwtTokenService,
    private val httpServletRequest: HttpServletRequest
) {
    private val log: Logger by logger()

    @Value("\${spring.application.secret}")
    private lateinit var appSecret: String

    @Value("\${spring.application.jwt.token.expires-in}")
    private var tokenExpiresIn: Long = 0

    @Value("\${spring.application.jwt.refresh-token.expires-in}")
    private var refreshTokenExpiresIn: Long = 0

    @Value("\${spring.application.jwt.remember-me.expires-in}")
    private var rememberMeTokenExpiresIn: Long = 0

    /**
     * Generate token by user ID.
     *
     * @param id      String
     * @param expires Long
     * @return String
     */
    fun generateTokenByUserId(id: String, expires: Long): String {
        val claims = Jwts.claims().setSubject(id)
        claims["custom_field"] = "custom_value"
        val token = Jwts.builder()
            .setClaims(claims)
            .setSubject(id)
            .setIssuedAt(Date())
            .setExpiration(getExpireDate(expires))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()
        log.trace("Token is added to the local cache for userID: {}, ttl: {}", id, expires)

        return token
    }

    /**
     * Generate JWT token by user ID.
     *
     * @param id String
     * @return String
     */
    fun generateJwt(id: String): String {
        return generateTokenByUserId(id, tokenExpiresIn)
    }

    /**
     * Generate refresh token by user ID.
     *
     * @param id String
     * @return String
     */
    fun generateRefresh(id: String): String {
        return generateTokenByUserId(id, refreshTokenExpiresIn)
    }

    /**
     * Get JwtUserDetails from authentication.
     *
     * @param authentication Authentication
     * @return JwtUserDetails
     */
    fun getPrincipal(authentication: Authentication): JwtUserDetails {
        return userService.getPrincipal(authentication)
    }

    /**
     * Get user ID from token.
     *
     * @param token String
     * @return String
     */
    fun getUserIdFromToken(token: String): String {
        val claims = parseToken(token).body

        return claims.subject
    }

    /**
     * Get user from token.
     *
     * @param token String
     * @return User
     */
    fun getUserFromToken(token: String): User? {
        return try {
            userService.findById(getUserIdFromToken(token))
        } catch (e: NotFoundException) {
            log.error("[JWT] User not found with token ${e.message}")
            null
        }
    }

    /**
     * Boolean result of whether token is valid or not.
     *
     * @param token String token
     * @return boolean
     */
    fun validateToken(token: String): Boolean {
        parseToken(token)

        try {
            val jwtToken: JwtToken = jwtTokenService.findByTokenOrRefreshToken(token)
            if (httpServletRequest.remoteAddr != jwtToken.ipAddress) {
                log.error("[JWT] IP address is not matched")
                return false
            }
            if (httpServletRequest.getHeader("User-agent") != jwtToken.userAgent) {
                log.error("[JWT] User-agent is not matched")
                return false
            }
        } catch (e: NotFoundException) {
            log.error("[JWT] Token could not found in Redis: ${e.message}")
            return false
        }

        return !isTokenExpired(token)
    }

    /**
     * Validate token.
     *
     * @param token              String
     * @param httpServletRequest HttpServletRequest
     * @return boolean
     */
    fun validateToken(token: String, httpServletRequest: HttpServletRequest): Boolean {
        var isValidToken = false

        try {
            isValidToken = validateToken(token)
            if (!isValidToken) {
                log.error("[JWT] Token could not found in local cache")
                httpServletRequest.setAttribute("notfound", "Token is not found in cache")
            }
        } catch (e: UnsupportedJwtException) {
            log.error("[JWT] Unsupported JWT token: ${e.message}")
            httpServletRequest.setAttribute("unsupported", "Unsupported JWT token!")
        } catch (e: MalformedJwtException) {
            log.error("[JWT] Invalid JWT token: ${e.message}")
            httpServletRequest.setAttribute("invalid", "Invalid JWT token!")
        } catch (e: ExpiredJwtException) {
            log.error("[JWT] Expired JWT token: ${e.message}")
            httpServletRequest.setAttribute("expired", "Expired JWT token!")
        } catch (e: IllegalArgumentException) {
            log.error("[JWT] Jwt claims string is empty: ${e.message}")
            httpServletRequest.setAttribute("illegal", "JWT claims string is empty.")
        }

        return isValidToken
    }

    /**
     * Set jwt refresh token for remember me option.
     */
    fun setRememberMe() {
        this.refreshTokenExpiresIn = rememberMeTokenExpiresIn
    }

    /**
     * Extract jwt from bearer string.
     *
     * @param bearer String
     * @return String value of bearer token or null
     */
    fun extractJwtFromBearerString(bearer: String): String? {
        if (bearer.isNotEmpty() && bearer.startsWith("$TOKEN_TYPE ")) {
            return bearer.substring(TOKEN_TYPE.length + 1)
        }

        return null
    }

    /**
     * Extract jwt from request.
     *
     * @param request HttpServletRequest object to get Authorization header
     * @return String value of bearer token or null
     */
    fun extractJwtFromRequest(request: HttpServletRequest): String? {
        return (request.getHeader(TOKEN_HEADER) ?: null)?.let { extractJwtFromBearerString(it) }
    }

    /**
     * Get token expires in.
     *
     * @return Long
     */
    fun getTokenExpiresIn(): Long {
        return tokenExpiresIn
    }

    /**
     * Get refresh token expires in.
     *
     * @return Long
     */
    fun getRefreshTokenExpiresIn(): Long {
        return refreshTokenExpiresIn
    }

    /**
     * Parsing token.
     *
     * @param token String jwt token to parse
     * @return Jws object
     */
    private fun parseToken(token: String): Jws<Claims> {
        try {
            return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
        } catch (e: SignatureException) {
            log.error("[JWT] Token is invalid: ${e.message}")
            throw BadCredentialsException("Token is invalid")
        }
    }

    /**
     * Check token is expired or not.
     *
     * @param token String jwt token to get expiration date
     * @return True or False
     */
    private fun isTokenExpired(token: String): Boolean {
        return parseToken(token).body.expiration.before(Date())
    }

    /**
     * Get expire date.
     *
     * @return Date object
     */
    private fun getExpireDate(expires: Long): Date {
        return Date(Date().time + expires)
    }

    /**
     * Get signing key.
     *
     * @return Key
     */
    private fun getSigningKey(): Key {
        return Keys.hmacShaKeyFor(appSecret.toByteArray())
    }
}

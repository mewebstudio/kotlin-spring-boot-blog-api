package com.mewebstudio.blogapi.security

import com.mewebstudio.blogapi.exception.NotFoundException
import com.mewebstudio.blogapi.service.UserService
import com.mewebstudio.blogapi.util.logger
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class CustomAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager
) : OncePerRequestFilter() {
    private val log: Logger by logger()

    /**
     * Do filter internal overrides.
     * @param req HttpServletRequest
     * @param res HttpServletResponse
     * @param filterChain FilterChain
     */
    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, filterChain: FilterChain) {
        var userId: String? = null
        val token: String? = jwtTokenProvider.extractJwtFromRequest(req)
        if (!token.isNullOrEmpty() && jwtTokenProvider.validateToken(token, req)) {
            userId = jwtTokenProvider.getUserIdFromToken(token)
        }

        if (userId != null) {
            try {
                val user = userService.loadUserById(userId)
                val auth = UsernamePasswordAuthenticationToken(user, null, user.authorities)
                authenticationManager.authenticate(auth)
            } catch (e: NotFoundException) {
                log.error(e.message)
            }
        }

        filterChain.doFilter(req, res)
        log.info("${req.method} # ${req.requestURI} | ${req.remoteAddr} | ${req.getHeader("User-Agent")}")
    }
}

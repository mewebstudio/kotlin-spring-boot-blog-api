package com.mewebstudio.blogapi.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.mewebstudio.blogapi.exception.AppExceptionHandler
import com.mewebstudio.blogapi.service.MessageSourceService
import com.mewebstudio.blogapi.util.logger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationEntryPoint(
    private val messageSourceService: MessageSourceService,
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {
    private val log: Logger by logger()

    override fun commence(request: HttpServletRequest, response: HttpServletResponse, e: AuthenticationException) {
        val message = determineErrorMessage(request, e)
        log.error("Could not set user authentication in security context. Error: {}", message)

        val responseEntity = AppExceptionHandler(messageSourceService)
            .handleBadCredentialsException(BadCredentialsException(message))

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(objectMapper.writeValueAsString(responseEntity.body))
    }

    /**
     * Determine error message.
     *
     * @param request HttpServletRequest
     * @param e       AuthenticationException
     * @return String
     */
    private fun determineErrorMessage(request: HttpServletRequest, e: AuthenticationException): String? = run {
        val attributes = arrayOf("expired", "unsupported", "invalid", "illegal", "notfound")
        for (attribute in attributes) {
            val message = request.getAttribute(attribute) as? String
            if (!message.isNullOrEmpty()) {
                return message
            }
        }

        e.message
    }
}

package com.mewebstudio.blogapi.exception

import com.mewebstudio.blogapi.dto.response.DetailedErrorResponse
import com.mewebstudio.blogapi.dto.response.ErrorResponse
import com.mewebstudio.blogapi.service.MessageSourceService
import com.mewebstudio.blogapi.util.logger
import io.jsonwebtoken.MalformedJwtException
import jakarta.validation.ConstraintViolationException
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.dao.InvalidDataAccessApiUsageException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MultipartException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.util.function.Consumer

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
class AppExceptionHandler(private val messageSourceService: MessageSourceService) {
    private val log: Logger by logger()

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupported(
        e: HttpRequestMethodNotSupportedException
    ): ResponseEntity<ErrorResponse> {
        log.error(e.toString(), e.message)
        return build(HttpStatus.METHOD_NOT_ALLOWED, messageSourceService.get("method_not_supported"))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        log.error(e.toString(), e.message)
        return build(HttpStatus.BAD_REQUEST, "${messageSourceService.get("malformed_json_request")}: ${e.message}")
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException): ResponseEntity<ErrorResponse> {
        log.error(e.toString(), e.message)
        val errors: MutableMap<String, String?> = HashMap()

        e.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
            val fieldName = (error as FieldError).field
            val message = error.getDefaultMessage()
            errors[fieldName] = message
        })

        return build(HttpStatus.UNPROCESSABLE_ENTITY, messageSourceService.get("validation_error"), errors)
    }

    @ExceptionHandler(
        BadRequestException::class,
        MultipartException::class,
        MissingServletRequestPartException::class,
        HttpMediaTypeNotSupportedException::class,
        MethodArgumentTypeMismatchException::class,
        IllegalArgumentException::class,
        InvalidDataAccessApiUsageException::class,
        ConstraintViolationException::class,
        MissingRequestHeaderException::class,
        MalformedJwtException::class
    )
    fun handleBadRequestException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error(e.toString(), e.message)
        return build(HttpStatus.BAD_REQUEST, (if (e.cause != null) e.cause!!.message else e.message)!!)
    }

    @ExceptionHandler(TokenExpiredException::class)
    fun handleTokenExpiredRequestException(e: TokenExpiredException): ResponseEntity<ErrorResponse> {
        log.error(e.toString(), e.message)
        return build(HttpStatus.UNAUTHORIZED, (if (e.cause != null) e.cause!!.message else e.message)!!)
    }

    @ExceptionHandler(NotFoundException::class, NoResourceFoundException::class)
    fun handleNotFoundException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error(e.toString(), e.message)
        return build(HttpStatus.NOT_FOUND, (if (e.cause != null) e.cause!!.message else e.message)!!)
    }

    @ExceptionHandler(
        InternalAuthenticationServiceException::class,
        BadCredentialsException::class,
        AuthenticationCredentialsNotFoundException::class
    )
    fun handleBadCredentialsException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error(e.toString(), e.message)
        return build(HttpStatus.UNAUTHORIZED, (if (e.cause != null) e.cause!!.message else e.message)!!)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error(e.toString(), e.message)
        return build(HttpStatus.FORBIDDEN, (if (e.cause != null) e.cause!!.message else e.message)!!)
    }

    @ExceptionHandler(ExpectationException::class)
    fun handleExpectationException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error(e.toString(), e.message)
        return build(HttpStatus.EXPECTATION_FAILED, (if (e.cause != null) e.cause!!.message else e.message)!!)
    }

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("Exception: {}", ExceptionUtils.getStackTrace(e))
        return build(HttpStatus.INTERNAL_SERVER_ERROR, messageSourceService.get("server_error"))
    }

    /**
     * Build error response.
     *
     * @param httpStatus HttpStatus enum to response status field
     * @param message    String for response message field
     * @param errors     MutableMap for response errors field
     * @return ResponseEntity
     */
    private fun build(
        httpStatus: HttpStatus,
        message: String,
        errors: MutableMap<String, String?>
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(httpStatus).body(
            if (errors.isNotEmpty()) {
                DetailedErrorResponse(message = message, items = errors)
            } else {
                ErrorResponse(message = message)
            }
        )
    }

    /**
     * Build error response.
     *
     * @param httpStatus HttpStatus enum to response status field
     * @param message    String for response message field
     * @return ResponseEntity
     */
    private fun build(httpStatus: HttpStatus, message: String): ResponseEntity<ErrorResponse> {
        return build(httpStatus, message, HashMap())
    }
}

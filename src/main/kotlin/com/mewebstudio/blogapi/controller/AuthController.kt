package com.mewebstudio.blogapi.controller

import com.mewebstudio.blogapi.dto.request.auth.LoginRequest
import com.mewebstudio.blogapi.dto.response.DetailedErrorResponse
import com.mewebstudio.blogapi.dto.response.ErrorResponse
import com.mewebstudio.blogapi.dto.response.SuccessResponse
import com.mewebstudio.blogapi.dto.response.auth.TokenResponse
import com.mewebstudio.blogapi.service.AuthService
import com.mewebstudio.blogapi.service.MessageSourceService
import com.mewebstudio.blogapi.util.Constants.SECURITY_SCHEME_NAME
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "001. Auth", description = "Auth API")
class AuthController(
    private val authService: AuthService,
    private val messageSourceService: MessageSourceService
) : AbstractBaseController() {
    @PostMapping("/login")
    @Operation(
        summary = "Login endpoint",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = TokenResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Bad credentials",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Validation failed",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = DetailedErrorResponse::class)
                )]
            )
        ]
    )
    fun login(
        @Parameter(description = "Request body to login", required = true)
        @RequestBody @Validated request: LoginRequest
    ): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.login(request))
    }

    @GetMapping("/refresh")
    @Operation(
        summary = "Refresh endpoint",
        responses = [ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = TokenResponse::class)
            )]
        ), ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ErrorResponse::class)
            )]
        ), ApiResponse(
            responseCode = "401",
            description = "Bad credentials",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ErrorResponse::class)
            )]
        )]
    )
    fun refresh(
        @Parameter(description = "Refresh token", required = true)
        @RequestHeader("Authorization") @Validated refreshToken: String
    ): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.refreshFromBearerString(refreshToken))
    }

    @GetMapping("/logout")
    @Operation(
        summary = "Logout endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = SuccessResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Bad credentials",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            )
        ]
    )
    fun logout(): ResponseEntity<SuccessResponse> {
        authService.logout()

        return ResponseEntity.ok(SuccessResponse(messageSourceService.get("logout_successfully")))
    }
}

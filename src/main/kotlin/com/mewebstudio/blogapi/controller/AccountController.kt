package com.mewebstudio.blogapi.controller

import com.mewebstudio.blogapi.dto.request.user.UpdateProfileRequest
import com.mewebstudio.blogapi.dto.response.ErrorResponse
import com.mewebstudio.blogapi.dto.response.user.UserResponse
import com.mewebstudio.blogapi.service.UserService
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
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/account")
@Tag(name = "002. Account", description = "Account API")
class AccountController(private val userService: UserService): AbstractBaseController() {
    @GetMapping("/me")
    @Operation(
        summary = "Me endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UserResponse::class)
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
    fun me(): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(UserResponse.convert(userService.getUser()))
    }

    @PatchMapping("/update")
    @Operation(
        summary = "Update profile endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UserResponse::class)
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
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            )
        ]
    )
    fun update(
        @Parameter(description = "Request body to update profile", required = true)
        @RequestBody @Validated request: UpdateProfileRequest
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(UserResponse.convert(userService.updateProfile(request)))
    }
}

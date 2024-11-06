package com.mewebstudio.blogapi.controller

import com.mewebstudio.blogapi.dto.request.user.CreateUserRequest
import com.mewebstudio.blogapi.dto.request.user.UpdateUserRequest
import com.mewebstudio.blogapi.dto.request.user.UserFilterRequest
import com.mewebstudio.blogapi.dto.response.DetailedErrorResponse
import com.mewebstudio.blogapi.dto.response.ErrorResponse
import com.mewebstudio.blogapi.dto.response.user.UserPaginationResponse
import com.mewebstudio.blogapi.dto.response.user.UserResponse
import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.security.CheckRole
import com.mewebstudio.blogapi.service.UserService
import com.mewebstudio.blogapi.util.Constants.SECURITY_SCHEME_NAME
import com.mewebstudio.blogapi.util.Enums
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
@CheckRole([Enums.RoleEnum.ADMIN])
@Tag(name = "003. Users", description = "Users API")
class UserController(private val userService: UserService) : AbstractBaseController() {
    @GetMapping
    @Operation(
        summary = "List users endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UserPaginationResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
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
    fun list(
        @ParameterObject @ModelAttribute @Validated request: UserFilterRequest
    ): ResponseEntity<UserPaginationResponse> {
        val users: Page<User> = userService.findAll(request)
        return ResponseEntity.ok(
            UserPaginationResponse(
                page = request.page,
                pages = users.totalPages,
                size = users.size,
                total = users.totalElements,
                items = users.map { UserResponse.convert(it) }.toList()
            )
        )
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by id endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
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
                responseCode = "404",
                description = "Not Found",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            )
        ]
    )
    fun show(
        @Parameter(name = "id", description = "User ID", required = true)
        @PathVariable("id") id: String
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(UserResponse.convert(userService.findById(id)))
    }

    @PostMapping
    @Operation(
        summary = "Create user endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Success operation",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = UserResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Full authentication is required to access this resource",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not Found",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Validation Failed",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = DetailedErrorResponse::class)
                )]
            )
        ]
    )
    @Throws(BindException::class)
    fun create(
        @Parameter(description = "Request body to user create", required = true)
        @RequestBody @Validated request: CreateUserRequest
    ): ResponseEntity<UserResponse> {
        return ResponseEntity<UserResponse>(UserResponse.convert(userService.create(request)), HttpStatus.CREATED)
    }

    @PatchMapping("/{id}")
    @Operation(
        summary = "Update user by id endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Success operation",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = UserResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Bad request",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
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
                responseCode = "404",
                description = "Not Found",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Validation Failed",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = DetailedErrorResponse::class)
                )]
            )
        ]
    )
    @Throws(BindException::class)
    fun update(
        @Parameter(name = "id", description = "User ID", required = true)
        @PathVariable("id") id: String,
        @Parameter(description = "Request body to user update", required = true)
        @RequestBody @Validated request: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(UserResponse.convert(userService.update(id, request)))
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete user by id endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "Success operation",
                content = [Content(schema = Schema(hidden = true))]
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
                responseCode = "404",
                description = "Not Found",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class)
                )]
            )
        ]
    )
    fun delete(
        @Parameter(name = "id", description = "User ID", required = true)
        @PathVariable("id") id: String
    ): ResponseEntity<UserResponse> {
        userService.delete(id)
        return ResponseEntity.noContent().build()
    }
}

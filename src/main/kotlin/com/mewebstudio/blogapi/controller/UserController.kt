package com.mewebstudio.blogapi.controller

import com.mewebstudio.blogapi.dto.request.user.CreateUserRequest
import com.mewebstudio.blogapi.dto.response.DetailedErrorResponse
import com.mewebstudio.blogapi.dto.response.ErrorResponse
import com.mewebstudio.blogapi.dto.response.user.UserPaginationResponse
import com.mewebstudio.blogapi.dto.response.user.UserResponse
import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.entity.specification.criteria.PaginationCriteria
import com.mewebstudio.blogapi.entity.specification.criteria.UserCriteria
import com.mewebstudio.blogapi.security.CheckRole
import com.mewebstudio.blogapi.service.MessageSourceService
import com.mewebstudio.blogapi.service.UserService
import com.mewebstudio.blogapi.util.Constants.SECURITY_SCHEME_NAME
import com.mewebstudio.blogapi.util.Enums
import com.mewebstudio.blogapi.util.Helpers
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Pattern
import org.springframework.data.domain.Page
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/users")
@CheckRole([Enums.RoleEnum.ADMIN])
@Tag(name = "003. Auth", description = "Users API")
class UserController(
    private val userService: UserService,
    private val messageSourceService: MessageSourceService
) : AbstractBaseController() {
    companion object {
        val SORT_COLUMNS = arrayOf(
            "id", "email", "firstname", "lastname", "gender", "blockedAt", "createdAt", "updatedAt"
        )
    }

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
            )
        ]
    )
    fun list(
        @Parameter(
            name = "roles",
            description = "roles",
            example = "admin,user",
            array = ArraySchema(
                schema = Schema(type = "string", allowableValues = ["admin", "user"])
            )
        )
        @RequestParam(name = "roles", required = false) roles: Array<String>?,
        @Parameter(
            name = "genders",
            description = "Genders",
            example = "male,female,diverse,unknown",
            array = ArraySchema(
                schema = Schema(type = "string", allowableValues = ["male", "female", "diverse", "unknown"])
            )
        )
        @RequestParam(name = "genders", required = false) genders: Array<String>?,
        @Parameter(name = "createdAtStart", description = "Created date start", example = "2024-10-25T22:54:58")
        @RequestParam(name = "createdAtStart", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) createdAtStart: LocalDateTime?,
        @Parameter(name = "createdAtEnd", description = "Created date end", example = "2024-10-25T22:54:58")
        @RequestParam(name = "createdAtEnd", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) createdAtEnd: LocalDateTime?,
        @Parameter(name = "isBlocked", description = "Is blocked?", example = "true")
        @RequestParam(name = "isBlocked", defaultValue = "false", required = false) isBlocked: Boolean,
        @Parameter(name = "q", description = "Search keyword", example = "lorem")
        @RequestParam(name = "q", required = false) q: String?,
        @Parameter(name = "page", description = "Page number", example = "1")
        @RequestParam(name = "page", defaultValue = "1", required = false) page: Int,
        @Parameter(name = "size", description = "Page size", example = "20")
        @RequestParam(
            name = "size",
            defaultValue = "\${spring.data.web.pageable.default-page-size}",
            required = false
        ) size: Int,
        @Parameter(
            name = "sortBy", description = "Sort by column", example = "createdAt", schema = Schema(
                type = "String",
                allowableValues = [
                    "id", "email", "firstname", "lastname", "gender", "blockedAt", "createdAt", "updatedAt"
                ]
            )
        )
        @RequestParam(name = "sortBy", defaultValue = "createdAt", required = false) sortBy: String,
        @Parameter(
            name = "sort",
            description = "Sort direction",
            schema = Schema(type = "string", allowableValues = ["asc", "desc"], defaultValue = "asc")
        )
        @RequestParam(name = "sort", defaultValue = "asc", required = false) @Pattern(regexp = "asc|desc") sort: String
    ): ResponseEntity<UserPaginationResponse> {
        sortColumnCheck(messageSourceService, SORT_COLUMNS, sortBy)

        val users: Page<User> = userService.findAll(
            UserCriteria(
                roles = roles?.map { Helpers.searchEnum(Enums.RoleEnum::class.java, it, true)!! },
                genders = genders?.map { Helpers.searchEnum(Enums.GenderEnum::class.java, it, true)!! },
                createdAtStart = createdAtStart,
                createdAtEnd = createdAtEnd,
                isBlocked = isBlocked,
                q = q
            ),
            PaginationCriteria(page, size, sortBy, sort)
        )
        return ResponseEntity.ok(
            UserPaginationResponse(
                page = page,
                pages = users.totalPages,
                size = users.size,
                total = users.totalElements,
                items = users.map { UserResponse.convert(it) }.toList()
            )
        )
    }

    @GetMapping("/list")
    fun list2(
        @RequestParam(name = "q", required = false) q: String?
    ): ResponseEntity<String> =
        ResponseEntity.ok("Query: $q")
            .also {
                println("query: $q")
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
}

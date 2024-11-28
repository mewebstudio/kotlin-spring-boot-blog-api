package com.mewebstudio.blogapi.controller

import com.mewebstudio.blogapi.dto.request.category.CategoryFilterRequest
import com.mewebstudio.blogapi.dto.request.category.CreateCategoryRequest
import com.mewebstudio.blogapi.dto.request.category.UpdateCategoryRequest
import com.mewebstudio.blogapi.dto.response.DetailedErrorResponse
import com.mewebstudio.blogapi.dto.response.ErrorResponse
import com.mewebstudio.blogapi.dto.response.category.CategoryPaginationResponse
import com.mewebstudio.blogapi.dto.response.category.CategoryResponse
import com.mewebstudio.blogapi.entity.Category
import com.mewebstudio.blogapi.security.CheckRole
import com.mewebstudio.blogapi.service.CategoryService
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
@RequestMapping("/categories")
@Tag(name = "004. Categories", description = "Categories API")
class CategoryController(private val categoryService: CategoryService) {
    @GetMapping
    @Operation(
        summary = "List categories endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CategoryPaginationResponse::class)
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
        @ParameterObject @ModelAttribute @Validated request: CategoryFilterRequest
    ): ResponseEntity<CategoryPaginationResponse> = run {
        val categories: Page<Category> = categoryService.findAll(request)
        ResponseEntity.ok(
            CategoryPaginationResponse(
                page = request.page,
                pages = categories.totalPages,
                size = categories.size,
                total = categories.totalElements,
                items = categories.map { CategoryResponse.convert(it) }.toList()
            )
        )
    }

    @GetMapping("/{idOrSlug}")
    @Operation(
        summary = "Get category by id or slug endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = CategoryResponse::class)
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
        @Parameter(name = "idOrSlug", description = "Category ID or Slug", required = true)
        @PathVariable("idOrSlug") idOrSlug: String
    ): ResponseEntity<CategoryResponse> = ResponseEntity.ok(
        CategoryResponse.convert(
            categoryService.findByIdOrSlug(idOrSlug)
        )
    )

    @PostMapping
    @CheckRole([Enums.RoleEnum.ADMIN])
    @Operation(
        summary = "Create category endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Success operation",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = CategoryResponse::class)
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
        @Parameter(description = "Request body to category create", required = true)
        @RequestBody @Validated request: CreateCategoryRequest
    ): ResponseEntity<CategoryResponse> =
        ResponseEntity<CategoryResponse>(CategoryResponse.convert(categoryService.create(request)), HttpStatus.CREATED)

    @PatchMapping("/{id}")
    @CheckRole([Enums.RoleEnum.ADMIN])
    @Operation(
        summary = "Update category by id endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Success operation",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = CategoryResponse::class)
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
        @Parameter(name = "id", description = "Category ID", required = true)
        @PathVariable("id") id: String,
        @Parameter(description = "Request body to category update", required = true)
        @RequestBody @Validated request: UpdateCategoryRequest
    ): ResponseEntity<CategoryResponse> =
        ResponseEntity.ok(CategoryResponse.convert(categoryService.update(id, request)))

    @DeleteMapping("/{id}")
    @CheckRole([Enums.RoleEnum.ADMIN])
    @Operation(
        summary = "Delete category by id endpoint",
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
        @Parameter(name = "id", description = "Category ID", required = true)
        @PathVariable("id") id: String
    ): ResponseEntity<CategoryResponse> = run {
        categoryService.delete(id)
        ResponseEntity.noContent().build()
    }
}

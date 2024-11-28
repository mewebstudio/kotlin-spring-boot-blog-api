package com.mewebstudio.blogapi.controller

import com.mewebstudio.blogapi.dto.request.tag.TagFilterRequest
import com.mewebstudio.blogapi.dto.request.tag.CreateTagRequest
import com.mewebstudio.blogapi.dto.request.tag.UpdateTagRequest
import com.mewebstudio.blogapi.dto.response.DetailedErrorResponse
import com.mewebstudio.blogapi.dto.response.ErrorResponse
import com.mewebstudio.blogapi.dto.response.tag.TagPaginationResponse
import com.mewebstudio.blogapi.dto.response.tag.TagResponse
import com.mewebstudio.blogapi.entity.Tag
import com.mewebstudio.blogapi.security.CheckRole
import com.mewebstudio.blogapi.service.TagService
import com.mewebstudio.blogapi.util.Constants.SECURITY_SCHEME_NAME
import com.mewebstudio.blogapi.util.Enums
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
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
@RequestMapping("/tags")
@io.swagger.v3.oas.annotations.tags.Tag(name = "005. Tags", description = "Tags API")
class TagController(private val tagService: TagService) {
    @GetMapping
    @Operation(
        summary = "List tags endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = TagPaginationResponse::class)
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
        @ParameterObject @ModelAttribute @Validated request: TagFilterRequest
    ): ResponseEntity<TagPaginationResponse> = run {
        val tags: Page<Tag> = tagService.findAll(request)
        ResponseEntity.ok(
            TagPaginationResponse(
                page = request.page,
                pages = tags.totalPages,
                size = tags.size,
                total = tags.totalElements,
                items = tags.map { TagResponse.convertForList(it) }.toList()
            )
        )
    }

    @GetMapping("/{idOrSlug}")
    @Operation(
        summary = "Get tag by id or slug endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = TagResponse::class)
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
        @Parameter(name = "idOrSlug", description = "Tag ID or Slug", required = true)
        @PathVariable("idOrSlug") idOrSlug: String
    ): ResponseEntity<TagResponse> = ResponseEntity.ok(
        TagResponse.convert(
            tagService.findByIdOrSlug(idOrSlug)
        )
    )

    @PostMapping
    @CheckRole([Enums.RoleEnum.ADMIN])
    @Operation(
        summary = "Create tag endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "201",
                description = "Success operation",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = TagResponse::class)
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
        @Parameter(description = "Request body to tag create", required = true)
        @RequestBody @Validated request: CreateTagRequest
    ): ResponseEntity<TagResponse> =
        ResponseEntity<TagResponse>(TagResponse.convert(tagService.create(request)), HttpStatus.CREATED)

    @PatchMapping("/{id}")
    @CheckRole([Enums.RoleEnum.ADMIN])
    @Operation(
        summary = "Update tag by id endpoint",
        security = [SecurityRequirement(name = SECURITY_SCHEME_NAME)],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Success operation",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = TagResponse::class)
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
        @Parameter(name = "id", description = "Tag ID", required = true)
        @PathVariable("id") id: String,
        @Parameter(description = "Request body to tag update", required = true)
        @RequestBody @Validated request: UpdateTagRequest
    ): ResponseEntity<TagResponse> =
        ResponseEntity.ok(TagResponse.convert(tagService.update(id, request)))

    @DeleteMapping("/{id}")
    @CheckRole([Enums.RoleEnum.ADMIN])
    @Operation(
        summary = "Delete tag by id endpoint",
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
        @Parameter(name = "id", description = "Tag ID", required = true)
        @PathVariable("id") id: String
    ): ResponseEntity<TagResponse> = run {
        tagService.delete(id)
        ResponseEntity.noContent().build()
    }
}

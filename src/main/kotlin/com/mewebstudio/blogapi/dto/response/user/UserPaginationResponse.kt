package com.mewebstudio.blogapi.dto.response.user

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

data class UserPaginationResponse(
    @Schema(name = "page", description = "Current page number (1-based)", example = "1")
    val page: Int,

    @Schema(name = "pages", description = "Total number of pages", example = "3")
    val pages: Int,

    @Schema(name = "size", description = "Size of each page", example = "3")
    val size: Int,

    @Schema(name = "total", description = "Total number of items", example = "10")
    val total: Long,

    @ArraySchema(schema = Schema(implementation = UserResponse::class, description = "List of users"))
    val items: List<UserResponse>
)

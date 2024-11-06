package com.mewebstudio.blogapi.dto.response.user

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

data class UserPaginationResponse(
    @Schema(
        name = "page",
        description = "Current page number (1-based)",
        type = "Integer",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "1"
    )
    val page: Int,

    @Schema(
        name = "pages",
        description = "Total number of pages",
        type = "Integer",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "3"
    )
    val pages: Int,

    @Schema(
        name = "size",
        description = "Size of each page",
        type = "Integer",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "3"
    )
    val size: Int,

    @Schema(
        name = "total",
        description = "Total number of items",
        type = "Integer",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "10"
    )
    val total: Long,

    @ArraySchema(
        schema = Schema(
            implementation = UserResponse::class,
            requiredMode = Schema.RequiredMode.REQUIRED,
            description = "List of users"
        )
    )
    val items: List<UserResponse>
)

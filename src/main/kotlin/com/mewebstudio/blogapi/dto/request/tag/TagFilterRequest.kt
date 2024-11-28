package com.mewebstudio.blogapi.dto.request.tag

import com.mewebstudio.blogapi.dto.annotation.CheckUUID
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.data.domain.Sort
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

data class TagFilterRequest(
    @field:Size(min = 1, message = "{min_list_size}")
    @field:CheckUUID(message = "{invalid_uuid}")
    @field:Parameter(
        name = "createdUsers",
        description = "Created users",
        example = "6f319190-2efd-4d8c-9ba8-f1db7ef0a355,51ecc236-09dd-4d15-bb05-bc1343b4e934",
    )
    val createdUsers: List<String>? = null,

    @field:Size(min = 1, message = "{min_list_size}")
    @field:Parameter(
        name = "updatedUsers",
        description = "Updated users",
        example = "6f319190-2efd-4d8c-9ba8-f1db7ef0a355,51ecc236-09dd-4d15-bb05-bc1343b4e934",
    )
    val updatedUsers: List<String>? = null,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:Parameter(
        name = "createdAtStart",
        description = "Created date start",
        example = "2024-10-25T22:54:58"
    )
    val createdAtStart: LocalDateTime? = null,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:Parameter(
        name = "createdAtEnd",
        description = "Created date end",
        example = "2024-10-25T22:54:58"
    )
    val createdAtEnd: LocalDateTime? = null,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:Parameter(
        name = "updatedAtStart",
        description = "Created date start",
        example = "2024-10-25T22:54:58"
    )
    val updatedAtStart: LocalDateTime? = null,

    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:Parameter(
        name = "updatedAtEnd",
        description = "Created date end",
        example = "2024-10-25T22:54:58"
    )
    val updatedAtEnd: LocalDateTime? = null,

    @field:Parameter(
        name = "q",
        description = "Search keyword",
        example = "lorem"
    )
    val q: String? = null,

    @field:Min(value = 1, message = "{min_value}")
    @field:Parameter(
        name = "page",
        description = "Page number",
        example = "1"
    )
    val page: Int = 1,

    @field:Min(value = 1, message = "{min_value}")
    @field:Parameter(
        name = "size",
        description = "Page size",
        example = "20"
    )
    val size: Int = 20,

    @field:Pattern(
        regexp = "id|title|slug|createdAt|updatedAt",
        message = "{invalid_sort_by_column}"
    )
    @field:Parameter(
        name = "sortBy",
        description = "Sort by column",
        example = "createdAt",
        schema = Schema(
            type = "String",
            allowableValues = ["id", "title", "slug", "blockedAt", "createdAt", "updatedAt"],
            defaultValue = "createdAt"
        )
    )
    val sortBy: String = "createdAt",

    @field:Pattern(regexp = "(?i)asc|desc", message = "{invalid_sort_direction}")
    @field:Parameter(
        name = "sort",
        description = "Sort direction",
        schema = Schema(type = "string", allowableValues = ["asc", "desc"], defaultValue = "asc")
    )
    val sort: String = Sort.Direction.ASC.name
)

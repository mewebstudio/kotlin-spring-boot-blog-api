package com.mewebstudio.blogapi.dto.request.user

import com.mewebstudio.blogapi.dto.annotation.MinListSize
import com.mewebstudio.blogapi.dto.annotation.ValueOfEnum
import com.mewebstudio.blogapi.util.Enums
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

data class UserFilterRequest(
    @field:MinListSize(min = 1, message = "{min_list_size}")
    @field:ValueOfEnum(enumClass = Enums.RoleEnum::class)
    @field:Parameter(
        name = "roles",
        description = "roles",
        example = "admin,user",
        array = ArraySchema(
            schema = Schema(type = "string", allowableValues = ["admin", "user"])
        )
    )
    val roles: List<String>? = null,

    @field:MinListSize(min = 1, message = "{min_list_size}")
    @field:ValueOfEnum(enumClass = Enums.GenderEnum::class)
    @field:Parameter(
        name = "genders",
        description = "Genders",
        example = "male,female,diverse,unknown",
        array = ArraySchema(
            schema = Schema(type = "string", allowableValues = ["male", "female", "diverse", "unknown"])
        )
    )
    val genders: List<String>? = null,

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

    @field:Parameter(
        name = "isBlocked",
        description = "Is blocked?",
        example = "true"
    )
    val isBlocked: Boolean = false,

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
        regexp = "id|email|firstname|lastname|gender|blockedAt|createdAt|updatedAt",
        message = "{invalid_sort_by_column}"
    )
    @field:Parameter(
        name = "sortBy",
        description = "Sort by column",
        example = "createdAt",
        schema = Schema(
            type = "String",
            allowableValues = ["id", "email", "firstname", "lastname", "gender", "blockedAt", "createdAt", "updatedAt"]
        )
    )
    val sortBy: String = "createdAt",

    @field:Pattern(regexp = "asc|desc", message = "{invalid_sort_direction}")
    @field:Parameter(
        name = "sort",
        description = "Sort direction",
        schema = Schema(type = "string", allowableValues = ["asc", "desc"], defaultValue = "asc")
    )
    val sort: String = "asc"
)

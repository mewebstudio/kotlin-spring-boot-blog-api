package com.mewebstudio.blogapi.dto.request.category

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

data class UpdateCategoryRequest(
    @field:Size(min = 3, max = 120, message = "{min_max_length}")
    @Schema(
        name = "title",
        description = "Title",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "John"
    )
    var title: String?,

    @field:Size(max = 500, message = "{max_length}")
    @Schema(
        name = "description",
        description = "Description",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "DOE"
    )
    var description: String?
)

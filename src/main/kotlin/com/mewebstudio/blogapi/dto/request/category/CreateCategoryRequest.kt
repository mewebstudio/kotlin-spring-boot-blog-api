package com.mewebstudio.blogapi.dto.request.category

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateCategoryRequest(
    @field:NotBlank(message = "{not_blank}")
    @field:Size(min = 3, max = 120, message = "{min_max_length}")
    @Schema(
        name = "title",
        description = "Title",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "Lorem Ipsum"
    )
    var title: String?,

    @field:Size(max = 500, message = "{max_length}")
    @Schema(
        name = "description",
        description = "Description",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    )
    var description: String?,
)

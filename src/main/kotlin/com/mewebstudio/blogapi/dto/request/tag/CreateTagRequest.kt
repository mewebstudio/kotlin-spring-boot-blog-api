package com.mewebstudio.blogapi.dto.request.tag

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateTagRequest(
    @field:NotBlank(message = "{not_blank}")
    @field:Size(min = 3, max = 120, message = "{min_max_length}")
    @Schema(
        name = "title",
        description = "Title",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "Lorem Ipsum"
    )
    var title: String?
)

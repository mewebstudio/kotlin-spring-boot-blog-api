package com.mewebstudio.blogapi.dto.request.tag

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

data class UpdateTagRequest(
    @field:Size(min = 3, max = 120, message = "{min_max_length}")
    @Schema(
        name = "title",
        description = "Title",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "John"
    )
    var title: String?
)

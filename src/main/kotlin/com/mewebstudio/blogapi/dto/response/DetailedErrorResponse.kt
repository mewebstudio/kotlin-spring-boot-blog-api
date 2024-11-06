package com.mewebstudio.blogapi.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class DetailedErrorResponse(
    override val message: String,
    @Schema(
        name = "items",
        description = "Error message",
        type = "MutableMap",
        nullable = true,
        example = "{\"foo\": \"Bar\"}"
    )
    var items: MutableMap<String, String?>
) : ErrorResponse(message)

package com.mewebstudio.blogapi.dto.response

import io.swagger.v3.oas.annotations.media.Schema

open class SuccessResponse(
    @Schema(
        name = "message",
        description = "Response messages field",
        type = "String",
        example = "This is message field"
    )
    open val message: String
) : AbstractBaseResponse()

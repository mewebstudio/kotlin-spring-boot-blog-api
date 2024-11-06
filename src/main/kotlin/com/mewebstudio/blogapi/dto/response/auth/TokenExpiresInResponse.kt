package com.mewebstudio.blogapi.dto.response.auth

import com.mewebstudio.blogapi.dto.response.AbstractBaseResponse
import io.swagger.v3.oas.annotations.media.Schema

data class TokenExpiresInResponse(
    @Schema(
        name = "token",
        description = "Token expires In",
        type = "Long",
        example = "3600"
    )
    var token: Long,

    @Schema(
        name = "refreshToken",
        description = "Refresh token expires In",
        type = "Long",
        example = "7200"
    )
    var refreshToken: Long,
) : AbstractBaseResponse()

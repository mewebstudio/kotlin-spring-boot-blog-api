package com.mewebstudio.blogapi.dto.response.auth

import com.mewebstudio.blogapi.dto.response.AbstractBaseResponse
import io.swagger.v3.oas.annotations.media.Schema

data class TokenResponse(
    @Schema(name = "token", description = "Token", type = "String", example = "eyJhbGciOiJIUzUxMiJ9...")
    var token: String,

    @Schema(
        name = "refreshToken",
        description = "Refresh Token",
        type = "String",
        example = "eyJhbGciOiJIUzUxMiJ9..."
    )
    var refreshToken: String,

    @Schema(name = "expiresIn", description = "Expires In", type = "TokenExpiresInResponse")
    var expiresIn: TokenExpiresInResponse,
) : AbstractBaseResponse()

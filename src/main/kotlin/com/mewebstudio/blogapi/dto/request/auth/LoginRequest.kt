package com.mewebstudio.blogapi.dto.request.auth

import com.mewebstudio.blogapi.dto.annotation.Password
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "{not_blank}")
    @field:Email(message = "{invalid_email}")
    @Schema(
        name = "email",
        description = "E-mail",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "mail@example.com"
    )
    var email: String?,

    @field:NotBlank(message = "{not_blank}")
    @field:Password(message = "{invalid_password}")
    @Schema(
        name = "password",
        description = "Password",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "P@sswd123."
    )
    var password: String?,

    @Schema(
        name = "rememberMe",
        description = "Remember option for token expiration",
        type = "Boolean",
        example = "true"
    )
    var rememberMe: Boolean?
)

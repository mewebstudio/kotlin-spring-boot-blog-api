package com.mewebstudio.blogapi.dto.request.auth

import com.mewebstudio.blogapi.dto.annotation.FieldMatch
import com.mewebstudio.blogapi.dto.annotation.Password
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@FieldMatch(first = "password", second = "passwordConfirm", message = "{password_mismatch}")
data class ChangePasswordRequest(
    @field:NotBlank(message = "{not_blank}")
    @field:Password(message = "{invalid_password}")
    @Schema(
        name = "password",
        description = "Password",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "P@sswd123."
    )
    var password: String? = null,

    @field:NotBlank(message = "{not_blank}")
    @field:Password(message = "{invalid_password}")
    @Schema(
        name = "passwordConfirm",
        description = "Password confirmation",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "P@sswd123."
    )
    var passwordConfirm: String? = null,
)

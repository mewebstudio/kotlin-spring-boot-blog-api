package com.mewebstudio.blogapi.dto.request.user

import com.mewebstudio.blogapi.dto.annotation.FieldMatch
import com.mewebstudio.blogapi.dto.annotation.Password
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email

@FieldMatch(first = "password", second = "passwordConfirm", message = "{password_mismatch}")
data class UpdateProfileRequest(
    @Schema(
        name = "firstname",
        description = "Firstname",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "John"
    )
    var firstname: String?,

    @Schema(
        name = "lastname",
        description = "Lastname",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "DOE"
    )
    var lastname: String?,

    @Schema(
        name = "gender",
        description = "Gender of the user",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        allowableValues = ["male", "female", "diverse"],
        example = "male"
    )
    var gender: String? = null,

    @field:Email(message = "{invalid_email}")
    @Schema(
        name = "email",
        description = "E-mail",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "mail@example.com"
    )
    var email: String?,

    @field:Password(message = "{invalid_password}")
    @Schema(
        name = "password",
        description = "Password",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "P@sswd123."
    )
    var password: String? = null,

    @field:Password(message = "{invalid_password}")
    @Schema(
        name = "passwordConfirm",
        description = "Password confirmation",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "P@sswd123."
    )
    var passwordConfirm: String? = null,
)

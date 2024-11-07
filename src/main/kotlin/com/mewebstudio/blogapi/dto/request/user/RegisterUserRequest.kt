package com.mewebstudio.blogapi.dto.request.user

import com.mewebstudio.blogapi.dto.annotation.Password
import com.mewebstudio.blogapi.dto.annotation.ValueOfEnum
import com.mewebstudio.blogapi.util.Enums
import com.mewebstudio.blogapi.dto.annotation.FieldMatch
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@FieldMatch(first = "password", second = "passwordConfirm", message = "{password_mismatch}")
data class RegisterUserRequest(
    @field:NotBlank(message = "{not_blank}")
    @Schema(
        name = "firstname",
        description = "Firstname",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "John"
    )
    override var firstname: String?,

    @field:NotBlank(message = "{not_blank}")
    @Schema(
        name = "lastname",
        description = "Lastname",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "DOE"
    )
    override var lastname: String?,

    @field:NotBlank(message = "{not_blank}")
    @field:ValueOfEnum(enumClass = Enums.GenderEnum::class)
    @Schema(
        name = "gender",
        description = "Gender",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        allowableValues = ["male", "female", "diverse", "unknown"],
        example = "male"
    )
    override var gender: String = Enums.GenderEnum.UNKNOWN.value,

    @field:NotBlank(message = "{not_blank}")
    @field:Email(message = "{invalid_email}")
    @Schema(
        name = "email",
        description = "E-mail",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "mail@example.com"
    )
    override var email: String?,

    @field:NotBlank(message = "{not_blank}")
    @field:Password(message = "{invalid_password}")
    @Schema(
        name = "password",
        description = "Password",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "P@sswd123."
    )
    override var password: String? = null,

    @field:NotBlank(message = "{not_blank}")
    @field:Password(message = "{invalid_password}")
    @Schema(
        name = "passwordConfirm",
        description = "Password confirmation",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "P@sswd123."
    )
    override var passwordConfirm: String? = null
) : IUserRequest

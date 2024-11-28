package com.mewebstudio.blogapi.dto.request.user

import com.mewebstudio.blogapi.dto.annotation.Password
import com.mewebstudio.blogapi.dto.annotation.ValueOfEnum
import com.mewebstudio.blogapi.util.Enums
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UpdateProfileRequest(
    @field:Size(max = 50, message = "{max_length}")
    @Schema(
        name = "firstname",
        description = "Firstname",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "John"
    )
    override var firstname: String?,

    @field:Size(max = 50, message = "{max_length}")
    @Schema(
        name = "lastname",
        description = "Lastname",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "DOE"
    )
    override var lastname: String?,

    @field:ValueOfEnum(enumClass = Enums.GenderEnum::class)
    @Schema(
        name = "gender",
        description = "Gender",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        allowableValues = ["male", "female", "diverse", "unknown"],
        example = "male"
    )
    override var gender: String?,

    @field:Email(message = "{invalid_email}")
    @field:Size(max = 255, message = "{max_length}")
    @Schema(
        name = "email",
        description = "E-mail",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "mail@example.com"
    )
    override var email: String?,

    @field:Password(message = "{invalid_password}")
    @Schema(
        name = "password",
        description = "Password",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "P@sswd123."
    )
    override var password: String? = null,

    @field:Password(message = "{invalid_password}")
    @Schema(
        name = "passwordConfirm",
        description = "Password confirmation",
        type = "String",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "P@sswd123."
    )
    override var passwordConfirm: String? = null
): IUserRequest

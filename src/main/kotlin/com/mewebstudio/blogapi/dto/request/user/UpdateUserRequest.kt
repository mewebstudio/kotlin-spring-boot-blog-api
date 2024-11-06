package com.mewebstudio.blogapi.dto.request.user

import com.mewebstudio.blogapi.dto.annotation.MinListSize
import com.mewebstudio.blogapi.dto.annotation.Password
import com.mewebstudio.blogapi.dto.annotation.ValueOfEnum
import com.mewebstudio.blogapi.util.Enums
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email

data class UpdateUserRequest(
    @Schema(
        name = "firstname",
        description = "Firstname",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "John"
    )
    override var firstname: String?,

    @Schema(
        name = "lastname",
        description = "Lastname",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "DOE"
    )
    override var lastname: String?,

    @field:ValueOfEnum(enumClass = Enums.GenderEnum::class)
    @Schema(
        name = "gender",
        description = "Gender",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        allowableValues = ["male", "female", "diverse"],
        example = "male"
    )
    override var gender: String?,

    @field:Email(message = "{invalid_email}")
    @Schema(
        name = "email",
        description = "E-mail",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "mail@example.com"
    )
    override var email: String?,

    @field:Password(message = "{invalid_password}")
    @Schema(
        name = "password",
        description = "Password",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "P@sswd123."
    )
    override var password: String? = null,

    @field:Password(message = "{invalid_password}")
    @Schema(
        name = "passwordConfirm",
        description = "Password confirmation",
        type = "String",
        requiredMode = Schema.RequiredMode.REQUIRED,
        example = "P@sswd123."
    )
    override var passwordConfirm: String? = null,

    @field:MinListSize(min = 1, message = "{min_list_size}")
    @field:ValueOfEnum(enumClass = Enums.RoleEnum::class)
    @Schema(
        name = "roles",
        description = "Roles of the user",
        type = "List<String>",
        requiredMode = Schema.RequiredMode.REQUIRED,
        allowableValues = ["admin", "user"],
        example = "[\"user\"]"
    )
    var roles: List<String>? = null,

    @Schema(
        name = "isBlocked",
        description = "Block user",
        type = "Boolean",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        example = "false"
    )
    var isBlocked: Boolean? = false
) : IUpdateUserRequest

package com.mewebstudio.blogapi.dto.response.user

import com.mewebstudio.blogapi.dto.response.AbstractBaseResponse
import com.mewebstudio.blogapi.entity.User
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import java.time.LocalDateTime

data class UserResponse(
    @Schema(
        name = "id",
        description = "ID",
        type = "String",
        requiredMode = RequiredMode.REQUIRED,
        example = "91b2999d-d327-4dc8-9956-2fadc0dc8778"
    )
    val id: String? = null,

    @Schema(
        name = "firstname",
        description = "Firstname",
        type = "String",
        requiredMode = RequiredMode.REQUIRED,
        example = "John"
    )
    val firstname: String? = null,

    @Schema(
        name = "lastname",
        description = "Lastname",
        type = "String",
        requiredMode = RequiredMode.REQUIRED,
        example = "Doe"
    )
    val lastname: String? = null,

    @Schema(
        name = "gender",
        description = "Gender",
        type = "String",
        requiredMode = RequiredMode.REQUIRED,
        allowableValues = ["male", "female", "other"],
        example = "male"
    )
    val gender: String? = null,

    @Schema(
        name = "fullName",
        description = "Full name",
        type = "String",
        requiredMode = RequiredMode.REQUIRED,
        example = "John Doe"
    )
    val fullName: String? = null,

    @Schema(
        name = "email",
        description = "E-mail",
        type = "String",
        requiredMode = RequiredMode.REQUIRED,
        example = "john@example.com"
    )
    val email: String? = null,

    @Schema(
        name = "roles",
        description = "Roles",
        type = "Array",
        requiredMode = RequiredMode.REQUIRED,
        example = "[\"user\"]"
    )
    val roles: Array<String> = emptyArray(),

    @Schema(
        name = "blockedAt",
        description = "Blocked at",
        type = "LocalDateTime",
        requiredMode = RequiredMode.NOT_REQUIRED
    )
    val blockedAt: LocalDateTime? = null,

    @Schema(
        name = "emailVerifiedAt",
        description = "E-mail verified at",
        type = "LocalDateTime",
        requiredMode = RequiredMode.NOT_REQUIRED
    )
    val emailVerifiedAt: LocalDateTime? = null,

    @Schema(
        name = "createdUser",
        description = "Created user",
        type = "UserResponse",
        requiredMode = RequiredMode.NOT_REQUIRED
    )
    var createdUser: UserResponse? = null,

    @Schema(
        name = "updatedUser",
        description = "Updated user",
        type = "UserResponse",
        requiredMode = RequiredMode.NOT_REQUIRED
    )
    var updatedUser: UserResponse? = null,

    @Schema(
        name = "createdAt",
        description = "Created at",
        type = "LocalDateTime",
        requiredMode = RequiredMode.REQUIRED
    )
    val createdAt: LocalDateTime? = null,

    @Schema(
        name = "updatedAt",
        description = "Updated at",
        type = "LocalDateTime",
        requiredMode = RequiredMode.REQUIRED
    )
    val updatedAt: LocalDateTime? = null
) : AbstractBaseResponse() {
    companion object {
        fun convert(user: User, isDetailed: Boolean): UserResponse = UserResponse(
            id = user.id.toString(),
            firstname = user.firstname,
            lastname = user.lastname,
            fullName = user.getFullName(),
            gender = user.gender.value,
            email = user.email,
            roles = user.roles.stream().map { it.lowercase() }.toArray { size -> arrayOfNulls(size) },
            blockedAt = user.blockedAt,
            emailVerifiedAt = user.emailVerifiedAt,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        ).apply {
            isDetailed.takeIf { it }?.run {
                createdUser = user.createdUser?.let { convertForRelation(it) }
                updatedUser = user.updatedUser?.let { convertForRelation(it) }
            }
        }

        fun convert(user: User): UserResponse = convert(user, true)

        fun convertForList(user: User): UserResponse = convert(user, false)

        fun convertForRelation(user: User): UserResponse = convert(user, false)
    }

    override fun equals(other: Any?): Boolean = run {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserResponse

        id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

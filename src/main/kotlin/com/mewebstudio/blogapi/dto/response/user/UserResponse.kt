package com.mewebstudio.blogapi.dto.response.user

import com.mewebstudio.blogapi.dto.response.AbstractBaseResponse
import com.mewebstudio.blogapi.entity.User
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class UserResponse(
    @Schema(
        name = "id",
        description = "ID",
        type = "String",
        example = "91b2999d-d327-4dc8-9956-2fadc0dc8778"
    )
    val id: String,

    @Schema(
        name = "firstname",
        description = "Firstname",
        type = "String",
        example = "John"
    )
    val firstname: String,

    @Schema(
        name = "lastname",
        description = "Lastname",
        type = "String",
        example = "Doe"
    )
    val lastname: String,

    @Schema(
        name = "gender",
        description = "Gender",
        type = "String",
        allowableValues = ["male", "female", "other"],
        example = "Doe"
    )
    val gender: String,

    @Schema(
        name = "fullName",
        description = "Full name",
        type = "String",
        example = "John Doe"
    )
    val fullName: String,

    @Schema(
        name = "email",
        description = "E-mail",
        type = "String",
        example = "john@example.com"
    )
    val email: String,

    @Schema(
        name = "roles",
        description = "Roles",
        type = "Array",
        example = "[\"user\"]"
    )
    val roles: Array<String>,

    @Schema(
        name = "blockedAt",
        description = "Blocked at",
        type = "LocalDateTime"
    )
    val blockedAt: LocalDateTime?,

    @Schema(
        name = "createdAt",
        description = "Created at",
        type = "LocalDateTime"
    )
    val createdAt: LocalDateTime?,

    @Schema(
        name = "updatedAt",
        description = "Updated at",
        type = "LocalDateTime"
    )
    val updatedAt: LocalDateTime?
) : AbstractBaseResponse() {
    companion object {
        fun convert(user: User): UserResponse {
            return UserResponse(
                id = user.id.toString(),
                firstname = user.firstname,
                lastname = user.lastname,
                fullName = user.fullName,
                gender = user.gender.value,
                email = user.email,
                roles = user.roles.stream().map { it.lowercase() }.toArray { size -> arrayOfNulls(size) },
                blockedAt = user.blockedAt,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserResponse

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

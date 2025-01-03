package com.mewebstudio.blogapi.dto.response.category

import com.mewebstudio.blogapi.dto.response.AbstractBaseResponse
import com.mewebstudio.blogapi.dto.response.user.UserResponse
import com.mewebstudio.blogapi.entity.Category
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import java.time.LocalDateTime

data class CategoryResponse(
    @Schema(
        name = "id",
        description = "ID",
        type = "String",
        requiredMode = RequiredMode.REQUIRED,
        example = "91b2999d-d327-4dc8-9956-2fadc0dc8778"
    )
    val id: String? = null,

    @Schema(
        name = "title",
        description = "Title",
        type = "String",
        requiredMode = RequiredMode.REQUIRED,
        example = "Lorem Ipsum"
    )
    val title: String? = null,

    @Schema(
        name = "slug",
        description = "Slug",
        type = "String",
        requiredMode = RequiredMode.REQUIRED,
        example = "lorem-ipsum"
    )
    val slug: String? = null,

    @Schema(
        name = "description",
        description = "Description",
        type = "String",
        requiredMode = RequiredMode.NOT_REQUIRED,
        example = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    )
    val description: String? = null,

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
        fun convert(category: Category, isDetailed: Boolean): CategoryResponse = CategoryResponse(
            id = category.id.toString(),
            title = category.title,
            slug = category.slug,
            description = category.description,
            createdAt = category.createdAt,
            updatedAt = category.updatedAt
        ).apply {
            isDetailed.takeIf { it }?.run {
                createdUser = category.createdUser?.let { UserResponse.convertForRelation(it) }
                updatedUser = category.updatedUser?.let { UserResponse.convertForRelation(it) }
            }
        }

        fun convert(category: Category): CategoryResponse = convert(category, true)

        fun convertForList(category: Category): CategoryResponse = convert(category, false)
    }

    override fun equals(other: Any?): Boolean = run {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryResponse

        id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

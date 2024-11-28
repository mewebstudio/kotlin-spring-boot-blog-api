package com.mewebstudio.blogapi.dto.response.tag

import com.mewebstudio.blogapi.dto.response.AbstractBaseResponse
import com.mewebstudio.blogapi.dto.response.user.UserResponse
import com.mewebstudio.blogapi.dto.response.user.UserResponse.Companion.convertForRelation
import com.mewebstudio.blogapi.entity.Tag
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import java.time.LocalDateTime

data class TagResponse(
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
    val createdAt: LocalDateTime?,

    @Schema(
        name = "updatedAt",
        description = "Updated at",
        type = "LocalDateTime",
        requiredMode = RequiredMode.REQUIRED
    )
    val updatedAt: LocalDateTime?
) : AbstractBaseResponse() {
    companion object {
        fun convert(tag: Tag, isDetailed: Boolean): TagResponse = TagResponse(
            id = tag.id.toString(),
            title = tag.title,
            slug = tag.slug,
            createdAt = tag.createdAt,
            updatedAt = tag.updatedAt
        ).apply {
            isDetailed.takeIf { it }?.run {
                createdUser = tag.createdUser?.let { convertForRelation(it) }
                updatedUser = tag.updatedUser?.let { convertForRelation(it) }
            }
        }

        fun convert(tag: Tag): TagResponse = convert(tag, true)

        fun convertForList(tag: Tag): TagResponse = convert(tag, false)
    }

    override fun equals(other: Any?): Boolean = run {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TagResponse

        id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

package com.mewebstudio.blogapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ForeignKey
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime

@Entity
@Table(
    name = "comments",
    indexes = [
        Index(columnList = "title", name = "idx_comments_title"),
        Index(columnList = "content", name = "idx_comments_content")
    ]
)
data class Comment(
    @ManyToOne(optional = false)
    @JoinColumn(
        name = "post_id",
        referencedColumnName = "id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_comments_post_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var post: Post? = null,

    @ManyToOne(optional = true)
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "id",
        nullable = true,
        foreignKey = ForeignKey(name = "fk_comments_user_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var user: User? = null,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "slug", nullable = false)
    var slug: String,

    @Column(name = "content", nullable = false)
    var content: String? = null,

    @Column(name = "published_at")
    var publishedAt: LocalDateTime? = null,
) : AbstractBaseEntity() {
    constructor() : this(title = "", slug = "")

    override fun equals(other: Any?): Boolean = run {
        if (this === other) return true
        if (other !is Post) return false

        id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = this::class.simpleName + "(id = $id, title = $title)"
}

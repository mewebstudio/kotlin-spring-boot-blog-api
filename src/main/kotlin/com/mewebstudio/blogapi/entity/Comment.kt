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
        Index(columnList = "content", name = "idx_comments_content")
    ]
)
class Comment(
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

    @Column(name = "content", nullable = false)
    var content: String? = null,

    @Column(name = "published_at")
    var publishedAt: LocalDateTime? = null,
) : AbstractBaseEntity() {
    override fun toString(): String = this::class.simpleName + "(id = $id, post = ${post})"
}

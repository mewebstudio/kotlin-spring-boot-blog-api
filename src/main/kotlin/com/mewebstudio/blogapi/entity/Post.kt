package com.mewebstudio.blogapi.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime


@Entity
@Table(
    name = "posts",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["title"], name = "uk_posts_title"),
        UniqueConstraint(columnNames = ["slug"], name = "uk_posts_slug")
    ],
    indexes = [
        Index(columnList = "title", name = "idx_posts_title"),
        Index(columnList = "slug", name = "idx_posts_slug"),
        Index(columnList = "content", name = "idx_posts_content")
    ]
)
class Post(
    @Column(name = "title", nullable = false)
    var title: String? = null,

    @Column(name = "slug", nullable = false, columnDefinition = "text")
    var slug: String? = null,

    @Column(name = "content", nullable = false, columnDefinition = "text")
    var content: String? = null,

    @Column(name = "published_at")
    var publishedAt: LocalDateTime? = null,

    @ManyToOne(optional = true)
    @JoinColumn(
        name = "created_user_id",
        referencedColumnName = "id",
        nullable = true,
        foreignKey = ForeignKey(name = "fk_posts_created_user_id")
    )
    @OnDelete(action = OnDeleteAction.SET_NULL)
    var createdUser: User? = null,

    @ManyToOne(optional = true)
    @JoinColumn(
        name = "updated_user_id",
        referencedColumnName = "id",
        nullable = true,
        foreignKey = ForeignKey(name = "fk_posts_updated_user_id")
    )
    @OnDelete(action = OnDeleteAction.SET_NULL)
    var updatedUser: User? = null,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
    @JoinTable(
        name = "post_categories",
        joinColumns = [JoinColumn(
            name = "category_id",
            foreignKey = ForeignKey(
                name = "fk_post_categories_category_id",
                foreignKeyDefinition = "FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE"
            ),
            nullable = false
        )],
        inverseJoinColumns = [JoinColumn(
            name = "post_id",
            foreignKey = ForeignKey(
                name = "fk_post_categories_post_id",
                foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE"
            ),
            nullable = false
        )],
        uniqueConstraints = [UniqueConstraint(
            columnNames = ["post_id", "category_id"],
            name = "uk_post_categories_post_id_category_id"
        )]
    )
    var categories: List<Category> = arrayListOf(),

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
    @JoinTable(
        name = "post_tags",
        joinColumns = [JoinColumn(
            name = "tag_id",
            foreignKey = ForeignKey(
                name = "fk_post_tags_tag_id",
                foreignKeyDefinition = "FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE"
            ),
            nullable = false
        )],
        inverseJoinColumns = [JoinColumn(
            name = "post_id",
            foreignKey = ForeignKey(
                name = "fk_post_tags_post_id",
                foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE"
            ),
            nullable = false
        )],
        uniqueConstraints = [UniqueConstraint(
            columnNames = ["post_id", "tag_id"],
            name = "uk_post_tags_post_id_tag_id"
        )]
    )
    var tags: List<Tag> = arrayListOf(),
) : AbstractBaseEntity() {
    override fun toString(): String = this::class.simpleName + "(id = $id, title = $title)"
}

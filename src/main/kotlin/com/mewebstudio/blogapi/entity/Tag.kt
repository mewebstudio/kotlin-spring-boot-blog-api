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
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "tags",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["slug"], name = "uk_tags_slug")
    ],
    indexes = [
        Index(columnList = "title", name = "idx_tags_title"),
        Index(columnList = "slug", name = "idx_tags_slug")
    ]
)
class Tag(
    @Column(name = "title", nullable = false)
    var title: String? = null,

    @Column(name = "slug", nullable = false)
    var slug: String? = null,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
    @JoinTable(
        joinColumns = [JoinColumn(
            name = "post_id",
            foreignKey = ForeignKey(
                name = "fk_post_tags_post_id",
                foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE"
            ),
            nullable = false
        )],
        name = "post_tags",
        inverseJoinColumns = [JoinColumn(
            name = "tag_id",
            foreignKey = ForeignKey(
                name = "fk_post_tags_tag_id",
                foreignKeyDefinition = "FOREIGN KEY (tag_id) REFERENCES tags (id) ON DELETE CASCADE"
            ),
            nullable = false
        )],
        uniqueConstraints = [UniqueConstraint(
            columnNames = ["post_id", "tag_id"],
            name = "uk_post_tags_post_id_tag_id"
        )]
    )
    var posts: List<Post> = arrayListOf(),
) : AbstractBaseEntity() {
    override fun toString(): String = this::class.simpleName + "(id = $id, title = $title)"
}

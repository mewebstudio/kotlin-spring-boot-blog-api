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
    name = "categories",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["slug"], name = "uk_categories_slug")
    ],
    indexes = [
        Index(columnList = "title", name = "idx_categories_title"),
        Index(columnList = "slug", name = "idx_categories_slug")
    ]
)
data class Category(
    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "slug", nullable = false)
    var slug: String,

    @Column(name = "description")
    var description: String? = null,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.DETACH])
    @JoinTable(
        joinColumns = [JoinColumn(
            name = "post_id",
            foreignKey = ForeignKey(
                name = "fk_post_categories_post_id",
                foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE"
            ),
            nullable = false
        )],
        name = "post_categories",
        inverseJoinColumns = [JoinColumn(
            name = "category_id",
            foreignKey = ForeignKey(
                name = "fk_post_categories_category_id",
                foreignKeyDefinition = "FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE"
            ),
            nullable = false
        )],
        uniqueConstraints = [UniqueConstraint(
            columnNames = ["post_id", "category_id"],
            name = "uk_post_categories_post_id_category_id"
        )]
    )
    var posts: List<Post> = arrayListOf(),
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

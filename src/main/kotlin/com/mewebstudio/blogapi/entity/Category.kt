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

@Entity
@Table(
    name = "categories",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["title"], name = "uk_categories_title"),
        UniqueConstraint(columnNames = ["slug"], name = "uk_categories_slug")
    ],
    indexes = [
        Index(columnList = "title", name = "idx_categories_title"),
        Index(columnList = "slug", name = "idx_categories_slug")
    ]
)
class Category(
    @Column(name = "title", nullable = false)
    var title: String? = null,

    @Column(name = "slug", nullable = false, columnDefinition = "text")
    var slug: String? = null,

    @Column(name = "description", columnDefinition = "text")
    var description: String? = null,

    @ManyToOne(optional = true)
    @JoinColumn(
        name = "created_user_id",
        referencedColumnName = "id",
        nullable = true,
        foreignKey = ForeignKey(name = "fk_categories_created_user_id")
    )
    @OnDelete(action = OnDeleteAction.SET_NULL)
    var createdUser: User? = null,

    @ManyToOne(optional = true)
    @JoinColumn(
        name = "updated_user_id",
        referencedColumnName = "id",
        nullable = true,
        foreignKey = ForeignKey(name = "fk_categories_updated_user_id")
    )
    @OnDelete(action = OnDeleteAction.SET_NULL)
    var updatedUser: User? = null,

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
    override fun toString(): String = this::class.simpleName + "(id = $id, title = $title)"
}

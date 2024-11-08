package com.mewebstudio.blogapi.entity

import com.mewebstudio.blogapi.entity.converter.StringArrayConverter
import com.mewebstudio.blogapi.util.Enums
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["email"], name = "uk_users_email")
    ],
    indexes = [
        Index(columnList = "email", name = "idx_users_email"),
        Index(columnList = "firstname", name = "idx_users_firstname"),
        Index(columnList = "lastname", name = "idx_users_lastname"),
        Index(columnList = "gender", name = "idx_users_gender")
    ]
)
class User(
    @Column(name = "email", nullable = false)
    var email: String? = null,

    @Column(name = "password", nullable = false)
    var password: String? = null,

    @Column(name = "firstname", nullable = false, length = 50)
    var firstname: String? = null,

    @Column(name = "lastname", nullable = false, length = 50)
    var lastname: String? = null,

    var fullName: String = "$firstname $lastname",

    @Column(name = "gender", nullable = false, length = 16)
    @Enumerated(EnumType.STRING)
    var gender: Enums.GenderEnum = Enums.GenderEnum.UNKNOWN,

    @Column(name = "roles", columnDefinition = "jsonb")
    @Convert(converter = StringArrayConverter::class)
    @JdbcTypeCode(SqlTypes.JSON)
    var roles: List<String> = arrayListOf(),

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("createdAt ASC")
    var posts: List<Post> = arrayListOf(),

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("createdAt ASC")
    var comments: List<Comment> = arrayListOf(),

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var emailVerificationToken: EmailVerificationToken? = null,

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var passwordResetToken: PasswordResetToken? = null,

    @Column(name = "blocked_at")
    var blockedAt: LocalDateTime? = null,

    @Column(name = "email_verified_at")
    var emailVerifiedAt: LocalDateTime? = null
) : AbstractBaseEntity() {
    override fun toString(): String = this::class.simpleName + "(id = $id)"
}

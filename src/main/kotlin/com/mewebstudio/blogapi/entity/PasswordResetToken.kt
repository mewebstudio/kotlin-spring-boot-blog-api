package com.mewebstudio.blogapi.entity

import com.mewebstudio.blogapi.util.Constants.PASSWORD_RESET_TOKEN_LENGTH
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.*

@Entity
@Table(
    name = "password_reset_tokens",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id"], name = "uk_password_reset_tokens_user_id"),
        UniqueConstraint(columnNames = ["token"], name = "uk_password_reset_tokens_token")
    ]
)
class PasswordResetToken(
    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_password_reset_tokens_user_id"),
        nullable = false,
        unique = true
    )
    var user: User? = null,

    @Column(name = "token", nullable = false, length = PASSWORD_RESET_TOKEN_LENGTH)
    var token: String? = null,

    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val expirationDate: Date? = null
) : AbstractBaseEntity() {
    override fun toString(): String = this::class.simpleName + "(id = $id, user = ${user})"
}

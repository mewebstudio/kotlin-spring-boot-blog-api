package com.mewebstudio.blogapi.entity

import com.mewebstudio.blogapi.entity.listener.EmailVerificationListener
import com.mewebstudio.blogapi.util.Constants.EMAIL_VERIFICATION_TOKEN_LENGTH
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
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
    name = "email_verification_tokens",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id"], name = "uk_email_verification_tokens_user_id"),
        UniqueConstraint(columnNames = ["token"], name = "uk_email_verification_tokens_token")
    ]
)
@EntityListeners(EmailVerificationListener::class)
class EmailVerificationToken(
    @OneToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
        name = "user_id",
        referencedColumnName = "id",
        foreignKey = ForeignKey(name = "fk_email_verification_tokens_user_id"),
        nullable = false,
        unique = true
    )
    var user: User? = null,

    @Column(name = "token", nullable = false, length = EMAIL_VERIFICATION_TOKEN_LENGTH)
    var token: String? = null,

    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val expirationDate: Date? = null
) : AbstractBaseEntity() {
    override fun toString(): String = this::class.simpleName + "(id = $id, user = ${user})"
}

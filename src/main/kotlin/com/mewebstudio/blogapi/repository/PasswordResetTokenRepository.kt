package com.mewebstudio.blogapi.repository

import com.mewebstudio.blogapi.entity.PasswordResetToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, UUID> {
    fun findByToken(token: String): PasswordResetToken?
}

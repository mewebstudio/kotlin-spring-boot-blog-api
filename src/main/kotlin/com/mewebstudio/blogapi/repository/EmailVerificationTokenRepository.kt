package com.mewebstudio.blogapi.repository

import com.mewebstudio.blogapi.entity.EmailVerificationToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EmailVerificationTokenRepository : JpaRepository<EmailVerificationToken, UUID> {
    fun findByToken(token: String): EmailVerificationToken?
}

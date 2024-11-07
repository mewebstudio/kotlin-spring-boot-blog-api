package com.mewebstudio.blogapi.repository

import com.mewebstudio.blogapi.entity.EmailVerificationToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface EmailVerificationTokenRepository : JpaRepository<EmailVerificationToken, UUID> {
    fun findByUserId(userId: UUID): EmailVerificationToken?

    fun findByToken(token: String): EmailVerificationToken?

    @Modifying
    @Query("DELETE FROM EmailVerificationToken evt WHERE evt.user.id = :userId")
    fun deleteByUserId(@Param("userId") userId: UUID)
}

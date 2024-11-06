package com.mewebstudio.blogapi.repository

import com.mewebstudio.blogapi.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface UserRepository: JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    fun findByEmail(email: String): User?

    fun findByEmailAndIdNot(email: String, id: UUID): User?
}

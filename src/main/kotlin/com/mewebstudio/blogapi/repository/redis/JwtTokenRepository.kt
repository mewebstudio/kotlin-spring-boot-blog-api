package com.mewebstudio.blogapi.repository.redis

import com.mewebstudio.blogapi.entity.JwtToken
import org.springframework.data.keyvalue.repository.KeyValueRepository
import java.util.*

interface JwtTokenRepository : KeyValueRepository<JwtToken, UUID> {
    fun findByTokenOrRefreshToken(token: String, refreshToken: String): JwtToken?

    fun findByUserIdAndRefreshToken(id: UUID, refreshToken: String): JwtToken?
}

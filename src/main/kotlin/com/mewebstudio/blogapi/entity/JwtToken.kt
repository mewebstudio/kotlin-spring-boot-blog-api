package com.mewebstudio.blogapi.entity

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed
import java.util.*
import java.util.concurrent.TimeUnit

@RedisHash(value = "jwt_tokens")
data class JwtToken(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false, updatable = false)
    @Indexed
    var userId: UUID,

    @Column(name = "token", nullable = false, updatable = false)
    @Indexed
    var token: String,

    @Column(name = "refresh_token", nullable = false, updatable = false)
    @Indexed
    var refreshToken: String,

    @Column(name = "remember_me", nullable = false, updatable = false)
    @Indexed
    var rememberMe: Boolean,

    @Column(name = "ip_address", nullable = false, updatable = false)
    @Indexed
    var ipAddress: String,

    @Column(name = "user_agent", nullable = false, updatable = false)
    @Indexed
    var userAgent: String,

    @Column(name = "time_to_live", nullable = false, updatable = false)
    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    var tokenTimeToLive: Long
)

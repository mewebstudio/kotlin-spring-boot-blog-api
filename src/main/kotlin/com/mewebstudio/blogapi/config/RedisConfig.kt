package com.mewebstudio.blogapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration

@Configuration
class RedisConfig {
    @Value("\${spring.data.redis.database}")
    private val database: String? = null

    @Value("\${spring.data.redis.host}")
    private val host: String? = null

    @Value("\${spring.data.redis.port}")
    private val port: String? = null

    @Value("\${spring.data.redis.password}")
    private val password: String? = null

    @Value("\${spring.data.redis.timeout}")
    private val timeout: String? = null

    @Bean
    fun lettuceConnectionFactory(): LettuceConnectionFactory {
        val config = RedisStandaloneConfiguration()
        config.database = database!!.toInt()
        config.hostName = host!!
        config.port = port!!.toInt()
        config.setPassword(password)

        val lettuceClientConfiguration = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofMillis(timeout!!.toLong()))
            .build()

        return LettuceConnectionFactory(config, lettuceClientConfiguration)
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory?): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = redisConnectionFactory

        return template
    }
}

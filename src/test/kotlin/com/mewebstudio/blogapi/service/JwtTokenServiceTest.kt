package com.mewebstudio.blogapi.service

import com.mewebstudio.blogapi.entity.JwtToken
import com.mewebstudio.blogapi.entity.User
import com.mewebstudio.blogapi.exception.NotFoundException
import com.mewebstudio.blogapi.repository.redis.JwtTokenRepository
import org.instancio.Instancio
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.lenient
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@Tag("unit")
@ExtendWith(MockitoExtension::class)
@DisplayName("Unit tests for JwtTokenService")
class JwtTokenServiceTest {
    @InjectMocks
    private lateinit var jwtTokenService: JwtTokenService

    @Mock
    private lateinit var jwtTokenRepository: JwtTokenRepository

    @Mock
    private lateinit var messageSourceService: MessageSourceService

    private val jwtToken = Instancio.create(JwtToken::class.java)

    private val user = Instancio.create(User::class.java)

    private val tokenNotFoundMessage = "Token not found"

    @BeforeEach
    fun setUp() {
        lenient().`when`(messageSourceService.get("not_found_with_param", arrayOf(messageSourceService.get("token"))))
            .thenReturn(tokenNotFoundMessage)
    }

    @Nested
    @DisplayName("Test for findByUserIdAndRefreshToken scenarios")
    inner class FindByUserIdAndRefreshTokenTest {
        @Test
        @DisplayName("Happy path")
        fun `should find by user id and refresh token`() {
            // Given
            `when`(jwtTokenRepository.findByUserIdAndRefreshToken(user.id!!, jwtToken.refreshToken))
                .thenReturn(jwtToken)
            // When
            val result = jwtTokenService.findByUserIdAndRefreshToken(user.id!!, jwtToken.refreshToken)
            // Then
            assertEquals(jwtToken, result)
        }

        @Test
        @DisplayName("Throw NotFoundException")
        fun `should throw NotFoundException`() {
            // Given
            `when`(jwtTokenRepository.findByUserIdAndRefreshToken(user.id!!, jwtToken.refreshToken)).thenReturn(null)
            // When
            val executable = Executable { jwtTokenService.findByUserIdAndRefreshToken(user.id!!, jwtToken.refreshToken) }
            // Then
            val exception = assertThrows(NotFoundException::class.java, executable)
            assertEquals(tokenNotFoundMessage, exception.message)
        }
    }

    @Nested
    @DisplayName("Test for findByTokenOrRefreshToken scenarios")
    inner class FindByTokenOrRefreshTokenTest {
        @Test
        @DisplayName("Happy path")
        fun `should find by user id and refresh token`() {
            // Given
            `when`(jwtTokenRepository.findByTokenOrRefreshToken(anyString(), anyString()))
                .thenReturn(jwtToken)
            // When
            val result = jwtTokenService.findByTokenOrRefreshToken(jwtToken.token)
            // Then
            assertEquals(jwtToken, result)
        }

        @Test
        @DisplayName("Throw NotFoundException")
        fun `should throw NotFoundException`() {
            // Given
            `when`(jwtTokenRepository.findByTokenOrRefreshToken(anyString(), anyString())).thenReturn(null)
            // When
            val executable = Executable { jwtTokenService.findByTokenOrRefreshToken(jwtToken.token) }
            // Then
            val exception = assertThrows(NotFoundException::class.java, executable)
            assertEquals(tokenNotFoundMessage, exception.message)
        }
    }

    @Test
    @DisplayName("Should save JWT token successfully")
    fun `should save JWT token successfully`() {
        // When
        val executable = { jwtTokenService.save(jwtToken) }
        // Then
        assertDoesNotThrow(executable)
        // Verify
        verify(jwtTokenRepository).save(jwtToken)
    }

    @Test
    @DisplayName("Should delete JWT token successfully")
    fun `should delete JWT token successfully`() {
        // When
        jwtTokenService.delete(jwtToken)
        // Then
        verify(jwtTokenRepository).delete(jwtToken)
    }
}

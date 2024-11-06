package com.mewebstudio.blogapi.service

import org.instancio.Instancio
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.MessageSource
import org.springframework.context.NoSuchMessageException
import org.springframework.context.i18n.LocaleContextHolder
import java.util.*

@Tag("unit")
@ExtendWith(MockitoExtension::class)
@DisplayName("Unit tests for MessageSourceService")
class MessageSourceServiceTest {
    @InjectMocks
    private lateinit var messageSourceService: MessageSourceService

    @Mock
    private lateinit var messageSource: MessageSource

    private val locale = Locale.ENGLISH

    private val noSuchMessageException = Instancio.create(NoSuchMessageException::class.java)

    @BeforeEach
    fun setUp() {
        LocaleContextHolder.setLocale(locale)
    }

    @Nested
    @DisplayName("Tests for get method with code and params")
    inner class GetWithCodeAndParams {
        @Test
        @DisplayName("Should return message for valid code and params")
        fun `should return message for valid code and params`() {
            // Given
            val code = "valid.code"
            val params = arrayOf("param1", "param2")
            val expectedMessage = "Hello, param1 and param2!"
            `when`(messageSource.getMessage(code, params, locale)).thenReturn(expectedMessage)
            // When
            val message = messageSourceService.get(code, params)
            // Then
            assertEquals(expectedMessage, message)
        }

        @Test
        @DisplayName("Should return code if message not found")
        fun `should return code if message not found`() {
            // Given
            val code = "invalid.code"
            val params = arrayOf("param1", "param2")
            `when`(messageSource.getMessage(code, params, locale)).thenThrow(noSuchMessageException)
            // When
            val message = messageSourceService.get(code, params)
            // Then
            assertEquals(code, message)
        }
    }

    @Nested
    @DisplayName("Tests for get method with code only")
    inner class GetWithCodeOnly {
        @Test
        @DisplayName("Should return message for valid code")
        fun `should return message for valid code`() {
            // Given
            val code = "valid.code"
            val expectedMessage = "Hello, World!"
            `when`(messageSource.getMessage(code, null, locale)).thenReturn(expectedMessage)
            // When
            val message = messageSourceService.get(code, locale)
            // Then
            assertEquals(expectedMessage, message)
        }

        @Test
        @DisplayName("Should return code if message not found with code only")
        fun `should return code if message not found with code only`() {
            // Given
            val code = "invalid.code"
            `when`(messageSource.getMessage(code, null, locale)).thenThrow(noSuchMessageException)
            // When
            val message = messageSourceService.get(code, locale)
            // Then
            assertEquals(code, message)
        }
    }

    @Nested
    @DisplayName("Tests for get method without locale")
    inner class GetWithoutLocale {
        @Test
        @DisplayName("Should return message for valid code without locale")
        fun `should return message for valid code without locale`() {
            // Given
            val code = "valid.code"
            val expectedMessage = "Hello, World!"
            `when`(messageSource.getMessage(code, null, Locale.ENGLISH)).thenReturn(expectedMessage)
            // When
            val message = messageSourceService.get(code)
            // Then
            assertEquals(expectedMessage, message)
        }

        @Test
        @DisplayName("Should return code if message not found without locale")
        fun `should return code if message not found without locale`() {
            // Given
            val code = "invalid.code"
            `when`(messageSource.getMessage(code, null, Locale.ENGLISH)).thenThrow(noSuchMessageException)
            // When
            val message = messageSourceService.get(code)
            // Then
            assertEquals(code, message)
        }
    }
}

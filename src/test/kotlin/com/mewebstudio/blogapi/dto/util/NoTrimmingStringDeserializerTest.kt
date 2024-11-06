package com.mewebstudio.blogapi.dto.util

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@Tag("unit")
@ExtendWith(MockitoExtension::class)
@DisplayName("Unit tests for NoTrimmingStringDeserializer")
class NoTrimmingStringDeserializerTest {
    private val objectMapper = ObjectMapper()

    data class TestStringWrapper @JsonCreator constructor(
        @JsonProperty("noTrimmedString")
        @field:JsonDeserialize(using = NoTrimmingStringDeserializer::class)
        val noTrimmedString: String
    )

    @Test
    @DisplayName("Should no trim whitespace from deserialized string")
    fun `should trim whitespace from deserialized string`() {
        // Given
        val json = """{ "noTrimmedString": "   Hello World   " }"""
        // When
        val result = objectMapper.readValue(json, TestStringWrapper::class.java)
        // Then
        assertEquals("   Hello World   ", result.noTrimmedString)
    }
}

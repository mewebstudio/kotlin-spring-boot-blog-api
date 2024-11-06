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
@DisplayName("Unit tests for TrimmingStringDeserializer")
class TrimmingStringDeserializerTest {
    private val objectMapper = ObjectMapper()

    data class TestStringWrapper @JsonCreator constructor(
        @JsonProperty("trimmedString")
        @field:JsonDeserialize(using = TrimmingStringDeserializer::class)
        val trimmedString: String
    )

    @Test
    @DisplayName("Should trim whitespace from deserialized string")
    fun `should trim whitespace from deserialized string`() {
        val json = """{ "trimmedString": "   Hello World   " }"""

        // Deserialize the JSON to our data class
        val result = objectMapper.readValue(json, TestStringWrapper::class.java)

        // Assert that the string has been trimmed
        assertEquals("Hello World", result.trimmedString)
    }
}

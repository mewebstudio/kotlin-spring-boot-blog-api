package com.mewebstudio.blogapi.entity.converter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@Tag("unit")
@ExtendWith(MockitoExtension::class)
@DisplayName("Unit tests for StringArrayConverter")
class StringArrayConverterTest {
    @InjectMocks
    private val converter: StringArrayConverter = StringArrayConverter()

    @Test
    @DisplayName("Should convert list to JSON string for database column")
    fun `should convert list to JSON string for database column`() {
        // Given
        val list = listOf("apple", "banana", "cherry")
        val expectedJson = """["apple","banana","cherry"]"""
        // When
        val result = converter.convertToDatabaseColumn(list)
        // Then
        assertEquals(expectedJson, result)
    }

    @Test
    @DisplayName("Should convert JSON string to list for entity attribute")
    fun `should convert JSON string to list for entity attribute`() {
        // Given
        val json = """["apple","banana","cherry"]"""
        val expectedList = listOf("apple", "banana", "cherry")
        // When
        val result = converter.convertToEntityAttribute(json)
        // Then
        assertEquals(expectedList, result)
    }

    @Test
    @DisplayName("Should return empty list when database column is null")
    fun `should return empty list when database column is null`() {
        // When
        val result = converter.convertToEntityAttribute(null)
        // Then
        assertEquals(emptyList<String>(), result)
    }

    @Test
    @DisplayName("Should return null when entity attribute is null")
    fun `should return null when entity attribute is null`() {
        // When
        val result = converter.convertToDatabaseColumn(null)
        // Then
        assertEquals(null, result)
    }
}

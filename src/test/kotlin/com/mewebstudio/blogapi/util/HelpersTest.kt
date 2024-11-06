package com.mewebstudio.blogapi.util

import com.mewebstudio.blogapi.SampleEnum
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@Tag("unit")
@ExtendWith(MockitoExtension::class)
@DisplayName("Unit tests for MessageSourceService")
class HelpersTest {
    @Nested
    @DisplayName("Tests for searchEnum method")
    inner class SearchEnumTests {
        @Test
        @DisplayName("Should return enum constant for valid name")
        fun `should return enum constant for valid name`() {
            val result = Helpers.searchEnum(SampleEnum::class.java, "OPTION_ONE")
            assertEquals(SampleEnum.OPTION_ONE, result)
        }

        @Test
        @DisplayName("Should return enum constant for valid name with case insensitive")
        fun `should return enum constant for valid name with case insensitive`() {
            val result = Helpers.searchEnum(SampleEnum::class.java, "option_two")
            assertEquals(SampleEnum.OPTION_TWO, result)
        }

        @Test
        @DisplayName("Should return null for invalid name without throwing")
        fun `should return null for invalid name without throwing`() {
            val result = Helpers.searchEnum(SampleEnum::class.java, "INVALID_OPTION")
            assertNull(result)
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for invalid name with throwable true")
        fun `should throw IllegalArgumentException for invalid name with throwable true`() {
            assertThrows<IllegalArgumentException> {
                Helpers.searchEnum(SampleEnum::class.java, "INVALID_OPTION", true)
            }
        }
    }

    @Nested
    @DisplayName("Tests for parseJsonContent method")
    inner class ParseJsonContentTests {
        @Test
        @DisplayName("Should parse JSON array string into a list")
        fun `should parse JSON array string into a list`() {
            val jsonArray = """["item1", "item2", "item3"]"""
            val result = Helpers.parseJsonContent(jsonArray)
            assertTrue(result is List<*>)
            assertEquals(listOf("item1", "item2", "item3"), result)
        }

        @Test
        @DisplayName("Should parse JSON object string into a map")
        fun `should parse JSON object string into a map`() {
            val jsonObject = """{"key1": "value1", "key2": "value2"}"""
            val result = Helpers.parseJsonContent(jsonObject)
            assertTrue(result is Map<*, *>)
            assertEquals(mapOf("key1" to "value1", "key2" to "value2"), result)
        }

        @Test
        @DisplayName("Should return null for null or blank input")
        fun `should return null for null or blank input`() {
            assertNull(Helpers.parseJsonContent(null))
            assertNull(Helpers.parseJsonContent(""))
            assertNull(Helpers.parseJsonContent(" "))
        }
    }
}

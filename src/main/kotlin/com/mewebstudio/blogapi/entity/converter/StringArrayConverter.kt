package com.mewebstudio.blogapi.entity.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class StringArrayConverter : AttributeConverter<List<String>, String> {
    private val objectMapper = jacksonObjectMapper()

    /**
     * Convert List<String> to JSON string
     */
    override fun convertToDatabaseColumn(attribute: List<String>?): String? =
        attribute?.let { objectMapper.writeValueAsString(it) }

    /**
     * Convert JSON string to List<String>
     */
    override fun convertToEntityAttribute(dbData: String?): List<String> = dbData?.let {
        objectMapper.readValue(it, object : TypeReference<List<String>>() {})
    } ?: emptyList()
}

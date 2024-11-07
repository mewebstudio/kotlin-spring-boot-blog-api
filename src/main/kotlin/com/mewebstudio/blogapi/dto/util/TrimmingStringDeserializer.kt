package com.mewebstudio.blogapi.dto.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

/**
 * Trimming string deserializer.
 *
 * Using this class to deserialize string with trimming.
 * Example usage: @JsonDeserialize(using = TrimmingStringDeserializer::class)
 */
class TrimmingStringDeserializer : JsonDeserializer<String>() {
    /**
     * Deserialize string with trimming.
     */
    override fun deserialize(parser: JsonParser, deserializationContext: DeserializationContext): String =
        parser.valueAsString.trim()
}

package com.mewebstudio.blogapi.dto.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

/**
 * No trimming string deserializer.
 *
 * Using this class to deserialize string without trimming.
 * Example usage: @JsonDeserialize(using = NoTrimmingStringDeserializer::class)
 */
class NoTrimmingStringDeserializer : JsonDeserializer<String>() {
    /**
     * Deserialize string without trimming.
     */
    override fun deserialize(parser: JsonParser, deserializationContext: DeserializationContext): String =
        parser.valueAsString
}

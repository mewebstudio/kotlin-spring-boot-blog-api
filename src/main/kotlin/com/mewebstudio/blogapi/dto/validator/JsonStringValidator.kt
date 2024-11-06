package com.mewebstudio.blogapi.dto.validator

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.mewebstudio.blogapi.dto.annotation.JsonString
import com.mewebstudio.blogapi.util.logger
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.slf4j.Logger

class JsonStringValidator(private val objectMapper: ObjectMapper) : ConstraintValidator<JsonString, String> {
    private val log: Logger by logger()

    override fun isValid(content: String?, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        if (content.isNullOrEmpty()) {
            return true
        }

        return try {
            if (content.startsWith("[")) {
                objectMapper.readValue(content, List::class.java)
                true
            } else {
                objectMapper.readValue(content, Map::class.java)
                true
            }
        } catch (e: JsonProcessingException) {
            log.error("Invalid JSON content provided: $content", e)
            false
        }
    }
}

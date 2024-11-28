package com.mewebstudio.blogapi.dto.validator

import com.mewebstudio.blogapi.dto.annotation.CheckUUID
import com.mewebstudio.blogapi.util.logger
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.slf4j.Logger
import java.util.*

class CheckUUIDValidator : ConstraintValidator<CheckUUID, Any?> {
    private val log: Logger by logger()

    override fun isValid(value: Any?, context: ConstraintValidatorContext): Boolean = run {
        if (value == null) {
            return true
        }

        when (value) {
            is String -> checkUUID(value)
            is List<*> -> value.all { it is String && checkUUID(it) }
            else -> false
        }
    }

    private fun checkUUID(value: String): Boolean {
        return try {
            UUID.fromString(value)
            true
        } catch (e: IllegalArgumentException) {
            log.error("UUID has wrong format: $e")
            false
        }
    }
}

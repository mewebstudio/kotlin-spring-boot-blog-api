package com.mewebstudio.blogapi.dto.validator

import com.mewebstudio.blogapi.dto.annotation.FieldMatch
import com.mewebstudio.blogapi.util.logger
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.slf4j.Logger
import kotlin.reflect.full.memberProperties

class FieldMatchValidator : ConstraintValidator<FieldMatch, Any> {
    private var firstField: String? = null

    private var secondField: String? = null

    private var message: String? = null

    private val log: Logger by logger()

    override fun initialize(constraintAnnotation: FieldMatch) {
        firstField = constraintAnnotation.first
        secondField = constraintAnnotation.second
        message = constraintAnnotation.message
    }

    override fun isValid(obj: Any, context: ConstraintValidatorContext): Boolean = run {
        var valid = true
        try {
            val firstProperty = obj::class.memberProperties.find { it.name == firstField }
            val secondProperty = obj::class.memberProperties.find { it.name == secondField }

            val firstValue = firstProperty?.getter?.call(obj)
            val secondValue = secondProperty?.getter?.call(obj)

            valid = (firstValue == null && secondValue == null) || (firstValue != null && firstValue == secondValue)
        } catch (e: Exception) {
            log.warn("Error while validating fields {} - {}", this::class.java.name, e.message)
        }

        if (!valid) {
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(firstField)
                .addConstraintViolation()
                .disableDefaultConstraintViolation()
        }

        valid
    }
}

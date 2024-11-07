package com.mewebstudio.blogapi.dto.validator

import com.mewebstudio.blogapi.dto.annotation.ValueOfEnum
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValueOfEnumValidator : ConstraintValidator<ValueOfEnum, Any?> {
    private lateinit var acceptedValues: List<String>

    override fun initialize(annotation: ValueOfEnum) {
        acceptedValues = annotation.enumClass.java.enumConstants.map { it.name }
    }

    override fun isValid(value: Any?, context: ConstraintValidatorContext): Boolean = run {
        if (value == null) {
            return true
        }

        when (value) {
            is CharSequence -> checkValue(value)
            is List<*> -> value.all { it is CharSequence && checkValue(it) }
            else -> false
        }
    }

    private fun checkValue(value: CharSequence): Boolean =
        acceptedValues.contains(value.toString().lowercase()) || acceptedValues.contains(value.toString().uppercase())
}

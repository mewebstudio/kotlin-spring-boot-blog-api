package com.mewebstudio.blogapi.dto.validator

import com.mewebstudio.blogapi.dto.annotation.MinListSize
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class MinListSizeValidator : ConstraintValidator<MinListSize, List<String>> {
    private var min: Long = -1

    override fun initialize(constraintAnnotation: MinListSize) {
        min = constraintAnnotation.min
    }

    override fun isValid(values: List<String>?, context: ConstraintValidatorContext): Boolean {
        if (values == null) {
            return true
        }

        return values.size >= min
    }
}

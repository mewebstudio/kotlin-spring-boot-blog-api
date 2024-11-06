package com.mewebstudio.blogapi.dto.validator

import com.mewebstudio.blogapi.dto.annotation.DateFormat
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class DateFormatValidator : ConstraintValidator<DateFormat, String> {
    private lateinit var format: String

    override fun initialize(constraintAnnotation: DateFormat) {
        format = constraintAnnotation.format
    }

    override fun isValid(date: String?, context: ConstraintValidatorContext): Boolean {
        if (date.isNullOrEmpty()) {
            return true
        }

        return try {
            when (format) {
                "yyyy-MM-dd" -> {
                    val dateFormatter = DateTimeFormatter.ofPattern(format)
                    LocalDate.parse(date, dateFormatter)
                }

                "yyyy-MM-dd'T'HH:mm:ss" -> {
                    val dateTimeFormatter = DateTimeFormatter.ofPattern(format)
                    LocalDateTime.parse(date, dateTimeFormatter)
                }
            }
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }
}

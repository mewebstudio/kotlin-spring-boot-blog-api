package com.mewebstudio.blogapi.dto.annotation

import com.mewebstudio.blogapi.dto.validator.DateFormatValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import org.hibernate.type.descriptor.java.JdbcDateJavaType.DATE_FORMAT
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [DateFormatValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class DateFormat(
    val message: String = "Invalid date format",
    val format: String = DATE_FORMAT,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

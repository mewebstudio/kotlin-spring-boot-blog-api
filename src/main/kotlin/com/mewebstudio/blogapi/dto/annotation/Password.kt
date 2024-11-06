package com.mewebstudio.blogapi.dto.annotation

import com.mewebstudio.blogapi.dto.validator.PasswordConstraintsValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [PasswordConstraintsValidator::class])
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Password(
    val message: String = "Invalid password.",
    val detailedMessage: Boolean = false,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

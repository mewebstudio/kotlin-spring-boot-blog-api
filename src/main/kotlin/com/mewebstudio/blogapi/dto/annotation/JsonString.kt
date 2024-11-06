package com.mewebstudio.blogapi.dto.annotation

import com.mewebstudio.blogapi.dto.validator.JsonStringValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [JsonStringValidator::class])
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.ANNOTATION_CLASS
)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class JsonString(
    val message: String = "Invalid JSON string",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

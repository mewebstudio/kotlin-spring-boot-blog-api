package com.mewebstudio.blogapi.dto.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.ANNOTATION_CLASS
)
@Constraint(validatedBy = [])
@MustBeDocumented
annotation class CheckUUID(
    val message: String = "UUID has wrong format",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

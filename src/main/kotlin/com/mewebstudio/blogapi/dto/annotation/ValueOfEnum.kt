package com.mewebstudio.blogapi.dto.annotation

import com.mewebstudio.blogapi.dto.validator.ValueOfEnumValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [ValueOfEnumValidator::class])
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.TYPE
)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValueOfEnum(
    val enumClass: KClass<out Enum<*>>,
    val message: String = "Invalid value!",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

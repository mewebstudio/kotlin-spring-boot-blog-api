package com.mewebstudio.blogapi.dto.annotation

import com.mewebstudio.blogapi.dto.validator.FieldMatchValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [FieldMatchValidator::class])
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class FieldMatch(
    val first: String,
    val second: String,
    val message: String = "The fields must match",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
) {
    @Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    @MustBeDocumented
    annotation class List(vararg val value: FieldMatch)
}

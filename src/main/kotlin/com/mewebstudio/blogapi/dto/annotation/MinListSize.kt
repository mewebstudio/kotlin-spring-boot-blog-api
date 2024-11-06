package com.mewebstudio.blogapi.dto.annotation

import com.mewebstudio.blogapi.dto.validator.MinListSizeValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [MinListSizeValidator::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class MinListSize(
    val message: String = "The list must be a minimum size",
    val min: Long = -1L,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

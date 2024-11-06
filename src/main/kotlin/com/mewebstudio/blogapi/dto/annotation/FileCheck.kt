package com.mewebstudio.blogapi.dto.annotation

import com.mewebstudio.blogapi.dto.validator.FileCheckValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import org.springframework.http.MediaType
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [FileCheckValidator::class])
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER
)
@Retention(
    AnnotationRetention.RUNTIME
)
annotation class FileCheck(
    val message: String = "Invalid file type",
    val mimeTypes: Array<String> = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE],
    val maxSizeMessage: String = "File size is too large",
    val maxSize: Long = -1L,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

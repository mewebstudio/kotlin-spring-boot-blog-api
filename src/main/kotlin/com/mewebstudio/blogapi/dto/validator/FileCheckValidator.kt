package com.mewebstudio.blogapi.dto.validator

import com.mewebstudio.blogapi.dto.annotation.FileCheck
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile

class FileCheckValidator : ConstraintValidator<FileCheck, MultipartFile> {
    private lateinit var maxSizeMessage: String
    private lateinit var mimeTypes: Array<String>
    private var maxSize: Long = -1

    override fun initialize(constraintAnnotation: FileCheck) {
        maxSizeMessage = constraintAnnotation.maxSizeMessage
        mimeTypes = constraintAnnotation.mimeTypes
        maxSize = constraintAnnotation.maxSize
    }

    override fun isValid(multipartFile: MultipartFile?, context: ConstraintValidatorContext): Boolean = run {
        if (multipartFile == null) {
            return true
        }

        val contentType = multipartFile.contentType ?: return false

        if (maxSize > -1 && multipartFile.size > maxSize) {
            context.buildConstraintViolationWithTemplate(maxSizeMessage)
                .addConstraintViolation()
                .disableDefaultConstraintViolation()
            return false
        }

        mimeTypes.contains(contentType)
    }
}

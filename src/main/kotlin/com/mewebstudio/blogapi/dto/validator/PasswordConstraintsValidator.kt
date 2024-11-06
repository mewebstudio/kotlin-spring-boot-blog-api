package com.mewebstudio.blogapi.dto.validator

import com.mewebstudio.blogapi.dto.annotation.Password
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.LengthRule
import org.passay.PasswordData
import org.passay.PasswordValidator
import org.passay.RuleResult
import org.passay.WhitespaceRule

class PasswordConstraintsValidator : ConstraintValidator<Password, String?> {
    private var isDetailedMessage = false

    companion object {
        private const val MIN_LENGTH = 6
        private const val MAX_LENGTH = 32
    }

    override fun initialize(constraintAnnotation: Password) {
        super.initialize(constraintAnnotation)
        isDetailedMessage = constraintAnnotation.detailedMessage
    }

    override fun isValid(password: String?, context: ConstraintValidatorContext): Boolean {
        if (password == null) {
            return true
        }

        val validator: PasswordValidator = PasswordValidator(
            listOf( // Length rule. Min 6 max 32 characters
                LengthRule(MIN_LENGTH, MAX_LENGTH),  // At least one upper case letter
                CharacterRule(EnglishCharacterData.UpperCase, 1),  // At least one lower case letter
                CharacterRule(EnglishCharacterData.LowerCase, 1),  // At least one number
                CharacterRule(EnglishCharacterData.Digit, 1),  // At least one special characters
                CharacterRule(EnglishCharacterData.Special, 1),  // No whitespace
                WhitespaceRule()
            )
        )

        val result: RuleResult = validator.validate(PasswordData(password))
        if (result.isValid) {
            return true
        }

        if (isDetailedMessage) {
            val messages: List<String> = validator.getMessages(result)
            val messageTemplate = java.lang.String.join("\n", messages)
            context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation()
        }

        return false
    }
}

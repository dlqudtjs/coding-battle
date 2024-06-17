package com.dlqudtjs.codingbattle.common.validator.programmingLanguage;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguageManager;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProgrammingLanguageValidator implements ConstraintValidator<ValidProgrammingLanguage, String> {

    @Override
    public void initialize(ValidProgrammingLanguage constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ProgrammingLanguageManager.isSupportedLanguage(value);
    }
}

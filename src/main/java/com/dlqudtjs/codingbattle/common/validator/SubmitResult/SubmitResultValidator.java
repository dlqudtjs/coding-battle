package com.dlqudtjs.codingbattle.common.validator.SubmitResult;

import com.dlqudtjs.codingbattle.common.constant.SubmitResultManager;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SubmitResultValidator implements ConstraintValidator<ValidSubmitResult, String> {

    @Override
    public void initialize(ValidSubmitResult constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return SubmitResultManager.isValid(value);
    }
}

package com.dlqudtjs.codingbattle.common.validator.problemLevel;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelManager;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProblemLevelValidator implements ConstraintValidator<ValidProblemLevel, String> {

    @Override
    public void initialize(ValidProblemLevel constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ProblemLevelManager.isSupportedLevel(value);
    }
}

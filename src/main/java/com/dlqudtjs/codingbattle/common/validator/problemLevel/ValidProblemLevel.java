package com.dlqudtjs.codingbattle.common.validator.problemLevel;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ProblemLevelValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidProblemLevel {
    String message() default "Invalid problem level";

    Class<?>[] groups() default {}; // groups 속성 추가

    Class<? extends Payload>[] payload() default {};
}

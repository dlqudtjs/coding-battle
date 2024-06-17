package com.dlqudtjs.codingbattle.common.validator.submitResult;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SubmitResultValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSubmitResult {
    String message() default "Invalid submit result";

    Class<?>[] groups() default {}; // groups 속성 추가

    Class<? extends Payload>[] payload() default {};
}

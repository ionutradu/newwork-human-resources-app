package com.newwork.human_resources_app.validations.absence;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = TimeframeValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface ValidTimeframe {

    String message() default "End date must be the same as or after the start date.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package com.colphacy.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBlankIfPresentValidator.class)
public @interface NotBlankIfPresent {
    String message() default "Field must not be blank.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
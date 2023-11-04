package com.colphacy.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotBlankIfPresentValidator implements ConstraintValidator<NotBlankIfPresent, String> {
    @Override
    public void initialize(NotBlankIfPresent constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            return !value.trim().isEmpty();
        }
        return true;
    }
}

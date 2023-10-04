package com.colphacy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFieldsException extends RuntimeException {
    private final List<FieldError> fieldErrors;

    public InvalidFieldsException(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public InvalidFieldsException addFieldError(FieldError fieldError) {
        this.fieldErrors.add(fieldError);
        return this;
    }

    public InvalidFieldsException addFieldError(String field, String message) {
        this.fieldErrors.add(new FieldError(field, message));
        return this;
    }

    public static InvalidFieldsException fromFieldError(String field, String message) {
        return new InvalidFieldsException(List.of(new FieldError(field, message)));
    }

    public static class FieldError {
        private final String field;
        private final String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}

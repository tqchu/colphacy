package com.colphacy.exception;

public class ImageUploadingException extends RuntimeException {
    public ImageUploadingException() {
    }

    public ImageUploadingException(String message) {
        super(message);
    }
}

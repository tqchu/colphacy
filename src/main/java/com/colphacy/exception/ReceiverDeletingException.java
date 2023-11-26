package com.colphacy.exception;

public class ReceiverDeletingException extends RuntimeException{
    public ReceiverDeletingException() {
    }

    public ReceiverDeletingException(String message) { super(message); }
}

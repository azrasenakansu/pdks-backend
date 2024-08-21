package com.proven.pdks.exceptionHandling;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

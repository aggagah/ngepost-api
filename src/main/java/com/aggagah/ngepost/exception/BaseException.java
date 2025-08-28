package com.aggagah.ngepost.exception;

public class BaseException extends RuntimeException {
    private final String message;

    public BaseException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

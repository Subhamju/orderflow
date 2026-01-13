package com.orderflow.exception;

public class InvalidOrderException extends RuntimeException{
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    private final ErrorCode errorCode;
    public InvalidOrderException(ErrorCode errorCode,String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

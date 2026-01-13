package com.orderflow.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final String errorCode;
    private final String message;
    private final LocalDateTime timeStamp;
    private final String path;

    public ErrorResponse(String errorCode, String message, LocalDateTime timeStamp, String path) {
        this.errorCode = errorCode;
        this.message = message;
        this.timeStamp = timeStamp;
        this.path = path;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getPath() {
        return path;
    }
}

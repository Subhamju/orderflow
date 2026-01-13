package com.orderflow.exception;

import com.orderflow.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrder(InvalidOrderException ex, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getErrorCode().name(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
                );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,HttpServletRequest request){
      ErrorResponse error = new ErrorResponse(ErrorCode.INTERNAL_ERROR.name(),
              "",
              LocalDateTime.now(),
              request.getRequestURI());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(error);
    }
}

package com.orderflow.exception;

import com.orderflow.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
        @ExceptionHandler(InvalidOrderException.class)
        public ResponseEntity<ErrorResponse> handleInvalidOrder(InvalidOrderException ex, HttpServletRequest request) {
                ErrorResponse error = new ErrorResponse(ex.getErrorCode().name(),
                                ex.getMessage(),
                                LocalDateTime.now(),
                                request.getRequestURI());
                return ResponseEntity.badRequest().body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handle(Exception ex, HttpServletRequest request) {

                log.error("Unhandled exception for path {}", request.getRequestURI(), ex);

                ErrorResponse error = new ErrorResponse(
                                "INTERNAL_ERROR",
                                ex.getMessage(),
                                LocalDateTime.now(),
                                request.getRequestURI());

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(error);
        }
}

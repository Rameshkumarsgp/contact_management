package com.contacthub.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ContactNotFoundException.class)
    public ResponseEntity<String> handleContactNotFound(ContactNotFoundException ex,
                                                        HttpServletRequest request) {
        log.error("Contact not found [{} {}]: {}", request.getMethod(), request.getRequestURI(),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.error("Validation failed [{} {}]: {}", request.getMethod(), request.getRequestURI(),
                errors);
        // Returns JSON like: { "email": "Email must be a valid email address", "firstName": "First name is required" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex,
                                                            HttpServletRequest request) {
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Validation failed");
        log.error("Constraint violation [{} {}]: {}", request.getMethod(), request.getRequestURI(),
                message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    // Handles missing required query parameters (e.g. ?email not provided) → returns HTTP 400 Bad Request
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParam(MissingServletRequestParameterException ex,
                                                     HttpServletRequest request) {
        log.error("Missing parameter [{} {}]: {}", request.getMethod(), request.getRequestURI(),
                ex.getParameterName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Required parameter is missing: " + ex.getParameterName());
    }

    // Handles IllegalArgumentException (e.g. duplicate email) → returns HTTP 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex,
                                                        HttpServletRequest request) {
        log.error("Illegal argument [{} {}]: {}", request.getMethod(), request.getRequestURI(),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // Catch-all: handles any unexpected exception → returns HTTP 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericError(Exception ex,
                                                     HttpServletRequest request) {
        log.error("Unexpected error [{} {}]", request.getMethod(), request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred");
    }

    //
}

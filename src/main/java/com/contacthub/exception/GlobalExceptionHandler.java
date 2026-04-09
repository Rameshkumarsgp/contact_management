package com.contacthub.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// @RestControllerAdvice: this class intercepts exceptions thrown by ANY controller in the application
// Instead of each controller handling its own errors, all error handling is centralised here
// This follows the Single Responsibility Principle — controllers just handle happy-path logic
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles ContactNotFoundException → returns HTTP 404 Not Found
    @ExceptionHandler(ContactNotFoundException.class)
    public ResponseEntity<String> handleContactNotFound(ContactNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Handles validation failures (e.g. @NotBlank, @Email) → returns HTTP 400 Bad Request
    // MethodArgumentNotValidException is thrown by Spring when @Valid fails on a @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // Each field that failed validation has a FieldError — we collect them all into a map
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        // Returns JSON like: { "email": "Email must be a valid email address", "firstName": "First name is required" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Handles @Validated + @Email/@NotBlank on @RequestParam → returns HTTP 400 Bad Request
    // This is different from MethodArgumentNotValidException which handles @Valid on @RequestBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
            .map(violation -> violation.getMessage())
            .findFirst()
            .orElse("Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    // Handles missing required query parameters (e.g. ?emailId not provided) → returns HTTP 400 Bad Request
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Required parameter is missing: " + ex.getParameterName());
    }

    // Handles IllegalArgumentException (e.g. duplicate email) → returns HTTP 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // Catch-all: handles any unexpected exception → returns HTTP 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }
}

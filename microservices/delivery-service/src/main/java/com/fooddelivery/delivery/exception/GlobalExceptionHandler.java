package com.fooddelivery.delivery.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for Delivery Service
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DeliveryPartnerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDeliveryPartnerNotFoundException(DeliveryPartnerNotFoundException ex) {
        log.error("Delivery partner not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Delivery Partner Not Found")
            .message(ex.getMessage())
            .path("/delivery-partners")
            .build();
            
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DeliveryPartnerAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleDeliveryPartnerAlreadyExistsException(DeliveryPartnerAlreadyExistsException ex) {
        log.error("Delivery partner already exists: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .error("Delivery Partner Already Exists")
            .message(ex.getMessage())
            .path("/delivery-partners")
            .build();
            
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(DeliveryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDeliveryNotFoundException(DeliveryNotFoundException ex) {
        log.error("Delivery not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Delivery Not Found")
            .message(ex.getMessage())
            .path("/deliveries")
            .build();
            
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DeliveryAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleDeliveryAlreadyExistsException(DeliveryAlreadyExistsException ex) {
        log.error("Delivery already exists: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .error("Delivery Already Exists")
            .message(ex.getMessage())
            .path("/deliveries")
            .build();
            
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        log.error("Unauthorized access: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.FORBIDDEN.value())
            .error("Unauthorized Access")
            .message(ex.getMessage())
            .build();
            
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Invalid input parameters")
            .validationErrors(validationErrors)
            .build();
            
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        log.error("Illegal state: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .error("Invalid State")
            .message(ex.getMessage())
            .build();
            
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Invalid Argument")
            .message(ex.getMessage())
            .build();
            
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .build();
            
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Error response DTO
     */
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> validationErrors;

        // Builder pattern
        public static ErrorResponseBuilder builder() {
            return new ErrorResponseBuilder();
        }

        public static class ErrorResponseBuilder {
            private LocalDateTime timestamp;
            private int status;
            private String error;
            private String message;
            private String path;
            private Map<String, String> validationErrors;

            public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public ErrorResponseBuilder status(int status) {
                this.status = status;
                return this;
            }

            public ErrorResponseBuilder error(String error) {
                this.error = error;
                return this;
            }

            public ErrorResponseBuilder message(String message) {
                this.message = message;
                return this;
            }

            public ErrorResponseBuilder path(String path) {
                this.path = path;
                return this;
            }

            public ErrorResponseBuilder validationErrors(Map<String, String> validationErrors) {
                this.validationErrors = validationErrors;
                return this;
            }

            public ErrorResponse build() {
                ErrorResponse response = new ErrorResponse();
                response.timestamp = this.timestamp;
                response.status = this.status;
                response.error = this.error;
                response.message = this.message;
                response.path = this.path;
                response.validationErrors = this.validationErrors;
                return response;
            }
        }

        // Getters and setters
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public Map<String, String> getValidationErrors() { return validationErrors; }
        public void setValidationErrors(Map<String, String> validationErrors) { this.validationErrors = validationErrors; }
    }
}
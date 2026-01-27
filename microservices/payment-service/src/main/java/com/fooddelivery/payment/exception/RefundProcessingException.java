package com.fooddelivery.payment.exception;

/**
 * Exception thrown when refund processing fails
 */
public class RefundProcessingException extends RuntimeException {

    public RefundProcessingException(String message) {
        super(message);
    }

    public RefundProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
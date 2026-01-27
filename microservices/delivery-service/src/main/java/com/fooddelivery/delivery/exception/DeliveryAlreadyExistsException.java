package com.fooddelivery.delivery.exception;

/**
 * Exception thrown when trying to create a delivery that already exists
 */
public class DeliveryAlreadyExistsException extends RuntimeException {

    public DeliveryAlreadyExistsException(String message) {
        super(message);
    }

    public DeliveryAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
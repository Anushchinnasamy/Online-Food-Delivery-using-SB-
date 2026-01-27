package com.fooddelivery.delivery.exception;

/**
 * Exception thrown when trying to create a delivery partner that already exists
 */
public class DeliveryPartnerAlreadyExistsException extends RuntimeException {

    public DeliveryPartnerAlreadyExistsException(String message) {
        super(message);
    }

    public DeliveryPartnerAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
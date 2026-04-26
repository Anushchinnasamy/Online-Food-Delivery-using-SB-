package com.fooddelivery.delivery.exception;

/**
 * Exception thrown when a delivery partner is not found
 */
public class DeliveryPartnerNotFoundException extends RuntimeException {

    public DeliveryPartnerNotFoundException(String message) {
        super(message);
    }

    public DeliveryPartnerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
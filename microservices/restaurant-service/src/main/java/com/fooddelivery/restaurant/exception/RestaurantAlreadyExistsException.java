package com.fooddelivery.restaurant.exception;

/**
 * Exception thrown when trying to create a restaurant that already exists
 */
public class RestaurantAlreadyExistsException extends RuntimeException {
    
    public RestaurantAlreadyExistsException(String message) {
        super(message);
    }
    
    public RestaurantAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
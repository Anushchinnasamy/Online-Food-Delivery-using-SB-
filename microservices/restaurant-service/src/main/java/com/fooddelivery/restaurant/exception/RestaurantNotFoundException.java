package com.fooddelivery.restaurant.exception;

/**
 * Exception thrown when a restaurant is not found
 */
public class RestaurantNotFoundException extends RuntimeException {
    
    public RestaurantNotFoundException(String message) {
        super(message);
    }
    
    public RestaurantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
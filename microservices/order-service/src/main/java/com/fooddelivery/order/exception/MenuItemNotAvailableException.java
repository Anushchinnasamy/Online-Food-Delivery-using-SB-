package com.fooddelivery.order.exception;

/**
 * Exception thrown when trying to order unavailable menu items
 */
public class MenuItemNotAvailableException extends RuntimeException {
    
    public MenuItemNotAvailableException(String message) {
        super(message);
    }
    
    public MenuItemNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
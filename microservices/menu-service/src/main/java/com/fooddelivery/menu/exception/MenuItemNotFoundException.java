package com.fooddelivery.menu.exception;

/**
 * Exception thrown when a menu item is not found
 */
public class MenuItemNotFoundException extends RuntimeException {
    
    public MenuItemNotFoundException(String message) {
        super(message);
    }
    
    public MenuItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
package com.fooddelivery.menu.exception;

/**
 * Exception thrown when trying to create a menu item that already exists
 */
public class MenuItemAlreadyExistsException extends RuntimeException {
    
    public MenuItemAlreadyExistsException(String message) {
        super(message);
    }
    
    public MenuItemAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
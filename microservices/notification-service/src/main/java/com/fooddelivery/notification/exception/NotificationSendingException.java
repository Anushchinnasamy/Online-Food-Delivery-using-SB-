package com.fooddelivery.notification.exception;

/**
 * Exception thrown when notification sending fails
 */
public class NotificationSendingException extends RuntimeException {

    public NotificationSendingException(String message) {
        super(message);
    }

    public NotificationSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
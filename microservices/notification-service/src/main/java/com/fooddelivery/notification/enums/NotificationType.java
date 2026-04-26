package com.fooddelivery.notification.enums;

/**
 * Notification type enumeration
 */
public enum NotificationType {
    EMAIL("Email Notification"),
    SMS("SMS Notification"),
    PUSH("Push Notification");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if notification type requires external service
     */
    public boolean requiresExternalService() {
        return this == EMAIL || this == SMS;
    }

    /**
     * Check if notification type is real-time
     */
    public boolean isRealTime() {
        return this == PUSH || this == SMS;
    }
}
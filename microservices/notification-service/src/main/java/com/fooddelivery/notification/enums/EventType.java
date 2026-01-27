package com.fooddelivery.notification.enums;

/**
 * Event type enumeration for different system events
 */
public enum EventType {
    // User events
    USER_REGISTERED("User Registration"),
    USER_VERIFIED("User Verification"),
    
    // Order events
    ORDER_PLACED("Order Placed"),
    ORDER_ACCEPTED("Order Accepted by Restaurant"),
    ORDER_REJECTED("Order Rejected by Restaurant"),
    ORDER_PREPARING("Order Being Prepared"),
    ORDER_READY("Order Ready for Pickup"),
    ORDER_CANCELLED("Order Cancelled"),
    ORDER_DELIVERED("Order Delivered"),
    
    // Payment events
    PAYMENT_SUCCESS("Payment Successful"),
    PAYMENT_FAILED("Payment Failed"),
    PAYMENT_REFUNDED("Payment Refunded"),
    
    // Delivery events
    DELIVERY_ASSIGNED("Delivery Partner Assigned"),
    DELIVERY_ACCEPTED("Delivery Accepted by Partner"),
    DELIVERY_PICKED_UP("Order Picked Up"),
    DELIVERY_ON_THE_WAY("Delivery On The Way"),
    
    // Restaurant events
    RESTAURANT_APPROVED("Restaurant Approved"),
    RESTAURANT_SUSPENDED("Restaurant Suspended"),
    
    // Promotional events
    PROMOTIONAL_OFFER("Promotional Offer"),
    DISCOUNT_AVAILABLE("Discount Available"),
    
    // System events
    SYSTEM_MAINTENANCE("System Maintenance"),
    ACCOUNT_LOCKED("Account Locked"),
    PASSWORD_RESET("Password Reset");

    private final String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if event type is critical (requires immediate notification)
     */
    public boolean isCritical() {
        return this == PAYMENT_FAILED || 
               this == ORDER_CANCELLED || 
               this == ACCOUNT_LOCKED ||
               this == SYSTEM_MAINTENANCE;
    }

    /**
     * Check if event type is user-facing
     */
    public boolean isUserFacing() {
        return !this.name().startsWith("SYSTEM_") && 
               this != RESTAURANT_APPROVED && 
               this != RESTAURANT_SUSPENDED;
    }

    /**
     * Get default notification types for this event
     */
    public NotificationType[] getDefaultNotificationTypes() {
        return switch (this) {
            case USER_REGISTERED, USER_VERIFIED, PASSWORD_RESET -> 
                new NotificationType[]{NotificationType.EMAIL};
            case ORDER_PLACED, ORDER_ACCEPTED, ORDER_DELIVERED, PAYMENT_SUCCESS -> 
                new NotificationType[]{NotificationType.SMS, NotificationType.PUSH};
            case PAYMENT_FAILED, ORDER_CANCELLED, ACCOUNT_LOCKED -> 
                new NotificationType[]{NotificationType.EMAIL, NotificationType.SMS, NotificationType.PUSH};
            case DELIVERY_ASSIGNED, DELIVERY_PICKED_UP, DELIVERY_ON_THE_WAY -> 
                new NotificationType[]{NotificationType.PUSH};
            case PROMOTIONAL_OFFER, DISCOUNT_AVAILABLE -> 
                new NotificationType[]{NotificationType.EMAIL, NotificationType.PUSH};
            default -> new NotificationType[]{NotificationType.PUSH};
        };
    }
}
package com.fooddelivery.entity;

/**
 * Delivery status enumeration
 */
public enum DeliveryStatus {
    /**
     * Delivery has been assigned to a delivery partner
     */
    ASSIGNED("Assigned"),
    
    /**
     * Delivery partner has accepted the delivery
     */
    ACCEPTED("Accepted"),
    
    /**
     * Order has been picked up from restaurant
     */
    PICKED_UP("Picked Up"),
    
    /**
     * Order is out for delivery
     */
    OUT_FOR_DELIVERY("Out for Delivery"),
    
    /**
     * Order has been delivered successfully
     */
    DELIVERED("Delivered"),
    
    /**
     * Delivery was cancelled
     */
    CANCELLED("Cancelled"),
    
    /**
     * Delivery was rejected by delivery partner
     */
    REJECTED("Rejected");

    private final String displayName;

    DeliveryStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if delivery is in active state
     */
    public boolean isActive() {
        return this != DELIVERED && this != CANCELLED && this != REJECTED;
    }

    /**
     * Check if delivery is completed
     */
    public boolean isCompleted() {
        return this == DELIVERED;
    }

    /**
     * Check if delivery is cancelled or rejected
     */
    public boolean isCancelled() {
        return this == CANCELLED || this == REJECTED;
    }

    /**
     * Check if delivery can be cancelled
     */
    public boolean canBeCancelled() {
        return this == ASSIGNED || this == ACCEPTED;
    }

    /**
     * Get next possible statuses
     */
    public DeliveryStatus[] getNextStatuses() {
        switch (this) {
            case ASSIGNED:
                return new DeliveryStatus[]{ACCEPTED, REJECTED};
            case ACCEPTED:
                return new DeliveryStatus[]{PICKED_UP, CANCELLED};
            case PICKED_UP:
                return new DeliveryStatus[]{OUT_FOR_DELIVERY};
            case OUT_FOR_DELIVERY:
                return new DeliveryStatus[]{DELIVERED};
            default:
                return new DeliveryStatus[]{};
        }
    }

    /**
     * Check if status transition is valid
     */
    public boolean canTransitionTo(DeliveryStatus newStatus) {
        switch (this) {
            case ASSIGNED:
                return newStatus == ACCEPTED || newStatus == REJECTED;
            case ACCEPTED:
                return newStatus == PICKED_UP || newStatus == CANCELLED;
            case PICKED_UP:
                return newStatus == OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY:
                return newStatus == DELIVERED;
            default:
                return false; // Terminal states cannot transition
        }
    }
}
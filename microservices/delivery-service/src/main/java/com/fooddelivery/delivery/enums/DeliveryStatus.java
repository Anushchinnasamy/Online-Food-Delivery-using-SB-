package com.fooddelivery.delivery.enums;

/**
 * Delivery status enumeration representing the lifecycle of a delivery
 */
public enum DeliveryStatus {
    ASSIGNED("Assigned to Delivery Partner"),
    ACCEPTED("Accepted by Delivery Partner"),
    PICKED_UP("Order Picked Up from Restaurant"),
    ON_THE_WAY("On the Way to Customer"),
    DELIVERED("Order Delivered"),
    CANCELLED("Delivery Cancelled");

    private final String displayName;

    DeliveryStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if delivery is in a final state
     */
    public boolean isFinalState() {
        return this == DELIVERED || this == CANCELLED;
    }

    /**
     * Check if delivery can be cancelled
     */
    public boolean canBeCancelled() {
        return this == ASSIGNED || this == ACCEPTED;
    }

    /**
     * Check if delivery partner can reject at this stage
     */
    public boolean canBeRejected() {
        return this == ASSIGNED;
    }

    /**
     * Get valid next states for this status
     */
    public DeliveryStatus[] getValidNextStates() {
        return switch (this) {
            case ASSIGNED -> new DeliveryStatus[]{ACCEPTED, CANCELLED};
            case ACCEPTED -> new DeliveryStatus[]{PICKED_UP, CANCELLED};
            case PICKED_UP -> new DeliveryStatus[]{ON_THE_WAY};
            case ON_THE_WAY -> new DeliveryStatus[]{DELIVERED};
            case DELIVERED, CANCELLED -> new DeliveryStatus[]{};
        };
    }

    /**
     * Check if transition to target status is valid
     */
    public boolean canTransitionTo(DeliveryStatus targetStatus) {
        DeliveryStatus[] validStates = getValidNextStates();
        for (DeliveryStatus validState : validStates) {
            if (validState == targetStatus) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if status requires delivery partner action
     */
    public boolean requiresDeliveryPartnerAction() {
        return this == ASSIGNED || this == ACCEPTED || this == PICKED_UP || this == ON_THE_WAY;
    }
}
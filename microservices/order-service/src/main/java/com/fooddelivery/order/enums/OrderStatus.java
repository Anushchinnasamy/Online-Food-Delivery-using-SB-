package com.fooddelivery.order.enums;

/**
 * Order status enumeration representing the lifecycle of an order
 */
public enum OrderStatus {
    PLACED("Order Placed"),
    ACCEPTED("Order Accepted by Restaurant"),
    PREPARING("Order is Being Prepared"),
    READY_FOR_PICKUP("Order Ready for Pickup"),
    PICKED_UP("Order Picked Up by Delivery Partner"),
    DELIVERED("Order Delivered"),
    CANCELLED("Order Cancelled");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if the order can be modified (items can be changed)
     */
    public boolean isModifiable() {
        return this == PLACED || this == ACCEPTED;
    }

    /**
     * Check if the order can be cancelled
     */
    public boolean isCancellable() {
        return this == PLACED || this == ACCEPTED;
    }

    /**
     * Check if the order is in a final state
     */
    public boolean isFinalState() {
        return this == DELIVERED || this == CANCELLED;
    }

    /**
     * Get valid next states for this status
     */
    public OrderStatus[] getValidNextStates() {
        return switch (this) {
            case PLACED -> new OrderStatus[]{ACCEPTED, CANCELLED};
            case ACCEPTED -> new OrderStatus[]{PREPARING, CANCELLED};
            case PREPARING -> new OrderStatus[]{READY_FOR_PICKUP};
            case READY_FOR_PICKUP -> new OrderStatus[]{PICKED_UP};
            case PICKED_UP -> new OrderStatus[]{DELIVERED};
            case DELIVERED, CANCELLED -> new OrderStatus[]{};
        };
    }

    /**
     * Check if transition to target status is valid
     */
    public boolean canTransitionTo(OrderStatus targetStatus) {
        OrderStatus[] validStates = getValidNextStates();
        for (OrderStatus validState : validStates) {
            if (validState == targetStatus) {
                return true;
            }
        }
        return false;
    }
}
package com.fooddelivery.entity;

/**
 * Order status enumeration representing the lifecycle of an order
 */
public enum OrderStatus {
    /**
     * Order has been placed by customer
     */
    PLACED("Order Placed"),
    
    /**
     * Order has been accepted by restaurant
     */
    ACCEPTED("Order Accepted"),
    
    /**
     * Restaurant is preparing the order
     */
    PREPARING("Preparing"),
    
    /**
     * Order is ready for pickup by delivery partner
     */
    READY_FOR_PICKUP("Ready for Pickup"),
    
    /**
     * Order has been picked up by delivery partner
     */
    PICKED_UP("Picked Up"),
    
    /**
     * Order is out for delivery
     */
    OUT_FOR_DELIVERY("Out for Delivery"),
    
    /**
     * Order has been delivered to customer
     */
    DELIVERED("Delivered"),
    
    /**
     * Order has been cancelled
     */
    CANCELLED("Cancelled"),
    
    /**
     * Order was rejected by restaurant
     */
    REJECTED("Rejected");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if order is in active state (not completed or cancelled)
     */
    public boolean isActive() {
        return this != DELIVERED && this != CANCELLED && this != REJECTED;
    }

    /**
     * Check if order is completed successfully
     */
    public boolean isCompleted() {
        return this == DELIVERED;
    }

    /**
     * Check if order is cancelled or rejected
     */
    public boolean isCancelled() {
        return this == CANCELLED || this == REJECTED;
    }

    /**
     * Check if order can be cancelled by customer
     */
    public boolean canBeCancelledByCustomer() {
        return this == PLACED || this == ACCEPTED;
    }

    /**
     * Check if order can be cancelled by restaurant
     */
    public boolean canBeCancelledByRestaurant() {
        return this == PLACED || this == ACCEPTED || this == PREPARING;
    }

    /**
     * Get next possible statuses for restaurant
     */
    public OrderStatus[] getNextStatusesForRestaurant() {
        switch (this) {
            case PLACED:
                return new OrderStatus[]{ACCEPTED, REJECTED};
            case ACCEPTED:
                return new OrderStatus[]{PREPARING, CANCELLED};
            case PREPARING:
                return new OrderStatus[]{READY_FOR_PICKUP, CANCELLED};
            default:
                return new OrderStatus[]{};
        }
    }

    /**
     * Get next possible statuses for delivery partner
     */
    public OrderStatus[] getNextStatusesForDelivery() {
        switch (this) {
            case READY_FOR_PICKUP:
                return new OrderStatus[]{PICKED_UP};
            case PICKED_UP:
                return new OrderStatus[]{OUT_FOR_DELIVERY};
            case OUT_FOR_DELIVERY:
                return new OrderStatus[]{DELIVERED};
            default:
                return new OrderStatus[]{};
        }
    }

    /**
     * Check if status transition is valid
     */
    public boolean canTransitionTo(OrderStatus newStatus) {
        switch (this) {
            case PLACED:
                return newStatus == ACCEPTED || newStatus == REJECTED || newStatus == CANCELLED;
            case ACCEPTED:
                return newStatus == PREPARING || newStatus == CANCELLED;
            case PREPARING:
                return newStatus == READY_FOR_PICKUP || newStatus == CANCELLED;
            case READY_FOR_PICKUP:
                return newStatus == PICKED_UP;
            case PICKED_UP:
                return newStatus == OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY:
                return newStatus == DELIVERED;
            default:
                return false; // Terminal states cannot transition
        }
    }
}
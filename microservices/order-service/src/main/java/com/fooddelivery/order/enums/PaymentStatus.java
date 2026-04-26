package com.fooddelivery.order.enums;

/**
 * Payment status enumeration
 */
public enum PaymentStatus {
    PENDING("Payment Pending"),
    PROCESSING("Payment Processing"),
    COMPLETED("Payment Completed"),
    FAILED("Payment Failed"),
    REFUNDED("Payment Refunded");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if payment is successful
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }

    /**
     * Check if payment is in final state
     */
    public boolean isFinalState() {
        return this == COMPLETED || this == FAILED || this == REFUNDED;
    }
}
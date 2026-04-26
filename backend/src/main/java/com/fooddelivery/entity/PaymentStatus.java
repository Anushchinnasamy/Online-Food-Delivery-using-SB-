package com.fooddelivery.entity;

/**
 * Payment status enumeration
 */
public enum PaymentStatus {
    /**
     * Payment is pending processing
     */
    PENDING("Pending"),
    
    /**
     * Payment is being processed
     */
    PROCESSING("Processing"),
    
    /**
     * Payment completed successfully
     */
    SUCCESS("Success"),
    
    /**
     * Payment failed
     */
    FAILED("Failed"),
    
    /**
     * Payment was cancelled
     */
    CANCELLED("Cancelled"),
    
    /**
     * Payment was fully refunded
     */
    REFUNDED("Refunded"),
    
    /**
     * Payment was partially refunded
     */
    PARTIALLY_REFUNDED("Partially Refunded");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if payment is in a final state
     */
    public boolean isFinal() {
        return this == SUCCESS || this == FAILED || this == CANCELLED || this == REFUNDED;
    }

    /**
     * Check if payment is successful
     */
    public boolean isSuccessful() {
        return this == SUCCESS || this == PARTIALLY_REFUNDED;
    }

    /**
     * Check if payment failed
     */
    public boolean isFailed() {
        return this == FAILED || this == CANCELLED;
    }

    /**
     * Check if payment can be refunded
     */
    public boolean canBeRefunded() {
        return this == SUCCESS || this == PARTIALLY_REFUNDED;
    }

    /**
     * Check if payment is in progress
     */
    public boolean isInProgress() {
        return this == PENDING || this == PROCESSING;
    }
}
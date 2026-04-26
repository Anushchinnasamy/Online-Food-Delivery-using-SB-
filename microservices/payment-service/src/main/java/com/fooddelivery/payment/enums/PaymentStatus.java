package com.fooddelivery.payment.enums;

/**
 * Payment status enumeration representing the lifecycle of a payment
 */
public enum PaymentStatus {
    INITIATED("Payment Initiated"),
    PROCESSING("Payment Processing"),
    SUCCESS("Payment Successful"),
    FAILED("Payment Failed"),
    CANCELLED("Payment Cancelled"),
    REFUNDED("Payment Refunded"),
    PARTIALLY_REFUNDED("Payment Partially Refunded");

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
    public boolean isFinalState() {
        return this == SUCCESS || this == FAILED || this == CANCELLED || this == REFUNDED;
    }

    /**
     * Check if payment can be refunded
     */
    public boolean canBeRefunded() {
        return this == SUCCESS || this == PARTIALLY_REFUNDED;
    }

    /**
     * Check if payment is successful
     */
    public boolean isSuccessful() {
        return this == SUCCESS;
    }

    /**
     * Get valid next states for this status
     */
    public PaymentStatus[] getValidNextStates() {
        return switch (this) {
            case INITIATED -> new PaymentStatus[]{PROCESSING, FAILED, CANCELLED};
            case PROCESSING -> new PaymentStatus[]{SUCCESS, FAILED};
            case SUCCESS -> new PaymentStatus[]{REFUNDED, PARTIALLY_REFUNDED};
            case PARTIALLY_REFUNDED -> new PaymentStatus[]{REFUNDED};
            case FAILED, CANCELLED, REFUNDED -> new PaymentStatus[]{};
        };
    }

    /**
     * Check if transition to target status is valid
     */
    public boolean canTransitionTo(PaymentStatus targetStatus) {
        PaymentStatus[] validStates = getValidNextStates();
        for (PaymentStatus validState : validStates) {
            if (validState == targetStatus) {
                return true;
            }
        }
        return false;
    }
}
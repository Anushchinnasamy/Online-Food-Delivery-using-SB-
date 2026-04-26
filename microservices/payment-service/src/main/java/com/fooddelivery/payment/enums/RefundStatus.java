package com.fooddelivery.payment.enums;

/**
 * Refund status enumeration
 */
public enum RefundStatus {
    INITIATED("Refund Initiated"),
    PROCESSING("Refund Processing"),
    SUCCESS("Refund Successful"),
    FAILED("Refund Failed"),
    CANCELLED("Refund Cancelled");

    private final String displayName;

    RefundStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if refund is in a final state
     */
    public boolean isFinalState() {
        return this == SUCCESS || this == FAILED || this == CANCELLED;
    }

    /**
     * Check if refund is successful
     */
    public boolean isSuccessful() {
        return this == SUCCESS;
    }

    /**
     * Get valid next states for this status
     */
    public RefundStatus[] getValidNextStates() {
        return switch (this) {
            case INITIATED -> new RefundStatus[]{PROCESSING, FAILED, CANCELLED};
            case PROCESSING -> new RefundStatus[]{SUCCESS, FAILED};
            case SUCCESS, FAILED, CANCELLED -> new RefundStatus[]{};
        };
    }

    /**
     * Check if transition to target status is valid
     */
    public boolean canTransitionTo(RefundStatus targetStatus) {
        RefundStatus[] validStates = getValidNextStates();
        for (RefundStatus validState : validStates) {
            if (validState == targetStatus) {
                return true;
            }
        }
        return false;
    }
}
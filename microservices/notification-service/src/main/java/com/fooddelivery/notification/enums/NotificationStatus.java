package com.fooddelivery.notification.enums;

/**
 * Notification status enumeration
 */
public enum NotificationStatus {
    PENDING("Pending"),
    SENT("Sent"),
    FAILED("Failed"),
    RETRYING("Retrying");

    private final String displayName;

    NotificationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if notification is in final state
     */
    public boolean isFinalState() {
        return this == SENT || this == FAILED;
    }

    /**
     * Check if notification can be retried
     */
    public boolean canBeRetried() {
        return this == FAILED;
    }

    /**
     * Check if notification is successful
     */
    public boolean isSuccessful() {
        return this == SENT;
    }

    /**
     * Get valid next states for this status
     */
    public NotificationStatus[] getValidNextStates() {
        return switch (this) {
            case PENDING -> new NotificationStatus[]{SENT, FAILED, RETRYING};
            case RETRYING -> new NotificationStatus[]{SENT, FAILED};
            case SENT, FAILED -> new NotificationStatus[]{};
        };
    }

    /**
     * Check if transition to target status is valid
     */
    public boolean canTransitionTo(NotificationStatus targetStatus) {
        NotificationStatus[] validStates = getValidNextStates();
        for (NotificationStatus validState : validStates) {
            if (validState == targetStatus) {
                return true;
            }
        }
        return false;
    }
}
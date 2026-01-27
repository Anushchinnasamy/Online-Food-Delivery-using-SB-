package com.fooddelivery.delivery.enums;

/**
 * Vehicle type enumeration for delivery partners
 */
public enum VehicleType {
    BICYCLE("Bicycle"),
    MOTORCYCLE("Motorcycle"),
    SCOOTER("Scooter"),
    CAR("Car");

    private final String displayName;

    VehicleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if vehicle type is suitable for long distance deliveries
     */
    public boolean isSuitableForLongDistance() {
        return this == MOTORCYCLE || this == CAR;
    }

    /**
     * Get maximum delivery radius in kilometers
     */
    public double getMaxDeliveryRadius() {
        return switch (this) {
            case BICYCLE -> 3.0;
            case SCOOTER -> 5.0;
            case MOTORCYCLE -> 10.0;
            case CAR -> 15.0;
        };
    }
}
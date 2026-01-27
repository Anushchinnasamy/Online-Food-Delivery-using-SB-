package com.fooddelivery.auth.entity;

/**
 * User roles in the Food Delivery Platform
 */
public enum UserRole {
    CUSTOMER("Customer"),
    RESTAURANT_ADMIN("Restaurant Admin"),
    DELIVERY_PARTNER("Delivery Partner"),
    PLATFORM_ADMIN("Platform Admin");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
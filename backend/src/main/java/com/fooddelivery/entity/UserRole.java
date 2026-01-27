package com.fooddelivery.entity;

/**
 * User roles in the food delivery platform
 * Each role has specific permissions and access levels
 */
public enum UserRole {
    /**
     * Customer - Can browse restaurants, place orders, track deliveries
     */
    CUSTOMER("Customer"),
    
    /**
     * Restaurant Admin - Can manage restaurant, menu, and process orders
     */
    RESTAURANT_ADMIN("Restaurant Admin"),
    
    /**
     * Delivery Partner - Can accept deliveries and update delivery status
     */
    DELIVERY_PARTNER("Delivery Partner"),
    
    /**
     * Platform Admin - Can manage entire platform, users, and analytics
     */
    PLATFORM_ADMIN("Platform Admin");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if role has customer permissions
     */
    public boolean isCustomer() {
        return this == CUSTOMER;
    }

    /**
     * Check if role has restaurant admin permissions
     */
    public boolean isRestaurantAdmin() {
        return this == RESTAURANT_ADMIN;
    }

    /**
     * Check if role has delivery partner permissions
     */
    public boolean isDeliveryPartner() {
        return this == DELIVERY_PARTNER;
    }

    /**
     * Check if role has platform admin permissions
     */
    public boolean isPlatformAdmin() {
        return this == PLATFORM_ADMIN;
    }

    /**
     * Check if role has admin privileges (Restaurant or Platform admin)
     */
    public boolean isAdmin() {
        return this == RESTAURANT_ADMIN || this == PLATFORM_ADMIN;
    }
}
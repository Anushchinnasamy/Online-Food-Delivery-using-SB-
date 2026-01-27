package com.fooddelivery.restaurant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Restaurant entity representing a restaurant in the food delivery platform
 */
@Entity
@Table(name = "restaurants", indexes = {
    @Index(name = "idx_restaurant_city", columnList = "city"),
    @Index(name = "idx_restaurant_cuisine", columnList = "cuisineType"),
    @Index(name = "idx_restaurant_active_approved", columnList = "isActive, isApproved"),
    @Index(name = "idx_restaurant_location", columnList = "latitude, longitude"),
    @Index(name = "idx_restaurant_rating", columnList = "rating")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Restaurant name is required")
    @Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(length = 500)
    private String description;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City name cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String city;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
    @Column(nullable = false, length = 6)
    private String pincode;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Column(nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Column(nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    @Column(nullable = false, length = 10)
    private String phone;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Cuisine type is required")
    @Size(max = 50, message = "Cuisine type cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String cuisineType;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    @Column(length = 500)
    private String imageUrl;

    @DecimalMin(value = "0.0", message = "Rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.0")
    @Column(precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;

    @Min(value = 0, message = "Total reviews cannot be negative")
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Min(value = 1, message = "Delivery time must be at least 1 minute")
    @Max(value = 120, message = "Delivery time cannot exceed 120 minutes")
    @Column(name = "delivery_time_minutes")
    private Integer deliveryTimeMinutes;

    @DecimalMin(value = "0.0", message = "Minimum order amount cannot be negative")
    @Column(name = "minimum_order_amount", precision = 10, scale = 2)
    private BigDecimal minimumOrderAmount;

    @DecimalMin(value = "0.0", message = "Delivery fee cannot be negative")
    @Column(name = "delivery_fee", precision = 8, scale = 2)
    private BigDecimal deliveryFee;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = false;

    @NotNull(message = "Opening time is required")
    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @NotNull(message = "Closing time is required")
    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    // Audit fields
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Business logic methods
    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }

    public boolean isVisibleToCustomers() {
        return isActive && isApproved && !isDeleted();
    }

    public boolean isCurrentlyOpen() {
        if (!isOpen || !isVisibleToCustomers()) {
            return false;
        }
        
        LocalTime now = LocalTime.now();
        
        // Handle case where restaurant closes after midnight
        if (closingTime.isBefore(openingTime)) {
            return now.isAfter(openingTime) || now.isBefore(closingTime);
        } else {
            return now.isAfter(openingTime) && now.isBefore(closingTime);
        }
    }

    public void approve() {
        this.isApproved = true;
    }

    public void suspend() {
        this.isApproved = false;
        this.isOpen = false;
    }

    public void updateRating(BigDecimal newRating, Integer newTotalReviews) {
        this.rating = newRating;
        this.totalReviews = newTotalReviews;
    }
}
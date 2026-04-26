package com.fooddelivery.menu.entity;

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

/**
 * MenuItem entity representing a menu item in the food delivery platform
 */
@Entity
@Table(name = "menu_items", indexes = {
    @Index(name = "idx_menu_item_restaurant", columnList = "restaurantId"),
    @Index(name = "idx_menu_item_category", columnList = "category"),
    @Index(name = "idx_menu_item_available", columnList = "isAvailable"),
    @Index(name = "idx_menu_item_vegetarian", columnList = "isVegetarian"),
    @Index(name = "idx_menu_item_restaurant_available", columnList = "restaurantId, isAvailable")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Restaurant ID is required")
    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @NotBlank(message = "Menu item name is required")
    @Size(min = 2, max = 100, message = "Menu item name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(length = 500)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String category;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "is_vegetarian", nullable = false)
    private Boolean isVegetarian = false;

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
        this.isAvailable = false;
    }

    public boolean isVisibleToCustomers() {
        return isAvailable && !isDeleted();
    }

    public void toggleAvailability() {
        this.isAvailable = !this.isAvailable;
    }

    public void makeUnavailable() {
        this.isAvailable = false;
    }

    public void makeAvailable() {
        if (!isDeleted()) {
            this.isAvailable = true;
        }
    }
}
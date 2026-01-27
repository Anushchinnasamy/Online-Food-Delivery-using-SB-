package com.fooddelivery.delivery.entity;

import com.fooddelivery.delivery.enums.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Delivery Partner entity representing a delivery partner in the system
 */
@Entity
@Table(name = "delivery_partners", indexes = {
    @Index(name = "idx_delivery_partner_phone", columnList = "phone", unique = true),
    @Index(name = "idx_delivery_partner_active", columnList = "isActive"),
    @Index(name = "idx_delivery_partner_vehicle", columnList = "vehicleType"),
    @Index(name = "idx_delivery_partner_created", columnList = "createdAt")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Phone is required")
    @Size(min = 10, max = 15, message = "Phone must be between 10 and 15 characters")
    @Column(nullable = false, unique = true, length = 15)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @Column(name = "rating", precision = 3, scale = 2)
    private Double rating = 0.0;

    @Column(name = "total_deliveries")
    private Integer totalDeliveries = 0;

    @Column(name = "successful_deliveries")
    private Integer successfulDeliveries = 0;

    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

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
        this.isAvailable = false;
    }

    public void activate() {
        this.isActive = true;
        this.deletedAt = null;
    }

    public void deactivate() {
        this.isActive = false;
        this.isAvailable = false;
    }

    public void makeAvailable() {
        if (isActive && !isDeleted()) {
            this.isAvailable = true;
            this.lastActiveAt = LocalDateTime.now();
        }
    }

    public void makeUnavailable() {
        this.isAvailable = false;
    }

    public boolean canAcceptDelivery() {
        return isActive && isAvailable && !isDeleted();
    }

    public void updateLocation(Double latitude, Double longitude) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
        this.lastActiveAt = LocalDateTime.now();
    }

    public void incrementDeliveryCount() {
        this.totalDeliveries++;
    }

    public void incrementSuccessfulDeliveryCount() {
        this.successfulDeliveries++;
        incrementDeliveryCount();
    }

    public double getSuccessRate() {
        if (totalDeliveries == 0) {
            return 0.0;
        }
        return (double) successfulDeliveries / totalDeliveries * 100;
    }

    public void updateRating(Double newRating) {
        if (newRating != null && newRating >= 0.0 && newRating <= 5.0) {
            this.rating = newRating;
        }
    }

    public boolean isOnline() {
        return isActive && isAvailable && lastActiveAt != null && 
               lastActiveAt.isAfter(LocalDateTime.now().minusMinutes(15));
    }
}
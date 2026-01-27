package com.fooddelivery.order.entity;

import com.fooddelivery.order.enums.OrderStatus;
import com.fooddelivery.order.enums.PaymentStatus;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity representing a customer order in the food delivery platform
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_number", columnList = "orderNumber", unique = true),
    @Index(name = "idx_order_user", columnList = "userId"),
    @Index(name = "idx_order_restaurant", columnList = "restaurantId"),
    @Index(name = "idx_order_status", columnList = "orderStatus"),
    @Index(name = "idx_order_created", columnList = "createdAt"),
    @Index(name = "idx_order_user_status", columnList = "userId, orderStatus"),
    @Index(name = "idx_order_restaurant_status", columnList = "restaurantId, orderStatus")
})
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number cannot exceed 50 characters")
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Restaurant ID is required")
    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Total amount must have at most 10 integer digits and 2 decimal places")
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.PLACED;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "delivery_partner_id")
    private Long deliveryPartnerId;

    @Size(max = 500, message = "Special instructions cannot exceed 500 characters")
    @Column(name = "special_instructions", length = 500)
    private String specialInstructions;

    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @Column(name = "actual_delivery_time")
    private LocalDateTime actualDeliveryTime;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "cancelled_by")
    private String cancelledBy; // USER, RESTAURANT, SYSTEM

    // Audit fields
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-many relationship with OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Business logic methods
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    public boolean canBeModified() {
        return orderStatus.isModifiable();
    }

    public boolean canBeCancelled() {
        return orderStatus.isCancellable();
    }

    public boolean isInFinalState() {
        return orderStatus.isFinalState();
    }

    public void updateStatus(OrderStatus newStatus) {
        if (!orderStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", orderStatus, newStatus)
            );
        }
        this.orderStatus = newStatus;
        
        // Set delivery time when order is delivered
        if (newStatus == OrderStatus.DELIVERED) {
            this.actualDeliveryTime = LocalDateTime.now();
        }
    }

    public void cancel(String reason, String cancelledBy) {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Order cannot be cancelled in current state: " + orderStatus);
        }
        this.orderStatus = OrderStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledBy = cancelledBy;
    }

    public void assignDeliveryPartner(Long deliveryPartnerId) {
        if (orderStatus != OrderStatus.READY_FOR_PICKUP) {
            throw new IllegalStateException("Order must be ready for pickup to assign delivery partner");
        }
        this.deliveryPartnerId = deliveryPartnerId;
    }

    public void calculateTotalAmount() {
        this.totalAmount = orderItems.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItemCount() {
        return orderItems.stream()
            .mapToInt(OrderItem::getQuantity)
            .sum();
    }
}
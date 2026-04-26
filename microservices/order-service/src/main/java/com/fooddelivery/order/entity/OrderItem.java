package com.fooddelivery.order.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * OrderItem entity representing individual items within an order
 * Stores snapshots of menu item data at the time of order
 */
@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_item_order", columnList = "order_id"),
    @Index(name = "idx_order_item_menu", columnList = "menu_item_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull(message = "Menu item ID is required")
    @Column(name = "menu_item_id", nullable = false)
    private Long menuItemId;

    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name cannot exceed 100 characters")
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @NotNull(message = "Item price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Item price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Item price must have at most 8 integer digits and 2 decimal places")
    @Column(name = "item_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal itemPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 99, message = "Quantity cannot exceed 99")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Subtotal must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Subtotal must have at most 10 integer digits and 2 decimal places")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Size(max = 200, message = "Special instructions cannot exceed 200 characters")
    @Column(name = "special_instructions", length = 200)
    private String specialInstructions;

    // Business logic methods
    public void calculateSubtotal() {
        if (itemPrice != null && quantity != null) {
            this.subtotal = itemPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.quantity = newQuantity;
        calculateSubtotal();
    }

    // Constructor for creating order item from menu item
    public OrderItem(Long menuItemId, String itemName, BigDecimal itemPrice, Integer quantity) {
        this.menuItemId = menuItemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
        calculateSubtotal();
    }

    public OrderItem(Long menuItemId, String itemName, BigDecimal itemPrice, Integer quantity, String specialInstructions) {
        this(menuItemId, itemName, itemPrice, quantity);
        this.specialInstructions = specialInstructions;
    }
}
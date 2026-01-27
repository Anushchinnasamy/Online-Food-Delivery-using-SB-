package com.fooddelivery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OrderItem entity representing individual items within an order
 */
@Entity
@Table(name = "order_items", indexes = {
    @Index(name = "idx_order_item_order", columnList = "order_id"),
    @Index(name = "idx_order_item_menu_item", columnList = "menu_item_id")
})
@EntityListeners(AuditingEntityListener.class)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "item_description", length = 500)
    private String itemDescription;

    @Column(name = "special_instructions", length = 200)
    private String specialInstructions;

    // Audit fields
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    // Constructors
    public OrderItem() {}

    public OrderItem(MenuItem menuItem, Integer quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.price = menuItem.getPrice();
        this.itemName = menuItem.getName();
        this.itemDescription = menuItem.getDescription();
    }

    public OrderItem(MenuItem menuItem, Integer quantity, String specialInstructions) {
        this(menuItem, quantity);
        this.specialInstructions = specialInstructions;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

    // Utility methods
    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public void updateQuantity(Integer newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        this.quantity = newQuantity;
    }

    public boolean hasSpecialInstructions() {
        return specialInstructions != null && !specialInstructions.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        OrderItem orderItem = (OrderItem) o;
        return id != null && id.equals(orderItem.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
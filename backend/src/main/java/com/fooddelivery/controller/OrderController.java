package com.fooddelivery.controller;

import com.fooddelivery.dto.CreateOrderRequest;
import com.fooddelivery.dto.OrderDTO;
import com.fooddelivery.entity.OrderStatus;
import com.fooddelivery.security.CustomUserDetails;
import com.fooddelivery.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for order operations
 */
@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Orders", description = "Order placement and management APIs")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * Create order (Customer only)
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Place an order", description = "Create a new order with items from a restaurant (Customer only)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("POST /orders - Creating order for customer: {}", userDetails.getId());
        OrderDTO order = orderService.createOrder(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT_ADMIN', 'PLATFORM_ADMIN')")
    public ResponseEntity<OrderDTO> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("GET /orders/{} - Fetching order", id);
        OrderDTO order = orderService.getOrderById(id, userDetails.getId());
        return ResponseEntity.ok(order);
    }

    /**
     * Get my orders (Customer only)
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get my orders", description = "Retrieve all orders for the authenticated customer",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<OrderDTO>> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("GET /orders/my - Fetching orders for customer: {}", userDetails.getId());
        List<OrderDTO> orders = orderService.getCustomerOrders(userDetails.getId());
        return ResponseEntity.ok(orders);
    }

    /**
     * Get restaurant orders (Restaurant Admin only)
     */
    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<List<OrderDTO>> getRestaurantOrders(
            @PathVariable Long restaurantId) {
        logger.info("GET /orders/restaurant/{} - Fetching restaurant orders", restaurantId);
        List<OrderDTO> orders = orderService.getRestaurantOrders(restaurantId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get orders by status (Platform Admin only)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@PathVariable OrderStatus status) {
        logger.info("GET /orders/status/{} - Fetching orders by status", status);
        List<OrderDTO> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * Update order status (Restaurant Admin only)
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("PUT /orders/{}/status - Updating order status", id);
        
        String statusStr = request.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }
        
        OrderStatus newStatus = OrderStatus.valueOf(statusStr);
        OrderDTO order = orderService.updateOrderStatus(id, newStatus, userDetails.getId());
        return ResponseEntity.ok(order);
    }

    /**
     * Cancel order (Customer only)
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderDTO> cancelOrder(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info("PUT /orders/{}/cancel - Cancelling order", id);
        
        String reason = request.getOrDefault("reason", "Customer requested cancellation");
        OrderDTO order = orderService.cancelOrder(id, reason, userDetails.getId());
        return ResponseEntity.ok(order);
    }
}

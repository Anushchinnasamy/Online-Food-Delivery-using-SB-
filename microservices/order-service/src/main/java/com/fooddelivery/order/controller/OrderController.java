package com.fooddelivery.order.controller;

import com.fooddelivery.order.dto.request.OrderCreateRequestDTO;
import com.fooddelivery.order.dto.request.OrderStatusUpdateRequestDTO;
import com.fooddelivery.order.dto.response.OrderResponseDTO;
import com.fooddelivery.order.dto.response.OrderSummaryResponseDTO;
import com.fooddelivery.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for order management operations
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    // ==================== CUSTOMER ENDPOINTS ====================

    /**
     * Create a new order (Customer)
     */
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody OrderCreateRequestDTO request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        log.info("Creating order for user: {} at restaurant: {}", userId, request.getRestaurantId());
        
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        OrderResponseDTO response = orderService.createOrder(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get order by ID (Customer)
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        log.debug("Fetching order with ID: {} for user: {}", id, userId);
        
        OrderResponseDTO response = orderService.getOrderById(id, userId, userRole);
        return ResponseEntity.ok(response);
    }

    /**
     * Get order by order number (Customer)
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponseDTO> getOrderByOrderNumber(
            @PathVariable String orderNumber,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        log.debug("Fetching order with number: {} for user: {}", orderNumber, userId);
        
        OrderResponseDTO response = orderService.getOrderByOrderNumber(orderNumber, userId, userRole);
        return ResponseEntity.ok(response);
    }

    /**
     * Get orders by user ID (Customer)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderSummaryResponseDTO>> getOrdersByUserId(@PathVariable Long userId) {
        log.debug("Fetching orders for user: {}", userId);
        
        List<OrderSummaryResponseDTO> response = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get orders by user ID with pagination (Customer)
     */
    @GetMapping("/user/{userId}/page")
    public ResponseEntity<Page<OrderSummaryResponseDTO>> getOrdersByUserIdWithPagination(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Fetching orders for user: {} with pagination", userId);
        
        Page<OrderSummaryResponseDTO> response = orderService.getOrdersByUserId(userId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel order (Customer)
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {
        log.info("Cancelling order with ID: {} by user: {}", id, userId);
        
        String reason = request.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            reason = "Cancelled by customer";
        }
        
        OrderResponseDTO response = orderService.cancelOrder(id, reason, userId, userRole);
        return ResponseEntity.ok(response);
    }

    // ==================== RESTAURANT ADMIN ENDPOINTS ====================

    /**
     * Get orders by restaurant ID (Restaurant Admin)
     */
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderSummaryResponseDTO>> getOrdersByRestaurantId(
            @PathVariable Long restaurantId,
            @RequestHeader(value = "X-Restaurant-Id", required = false) Long currentUserRestaurantId) {
        log.debug("Fetching orders for restaurant: {}", restaurantId);
        
        List<OrderSummaryResponseDTO> response = orderService.getOrdersByRestaurantId(restaurantId, currentUserRestaurantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get orders by restaurant ID with pagination (Restaurant Admin)
     */
    @GetMapping("/restaurant/{restaurantId}/page")
    public ResponseEntity<Page<OrderSummaryResponseDTO>> getOrdersByRestaurantIdWithPagination(
            @PathVariable Long restaurantId,
            @RequestHeader(value = "X-Restaurant-Id", required = false) Long currentUserRestaurantId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("Fetching orders for restaurant: {} with pagination", restaurantId);
        
        Page<OrderSummaryResponseDTO> response = orderService.getOrdersByRestaurantId(restaurantId, currentUserRestaurantId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Get active orders by restaurant ID (Restaurant Admin)
     */
    @GetMapping("/restaurant/{restaurantId}/active")
    public ResponseEntity<List<OrderSummaryResponseDTO>> getActiveOrdersByRestaurantId(
            @PathVariable Long restaurantId,
            @RequestHeader(value = "X-Restaurant-Id", required = false) Long currentUserRestaurantId) {
        log.debug("Fetching active orders for restaurant: {}", restaurantId);
        
        List<OrderSummaryResponseDTO> response = orderService.getActiveOrdersByRestaurantId(restaurantId, currentUserRestaurantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update order status (Restaurant Admin)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequestDTO request,
            @RequestHeader(value = "X-Restaurant-Id", required = false) Long currentUserRestaurantId) {
        log.info("Updating order status for ID: {} to {}", id, request.getOrderStatus());
        
        OrderResponseDTO response = orderService.updateOrderStatus(id, request.getOrderStatus(), currentUserRestaurantId);
        return ResponseEntity.ok(response);
    }

    // ==================== DELIVERY PARTNER ENDPOINTS ====================

    /**
     * Get orders assigned to delivery partner (Delivery Partner)
     */
    @GetMapping("/assigned/{deliveryPartnerId}")
    public ResponseEntity<List<OrderSummaryResponseDTO>> getOrdersByDeliveryPartnerId(@PathVariable Long deliveryPartnerId) {
        log.debug("Fetching orders for delivery partner: {}", deliveryPartnerId);
        
        List<OrderSummaryResponseDTO> response = orderService.getOrdersByDeliveryPartnerId(deliveryPartnerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get orders ready for pickup (Delivery Partner)
     */
    @GetMapping("/ready-for-pickup")
    public ResponseEntity<List<OrderSummaryResponseDTO>> getOrdersReadyForPickup() {
        log.debug("Fetching orders ready for pickup");
        
        List<OrderSummaryResponseDTO> response = orderService.getOrdersReadyForPickup();
        return ResponseEntity.ok(response);
    }

    /**
     * Assign delivery partner to order (Internal/Admin)
     */
    @PutMapping("/{id}/assign-delivery-partner")
    public ResponseEntity<OrderResponseDTO> assignDeliveryPartner(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        log.info("Assigning delivery partner to order: {}", id);
        
        Long deliveryPartnerId = request.get("delivery_partner_id");
        if (deliveryPartnerId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        OrderResponseDTO response = orderService.assignDeliveryPartner(id, deliveryPartnerId);
        return ResponseEntity.ok(response);
    }

    // ==================== UTILITY ENDPOINTS ====================

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "order-service",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
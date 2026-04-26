package com.fooddelivery.order.service;

import com.fooddelivery.order.client.MenuServiceClient;
import com.fooddelivery.order.client.RestaurantServiceClient;
import com.fooddelivery.order.dto.request.OrderCreateRequestDTO;
import com.fooddelivery.order.dto.request.OrderItemCreateRequestDTO;
import com.fooddelivery.order.dto.response.OrderResponseDTO;
import com.fooddelivery.order.dto.response.OrderSummaryResponseDTO;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderItem;
import com.fooddelivery.order.enums.OrderStatus;
import com.fooddelivery.order.enums.PaymentStatus;
import com.fooddelivery.order.exception.*;
import com.fooddelivery.order.mapper.OrderMapper;
import com.fooddelivery.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for order management operations
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderNumberGeneratorService orderNumberGenerator;
    private final MenuServiceClient menuServiceClient;
    private final RestaurantServiceClient restaurantServiceClient;

    /**
     * Create a new order
     */
    public OrderResponseDTO createOrder(OrderCreateRequestDTO request, Long userId) {
        log.info("Creating new order for user: {} at restaurant: {}", userId, request.getRestaurantId());

        // Validate restaurant exists and is active
        validateRestaurant(request.getRestaurantId());

        // Fetch menu item details and validate availability
        List<Map<String, Object>> menuItems = fetchAndValidateMenuItems(request.getItems());

        // Create order entity
        Order order = new Order();
        order.setOrderNumber(orderNumberGenerator.generateOrderNumber());
        order.setUserId(userId);
        order.setRestaurantId(request.getRestaurantId());
        order.setSpecialInstructions(request.getSpecialInstructions());
        order.setOrderStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.PENDING);

        // Create order items with snapshots
        createOrderItems(order, request.getItems(), menuItems);

        // Calculate total amount
        order.calculateTotalAmount();

        // Set estimated delivery time (simplified - 30-45 minutes from now)
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(35));

        // Save order
        order = orderRepository.save(order);

        log.info("Order created successfully with ID: {} and order number: {}", order.getId(), order.getOrderNumber());
        return orderMapper.toResponseDTO(order);
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long orderId, Long userId, String userRole) {
        log.debug("Fetching order with ID: {} for user: {}", orderId, userId);

        Order order = findOrderById(orderId);
        validateOrderAccess(order, userId, userRole);

        return orderMapper.toResponseDTO(order);
    }

    /**
     * Get order by order number
     */
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderByOrderNumber(String orderNumber, Long userId, String userRole) {
        log.debug("Fetching order with number: {} for user: {}", orderNumber, userId);

        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new OrderNotFoundException("Order not found with number: " + orderNumber));

        validateOrderAccess(order, userId, userRole);

        return orderMapper.toResponseDTO(order);
    }

    /**
     * Get orders by user ID
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryResponseDTO> getOrdersByUserId(Long userId) {
        log.debug("Fetching orders for user: {}", userId);

        List<Order> orders = orderRepository.findByUserId(userId);
        return orderMapper.toSummaryResponseDTOList(orders);
    }

    /**
     * Get orders by user ID with pagination
     */
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponseDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        log.debug("Fetching orders for user: {} with pagination", userId);

        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(orderMapper::toSummaryResponseDTO);
    }

    /**
     * Get orders by restaurant ID
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryResponseDTO> getOrdersByRestaurantId(Long restaurantId, Long currentUserRestaurantId) {
        log.debug("Fetching orders for restaurant: {}", restaurantId);

        // Validate restaurant ownership
        validateRestaurantOwnership(restaurantId, currentUserRestaurantId);

        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        return orderMapper.toSummaryResponseDTOList(orders);
    }

    /**
     * Get orders by restaurant ID with pagination
     */
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponseDTO> getOrdersByRestaurantId(Long restaurantId, Long currentUserRestaurantId, Pageable pageable) {
        log.debug("Fetching orders for restaurant: {} with pagination", restaurantId);

        // Validate restaurant ownership
        validateRestaurantOwnership(restaurantId, currentUserRestaurantId);

        Page<Order> orders = orderRepository.findByRestaurantId(restaurantId, pageable);
        return orders.map(orderMapper::toSummaryResponseDTO);
    }

    /**
     * Get orders by delivery partner ID
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryResponseDTO> getOrdersByDeliveryPartnerId(Long deliveryPartnerId) {
        log.debug("Fetching orders for delivery partner: {}", deliveryPartnerId);

        List<Order> orders = orderRepository.findByDeliveryPartnerId(deliveryPartnerId);
        return orderMapper.toSummaryResponseDTOList(orders);
    }

    /**
     * Update order status
     */
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus, Long currentUserRestaurantId) {
        log.info("Updating order status for ID: {} to {}", orderId, newStatus);

        Order order = findOrderById(orderId);

        // Validate restaurant ownership for restaurant admin
        if (currentUserRestaurantId != null) {
            validateRestaurantOwnership(order.getRestaurantId(), currentUserRestaurantId);
        }

        try {
            order.updateStatus(newStatus);
            order = orderRepository.save(order);

            log.info("Order status updated successfully for ID: {} to {}", orderId, newStatus);
            return orderMapper.toResponseDTO(order);

        } catch (IllegalStateException e) {
            throw new InvalidOrderStateException(e.getMessage());
        }
    }

    /**
     * Cancel order
     */
    public OrderResponseDTO cancelOrder(Long orderId, String reason, Long userId, String userRole) {
        log.info("Cancelling order with ID: {} by user: {}", orderId, userId);

        Order order = findOrderById(orderId);
        validateOrderAccess(order, userId, userRole);

        try {
            String cancelledBy = "CUSTOMER".equals(userRole) ? "USER" : userRole;
            order.cancel(reason, cancelledBy);
            order = orderRepository.save(order);

            log.info("Order cancelled successfully with ID: {}", orderId);
            return orderMapper.toResponseDTO(order);

        } catch (IllegalStateException e) {
            throw new InvalidOrderStateException(e.getMessage());
        }
    }

    /**
     * Get active orders for restaurant
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryResponseDTO> getActiveOrdersByRestaurantId(Long restaurantId, Long currentUserRestaurantId) {
        log.debug("Fetching active orders for restaurant: {}", restaurantId);

        // Validate restaurant ownership
        validateRestaurantOwnership(restaurantId, currentUserRestaurantId);

        List<Order> orders = orderRepository.findActiveOrdersByRestaurantId(restaurantId);
        return orderMapper.toSummaryResponseDTOList(orders);
    }

    /**
     * Get orders ready for pickup
     */
    @Transactional(readOnly = true)
    public List<OrderSummaryResponseDTO> getOrdersReadyForPickup() {
        log.debug("Fetching orders ready for pickup");

        List<Order> orders = orderRepository.findOrdersReadyForPickup();
        return orderMapper.toSummaryResponseDTOList(orders);
    }

    /**
     * Assign delivery partner to order
     */
    public OrderResponseDTO assignDeliveryPartner(Long orderId, Long deliveryPartnerId) {
        log.info("Assigning delivery partner: {} to order: {}", deliveryPartnerId, orderId);

        Order order = findOrderById(orderId);

        try {
            order.assignDeliveryPartner(deliveryPartnerId);
            order = orderRepository.save(order);

            log.info("Delivery partner assigned successfully to order: {}", orderId);
            return orderMapper.toResponseDTO(order);

        } catch (IllegalStateException e) {
            throw new InvalidOrderStateException(e.getMessage());
        }
    }

    /**
     * Helper method to find order by ID
     */
    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }

    /**
     * Validate restaurant exists and is active
     * TEMPORARILY DISABLED - Feign clients not configured
     */
    private void validateRestaurant(Long restaurantId) {
        log.info("Restaurant validation temporarily disabled for ID: {}", restaurantId);
        // TODO: Re-enable when Feign clients are properly configured
        return;
    }

    /**
     * Fetch and validate menu items
     * TEMPORARILY DISABLED - Feign clients not configured
     */
    private List<Map<String, Object>> fetchAndValidateMenuItems(List<OrderItemCreateRequestDTO> items) {
        log.info("Menu item validation temporarily disabled for {} items", items.size());
        // TODO: Re-enable when Feign clients are properly configured
        
        // Return mock menu items for now
        return items.stream().map(item -> {
            Map<String, Object> mockMenuItem = Map.of(
                "id", item.getMenuItemId(),
                "name", "Menu Item " + item.getMenuItemId(),
                "price", 100.0, // Default price
                "is_available", true
            );
            return mockMenuItem;
        }).collect(Collectors.toList());
    }

    /**
     * Create order items with menu item snapshots
     */
    private void createOrderItems(Order order, List<OrderItemCreateRequestDTO> itemRequests, List<Map<String, Object>> menuItems) {
        Map<Long, Map<String, Object>> menuItemMap = menuItems.stream()
            .collect(Collectors.toMap(
                item -> ((Number) item.get("id")).longValue(),
                item -> item
            ));

        for (OrderItemCreateRequestDTO itemRequest : itemRequests) {
            Map<String, Object> menuItem = menuItemMap.get(itemRequest.getMenuItemId());
            if (menuItem == null) {
                throw new MenuItemNotAvailableException("Menu item not found: " + itemRequest.getMenuItemId());
            }

            // Check availability
            Boolean isAvailable = (Boolean) menuItem.get("is_available");
            if (isAvailable == null || !isAvailable) {
                throw new MenuItemNotAvailableException("Menu item is not available: " + menuItem.get("name"));
            }

            // Create order item with snapshot data
            String itemName = (String) menuItem.get("name");
            BigDecimal itemPrice = new BigDecimal(menuItem.get("price").toString());

            OrderItem orderItem = new OrderItem(
                itemRequest.getMenuItemId(),
                itemName,
                itemPrice,
                itemRequest.getQuantity(),
                itemRequest.getSpecialInstructions()
            );

            order.addOrderItem(orderItem);
        }
    }

    /**
     * Validate order access based on user role
     */
    private void validateOrderAccess(Order order, Long userId, String userRole) {
        if ("CUSTOMER".equals(userRole)) {
            if (!order.getUserId().equals(userId)) {
                throw new UnauthorizedAccessException("You can only access your own orders");
            }
        } else if ("RESTAURANT_ADMIN".equals(userRole)) {
            // Restaurant admin can access orders for their restaurant
            // Additional validation would be needed to check restaurant ownership
        } else if ("DELIVERY_PARTNER".equals(userRole)) {
            if (order.getDeliveryPartnerId() == null || !order.getDeliveryPartnerId().equals(userId)) {
                throw new UnauthorizedAccessException("You can only access orders assigned to you");
            }
        }
    }

    /**
     * Validate restaurant ownership
     */
    private void validateRestaurantOwnership(Long restaurantId, Long currentUserRestaurantId) {
        if (currentUserRestaurantId == null) {
            throw new UnauthorizedAccessException("Restaurant ID not found in request context");
        }
        
        if (!restaurantId.equals(currentUserRestaurantId)) {
            throw new UnauthorizedAccessException("You can only manage orders for your own restaurant");
        }
    }
}
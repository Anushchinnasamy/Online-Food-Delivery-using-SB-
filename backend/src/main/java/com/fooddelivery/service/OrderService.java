package com.fooddelivery.service;

import com.fooddelivery.dto.CreateOrderRequest;
import com.fooddelivery.dto.DeliveryDTO;
import com.fooddelivery.dto.OrderDTO;
import com.fooddelivery.dto.OrderItemDTO;
import com.fooddelivery.dto.PaymentDTO;
import com.fooddelivery.entity.*;
import com.fooddelivery.exception.BadRequestException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.exception.UnauthorizedException;
import com.fooddelivery.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for order operations
 */
@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    /**
     * Create a new order
     */
    public OrderDTO createOrder(CreateOrderRequest request, Long customerId) {
        logger.info("Creating order for customer ID: {}", customerId);

        // Validate customer
        User customer = userRepository.findById(customerId)
                .filter(u -> u.getIsActive() && !u.isDeleted() && u.getRole() == UserRole.CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        // Validate restaurant
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", request.getRestaurantId()));

        // Check if restaurant is active and approved
        if (!restaurant.getIsActive() || !restaurant.getIsApproved()) {
            throw new BadRequestException("Restaurant is not available for orders");
        }

        // Check if restaurant is open
        if (!restaurant.isCurrentlyOpen()) {
            throw new BadRequestException("Restaurant is currently closed");
        }

        // Create order
        Order order = new Order(customer, restaurant, request.getDeliveryAddress());
        order.setDeliveryLatitude(request.getDeliveryLatitude());
        order.setDeliveryLongitude(request.getDeliveryLongitude());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setSpecialInstructions(request.getSpecialInstructions());
        order.setStatus(OrderStatus.PLACED);

        // Add order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .filter(m -> !m.isDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", itemRequest.getMenuItemId()));

            // Validate menu item belongs to the restaurant
            if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
                throw new BadRequestException("Menu item does not belong to this restaurant");
            }

            // Check if menu item is available
            if (!menuItem.getIsAvailable()) {
                throw new BadRequestException("Menu item '" + menuItem.getName() + "' is not available");
            }

            OrderItem orderItem = new OrderItem(menuItem, itemRequest.getQuantity(), itemRequest.getSpecialInstructions());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems.stream().collect(Collectors.toSet()));

        // Calculate totals
        order.calculateTotals();

        // Check minimum order amount
        if (order.getSubtotal().compareTo(restaurant.getMinimumOrderAmount()) < 0) {
            throw new BadRequestException(
                    String.format("Minimum order amount is ₹%.2f. Your order is ₹%.2f",
                            restaurant.getMinimumOrderAmount(), order.getSubtotal()));
        }

        // Calculate estimated delivery time
        int preparationTime = orderItems.stream()
                .mapToInt(item -> item.getMenuItem().getPreparationTimeMinutes())
                .max()
                .orElse(15);
        order.setPreparationTimeMinutes(preparationTime);
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(preparationTime + restaurant.getDeliveryTimeMinutes()));

        // Save order
        order = orderRepository.save(order);
        logger.info("Order created successfully with ID: {}", order.getId());

        // Create payment
        Payment payment = new Payment(order, order.getTotalAmount(), request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);
        logger.info("Payment created with transaction ID: {}", payment.getTransactionId());

        // Simulate payment processing (in real app, integrate with payment gateway)
        if (request.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
            payment.setStatus(PaymentStatus.PENDING);
        } else {
            // Simulate successful payment
            payment.markAsSuccessful("Payment processed successfully");
        }
        payment = paymentRepository.save(payment);

        // Create delivery
        Delivery delivery = new Delivery(order);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setEstimatedPickupTime(LocalDateTime.now().plusMinutes(preparationTime));
        delivery.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        delivery = deliveryRepository.save(delivery);
        logger.info("Delivery created with tracking number: {}", delivery.getTrackingNumber());

        return convertToDTO(order);
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id, Long userId) {
        logger.info("Fetching order with ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        // Check authorization
        if (!order.getCustomer().getId().equals(userId) &&
                !order.getRestaurant().getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to view this order");
        }

        return convertToDTO(order);
    }

    /**
     * Get customer orders
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getCustomerOrders(Long customerId) {
        logger.info("Fetching orders for customer ID: {}", customerId);
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get restaurant orders
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getRestaurantOrders(Long restaurantId) {
        logger.info("Fetching orders for restaurant ID: {}", restaurantId);
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get orders by status
     */
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByStatus(OrderStatus status) {
        logger.info("Fetching orders with status: {}", status);
        return orderRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update order status
     */
    public OrderDTO updateOrderStatus(Long id, OrderStatus newStatus, Long userId) {
        logger.info("Updating order {} status to: {}", id, newStatus);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        // Check authorization (restaurant owner can update)
        if (!order.getRestaurant().getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this order");
        }

        // Validate status transition
        validateStatusTransition(order.getStatus(), newStatus);

        order.updateStatus(newStatus);

        // Update delivery status accordingly
        if (order.getDelivery() != null) {
            updateDeliveryStatus(order.getDelivery(), newStatus);
        }

        order = orderRepository.save(order);
        logger.info("Order status updated successfully");

        return convertToDTO(order);
    }

    /**
     * Cancel order
     */
    public OrderDTO cancelOrder(Long id, String reason, Long userId) {
        logger.info("Cancelling order ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        // Check authorization
        if (!order.getCustomer().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to cancel this order");
        }

        // Check if order can be cancelled
        if (!order.canBeCancelled()) {
            throw new BadRequestException("Order cannot be cancelled at this stage");
        }

        order.cancel(reason);

        // Cancel delivery
        if (order.getDelivery() != null) {
            order.getDelivery().cancelDelivery(reason);
        }

        // Process refund if payment was made
        if (order.getPayment() != null && order.getPayment().getStatus() == PaymentStatus.SUCCESS) {
            order.getPayment().processRefund(order.getTotalAmount());
        }

        order = orderRepository.save(order);
        logger.info("Order cancelled successfully");

        return convertToDTO(order);
    }

    /**
     * Validate status transition
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid transitions
        switch (currentStatus) {
            case PLACED:
                if (newStatus != OrderStatus.ACCEPTED && newStatus != OrderStatus.CANCELLED) {
                    throw new BadRequestException("Invalid status transition from PLACED to " + newStatus);
                }
                break;
            case ACCEPTED:
                if (newStatus != OrderStatus.PREPARING && newStatus != OrderStatus.CANCELLED) {
                    throw new BadRequestException("Invalid status transition from ACCEPTED to " + newStatus);
                }
                break;
            case PREPARING:
                if (newStatus != OrderStatus.READY_FOR_PICKUP) {
                    throw new BadRequestException("Invalid status transition from PREPARING to " + newStatus);
                }
                break;
            case READY_FOR_PICKUP:
                if (newStatus != OrderStatus.PICKED_UP) {
                    throw new BadRequestException("Invalid status transition from READY_FOR_PICKUP to " + newStatus);
                }
                break;
            case PICKED_UP:
                if (newStatus != OrderStatus.OUT_FOR_DELIVERY) {
                    throw new BadRequestException("Invalid status transition from PICKED_UP to " + newStatus);
                }
                break;
            case OUT_FOR_DELIVERY:
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new BadRequestException("Invalid status transition from OUT_FOR_DELIVERY to " + newStatus);
                }
                break;
            case DELIVERED:
            case CANCELLED:
                throw new BadRequestException("Cannot change status of " + currentStatus + " order");
        }
    }

    /**
     * Update delivery status based on order status
     */
    private void updateDeliveryStatus(Delivery delivery, OrderStatus orderStatus) {
        switch (orderStatus) {
            case ACCEPTED:
                delivery.setStatus(DeliveryStatus.ASSIGNED);
                break;
            case READY_FOR_PICKUP:
                delivery.setStatus(DeliveryStatus.ACCEPTED);
                break;
            case PICKED_UP:
                delivery.setStatus(DeliveryStatus.PICKED_UP);
                delivery.setActualPickupTime(LocalDateTime.now());
                break;
            case OUT_FOR_DELIVERY:
                delivery.setStatus(DeliveryStatus.OUT_FOR_DELIVERY);
                break;
            case DELIVERED:
                delivery.setStatus(DeliveryStatus.DELIVERED);
                delivery.setActualDeliveryTime(LocalDateTime.now());
                break;
        }
    }

    /**
     * Convert Order entity to DTO
     */
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setSubtotal(order.getSubtotal());
        dto.setDeliveryFee(order.getDeliveryFee());
        dto.setTaxAmount(order.getTaxAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setDeliveryLatitude(order.getDeliveryLatitude());
        dto.setDeliveryLongitude(order.getDeliveryLongitude());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setSpecialInstructions(order.getSpecialInstructions());
        dto.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        dto.setActualDeliveryTime(order.getActualDeliveryTime());
        dto.setPreparationTimeMinutes(order.getPreparationTimeMinutes());
        dto.setCustomerId(order.getCustomer().getId());
        dto.setCustomerName(order.getCustomer().getName());
        dto.setRestaurantId(order.getRestaurant().getId());
        dto.setRestaurantName(order.getRestaurant().getName());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        // Convert order items
        List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map(this::convertOrderItemToDTO)
                .collect(Collectors.toList());
        dto.setOrderItems(orderItemDTOs);

        // Convert payment
        if (order.getPayment() != null) {
            dto.setPayment(convertPaymentToDTO(order.getPayment()));
        }

        // Convert delivery
        if (order.getDelivery() != null) {
            dto.setDelivery(convertDeliveryToDTO(order.getDelivery()));
        }

        return dto;
    }

    private OrderItemDTO convertOrderItemToDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setMenuItemId(orderItem.getMenuItem().getId());
        dto.setItemName(orderItem.getItemName());
        dto.setItemDescription(orderItem.getItemDescription());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        dto.setSpecialInstructions(orderItem.getSpecialInstructions());
        return dto;
    }

    private PaymentDTO convertPaymentToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setTransactionId(payment.getTransactionId());
        dto.setAmount(payment.getAmount());
        dto.setMethod(payment.getMethod());
        dto.setStatus(payment.getStatus());
        dto.setProcessedAt(payment.getProcessedAt());
        dto.setOrderId(payment.getOrder().getId());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }

    private DeliveryDTO convertDeliveryToDTO(Delivery delivery) {
        DeliveryDTO dto = new DeliveryDTO();
        dto.setId(delivery.getId());
        dto.setTrackingNumber(delivery.getTrackingNumber());
        dto.setStatus(delivery.getStatus());
        dto.setPickupAddress(delivery.getPickupAddress());
        dto.setDeliveryAddress(delivery.getDeliveryAddress());
        dto.setEstimatedDeliveryTime(delivery.getEstimatedDeliveryTime());
        dto.setActualDeliveryTime(delivery.getActualDeliveryTime());
        dto.setOrderId(delivery.getOrder().getId());
        if (delivery.getDeliveryPartner() != null) {
            dto.setDeliveryPartnerId(delivery.getDeliveryPartner().getId());
            dto.setDeliveryPartnerName(delivery.getDeliveryPartner().getName());
            dto.setDeliveryPartnerPhone(delivery.getDeliveryPartner().getPhone());
        }
        dto.setCreatedAt(delivery.getCreatedAt());
        return dto;
    }
}

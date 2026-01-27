package com.fooddelivery.delivery.service;

import com.fooddelivery.delivery.dto.request.DeliveryAssignRequestDTO;
import com.fooddelivery.delivery.dto.request.DeliveryStatusUpdateRequestDTO;
import com.fooddelivery.delivery.dto.response.DeliveryResponseDTO;
import com.fooddelivery.delivery.dto.response.DeliverySummaryResponseDTO;
import com.fooddelivery.delivery.entity.Delivery;
import com.fooddelivery.delivery.entity.DeliveryPartner;
import com.fooddelivery.delivery.enums.DeliveryStatus;
import com.fooddelivery.delivery.exception.DeliveryAlreadyExistsException;
import com.fooddelivery.delivery.exception.DeliveryNotFoundException;
import com.fooddelivery.delivery.exception.DeliveryPartnerNotFoundException;
import com.fooddelivery.delivery.exception.UnauthorizedAccessException;
import com.fooddelivery.delivery.mapper.DeliveryMapper;
import com.fooddelivery.delivery.repository.DeliveryPartnerRepository;
import com.fooddelivery.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for delivery operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final DeliveryMapper deliveryMapper;
    private final DeliveryAssignmentService deliveryAssignmentService;
    private final OrderServiceClient orderServiceClient;

    /**
     * Assign delivery to a delivery partner
     */
    public DeliveryResponseDTO assignDelivery(DeliveryAssignRequestDTO request) {
        log.info("Assigning delivery for order: {}", request.getOrderId());

        // Check if delivery already exists for this order
        if (deliveryRepository.existsByOrderId(request.getOrderId())) {
            throw new DeliveryAlreadyExistsException("Delivery already exists for order: " + request.getOrderId());
        }

        // Find suitable delivery partner
        Long deliveryPartnerId = request.getPreferredDeliveryPartnerId();
        if (deliveryPartnerId == null) {
            deliveryPartnerId = deliveryAssignmentService.findBestDeliveryPartner(
                request.getRestaurantLatitude(), 
                request.getRestaurantLongitude(),
                request.getCustomerLatitude(),
                request.getCustomerLongitude()
            );
        }

        if (deliveryPartnerId == null) {
            throw new RuntimeException("No available delivery partner found");
        }

        // Validate delivery partner
        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(deliveryPartnerId)
            .filter(dp -> dp.canAcceptDelivery())
            .orElseThrow(() -> new DeliveryPartnerNotFoundException("Suitable delivery partner not found"));

        // Create delivery entity
        Delivery delivery = deliveryMapper.toEntity(request);
        delivery.setDeliveryPartnerId(deliveryPartnerId);
        delivery.setDeliveryStatus(DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());

        // Make delivery partner unavailable
        deliveryPartner.makeUnavailable();
        deliveryPartnerRepository.save(deliveryPartner);

        // Save delivery
        delivery = deliveryRepository.save(delivery);

        // Notify order service about delivery assignment
        try {
            orderServiceClient.updateOrderDeliveryStatus(request.getOrderId(), "ASSIGNED_FOR_DELIVERY");
        } catch (Exception e) {
            log.error("Failed to notify order service about delivery assignment", e);
        }

        log.info("Delivery assigned successfully with ID: {} to partner: {}", delivery.getId(), deliveryPartnerId);
        return deliveryMapper.toResponseDTO(delivery);
    }

    /**
     * Accept delivery assignment
     */
    public DeliveryResponseDTO acceptDelivery(Long deliveryId, Long deliveryPartnerId) {
        log.info("Accepting delivery: {} by partner: {}", deliveryId, deliveryPartnerId);

        Delivery delivery = findDeliveryById(deliveryId);
        
        // Validate delivery partner ownership
        if (!delivery.getDeliveryPartnerId().equals(deliveryPartnerId)) {
            throw new UnauthorizedAccessException("Delivery partner not authorized to accept this delivery");
        }

        // Validate delivery status
        if (delivery.getDeliveryStatus() != DeliveryStatus.ASSIGNED) {
            throw new IllegalStateException("Delivery cannot be accepted in current state: " + delivery.getDeliveryStatus());
        }

        // Accept delivery
        delivery.accept();
        delivery = deliveryRepository.save(delivery);

        // Notify order service
        try {
            orderServiceClient.updateOrderDeliveryStatus(delivery.getOrderId(), "ACCEPTED_BY_DELIVERY_PARTNER");
        } catch (Exception e) {
            log.error("Failed to notify order service about delivery acceptance", e);
        }

        log.info("Delivery accepted successfully: {}", deliveryId);
        return deliveryMapper.toResponseDTO(delivery);
    }

    /**
     * Reject delivery assignment
     */
    public void rejectDelivery(Long deliveryId, Long deliveryPartnerId, String rejectionReason) {
        log.info("Rejecting delivery: {} by partner: {} with reason: {}", deliveryId, deliveryPartnerId, rejectionReason);

        Delivery delivery = findDeliveryById(deliveryId);
        
        // Validate delivery partner ownership
        if (!delivery.getDeliveryPartnerId().equals(deliveryPartnerId)) {
            throw new UnauthorizedAccessException("Delivery partner not authorized to reject this delivery");
        }

        // Reject delivery
        delivery.reject(rejectionReason);
        
        // Make delivery partner available again
        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(deliveryPartnerId)
            .orElseThrow(() -> new DeliveryPartnerNotFoundException("Delivery partner not found: " + deliveryPartnerId));
        deliveryPartner.makeAvailable();
        deliveryPartnerRepository.save(deliveryPartner);

        // Try to reassign if possible
        if (delivery.canBeReassigned()) {
            try {
                Long newDeliveryPartnerId = deliveryAssignmentService.findBestDeliveryPartner(null, null, null, null);
                if (newDeliveryPartnerId != null) {
                    DeliveryPartner newDeliveryPartner = deliveryPartnerRepository.findById(newDeliveryPartnerId)
                        .orElse(null);
                    if (newDeliveryPartner != null && newDeliveryPartner.canAcceptDelivery()) {
                        delivery.reassign(newDeliveryPartnerId);
                        newDeliveryPartner.makeUnavailable();
                        deliveryPartnerRepository.save(newDeliveryPartner);
                        log.info("Delivery reassigned to partner: {}", newDeliveryPartnerId);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to reassign delivery after rejection", e);
            }
        } else {
            // Cancel delivery if cannot be reassigned
            delivery.cancel("Maximum rejection limit reached");
            
            // Notify order service
            try {
                orderServiceClient.updateOrderDeliveryStatus(delivery.getOrderId(), "DELIVERY_CANCELLED");
            } catch (Exception e) {
                log.error("Failed to notify order service about delivery cancellation", e);
            }
        }

        deliveryRepository.save(delivery);
        log.info("Delivery rejection processed: {}", deliveryId);
    }

    /**
     * Update delivery status
     */
    public DeliveryResponseDTO updateDeliveryStatus(Long deliveryId, Long deliveryPartnerId, 
                                                   DeliveryStatusUpdateRequestDTO request) {
        log.info("Updating delivery status: {} to {} by partner: {}", deliveryId, request.getDeliveryStatus(), deliveryPartnerId);

        Delivery delivery = findDeliveryById(deliveryId);
        
        // Validate delivery partner ownership
        if (!delivery.getDeliveryPartnerId().equals(deliveryPartnerId)) {
            throw new UnauthorizedAccessException("Delivery partner not authorized to update this delivery");
        }

        // Update delivery status
        delivery.updateStatus(request.getDeliveryStatus());
        
        // Add delivery notes if provided
        if (request.getDeliveryNotes() != null) {
            delivery.addDeliveryNotes(request.getDeliveryNotes());
        }

        // Update delivery partner location if provided
        if (request.getCurrentLatitude() != null && request.getCurrentLongitude() != null) {
            DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(deliveryPartnerId)
                .orElseThrow(() -> new DeliveryPartnerNotFoundException("Delivery partner not found: " + deliveryPartnerId));
            deliveryPartner.updateLocation(request.getCurrentLatitude(), request.getCurrentLongitude());
            deliveryPartnerRepository.save(deliveryPartner);
        }

        // Handle final states
        if (request.getDeliveryStatus() == DeliveryStatus.DELIVERED) {
            handleDeliveryCompletion(delivery);
        } else if (request.getDeliveryStatus() == DeliveryStatus.CANCELLED) {
            handleDeliveryCancellation(delivery);
        }

        delivery = deliveryRepository.save(delivery);

        // Notify order service
        try {
            String orderStatus = mapDeliveryStatusToOrderStatus(request.getDeliveryStatus());
            orderServiceClient.updateOrderDeliveryStatus(delivery.getOrderId(), orderStatus);
        } catch (Exception e) {
            log.error("Failed to notify order service about delivery status update", e);
        }

        log.info("Delivery status updated successfully: {} to {}", deliveryId, request.getDeliveryStatus());
        return deliveryMapper.toResponseDTO(delivery);
    }

    /**
     * Get delivery by ID
     */
    @Transactional(readOnly = true)
    public DeliveryResponseDTO getDeliveryById(Long id) {
        Delivery delivery = findDeliveryById(id);
        return deliveryMapper.toResponseDTO(delivery);
    }

    /**
     * Get delivery by order ID
     */
    @Transactional(readOnly = true)
    public DeliveryResponseDTO getDeliveryByOrderId(Long orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found for order: " + orderId));
        return deliveryMapper.toResponseDTO(delivery);
    }

    /**
     * Get deliveries assigned to delivery partner
     */
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getAssignedDeliveries(Long deliveryPartnerId) {
        List<DeliveryStatus> assignedStatuses = List.of(DeliveryStatus.ASSIGNED, DeliveryStatus.ACCEPTED);
        List<Delivery> deliveries = deliveryRepository
            .findByDeliveryPartnerIdAndDeliveryStatusInOrderByAssignedAtDesc(deliveryPartnerId, assignedStatuses);
        return deliveryMapper.toResponseDTOList(deliveries);
    }

    /**
     * Get delivery partner's delivery history
     */
    @Transactional(readOnly = true)
    public Page<DeliveryResponseDTO> getDeliveryHistory(Long deliveryPartnerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Delivery> deliveries = deliveryRepository.findByDeliveryPartnerIdOrderByCreatedAtDesc(deliveryPartnerId, pageable);
        return deliveries.map(deliveryMapper::toResponseDTO);
    }

    /**
     * Get deliveries by status
     */
    @Transactional(readOnly = true)
    public List<DeliveryResponseDTO> getDeliveriesByStatus(DeliveryStatus status) {
        List<Delivery> deliveries = deliveryRepository.findByDeliveryStatusOrderByCreatedAtDesc(status);
        return deliveryMapper.toResponseDTOList(deliveries);
    }

    /**
     * Get active delivery for delivery partner
     */
    @Transactional(readOnly = true)
    public Optional<DeliveryResponseDTO> getActiveDelivery(Long deliveryPartnerId) {
        Optional<Delivery> delivery = deliveryRepository.findActiveDeliveryByDeliveryPartnerId(deliveryPartnerId);
        return delivery.map(deliveryMapper::toResponseDTO);
    }

    /**
     * Get delivery summary with partner details
     */
    @Transactional(readOnly = true)
    public DeliverySummaryResponseDTO getDeliverySummary(Long deliveryId) {
        Delivery delivery = findDeliveryById(deliveryId);
        DeliverySummaryResponseDTO summary = deliveryMapper.toSummaryResponseDTO(delivery);
        
        // Populate delivery partner details
        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(delivery.getDeliveryPartnerId())
            .orElse(null);
        if (deliveryPartner != null) {
            summary.setDeliveryPartnerName(deliveryPartner.getName());
            summary.setDeliveryPartnerPhone(deliveryPartner.getPhone());
        }
        
        return summary;
    }

    /**
     * Cancel delivery
     */
    public DeliveryResponseDTO cancelDelivery(Long deliveryId, String cancellationReason) {
        log.info("Cancelling delivery: {} with reason: {}", deliveryId, cancellationReason);

        Delivery delivery = findDeliveryById(deliveryId);
        delivery.cancel(cancellationReason);
        
        // Make delivery partner available
        handleDeliveryCancellation(delivery);
        
        delivery = deliveryRepository.save(delivery);

        // Notify order service
        try {
            orderServiceClient.updateOrderDeliveryStatus(delivery.getOrderId(), "DELIVERY_CANCELLED");
        } catch (Exception e) {
            log.error("Failed to notify order service about delivery cancellation", e);
        }

        log.info("Delivery cancelled successfully: {}", deliveryId);
        return deliveryMapper.toResponseDTO(delivery);
    }

    // Private helper methods

    private Delivery findDeliveryById(Long id) {
        return deliveryRepository.findById(id)
            .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found with ID: " + id));
    }

    private void handleDeliveryCompletion(Delivery delivery) {
        // Make delivery partner available
        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(delivery.getDeliveryPartnerId())
            .orElse(null);
        if (deliveryPartner != null) {
            deliveryPartner.makeAvailable();
            deliveryPartner.incrementSuccessfulDeliveryCount();
            deliveryPartnerRepository.save(deliveryPartner);
        }
    }

    private void handleDeliveryCancellation(Delivery delivery) {
        // Make delivery partner available
        DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(delivery.getDeliveryPartnerId())
            .orElse(null);
        if (deliveryPartner != null) {
            deliveryPartner.makeAvailable();
            deliveryPartner.incrementDeliveryCount();
            deliveryPartnerRepository.save(deliveryPartner);
        }
    }

    private String mapDeliveryStatusToOrderStatus(DeliveryStatus deliveryStatus) {
        return switch (deliveryStatus) {
            case ASSIGNED -> "ASSIGNED_FOR_DELIVERY";
            case ACCEPTED -> "ACCEPTED_BY_DELIVERY_PARTNER";
            case PICKED_UP -> "PICKED_UP_BY_DELIVERY_PARTNER";
            case ON_THE_WAY -> "ON_THE_WAY";
            case DELIVERED -> "DELIVERED";
            case CANCELLED -> "DELIVERY_CANCELLED";
        };
    }
}
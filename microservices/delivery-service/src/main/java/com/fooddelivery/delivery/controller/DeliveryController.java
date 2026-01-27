package com.fooddelivery.delivery.controller;

import com.fooddelivery.delivery.dto.request.DeliveryAssignRequestDTO;
import com.fooddelivery.delivery.dto.request.DeliveryRejectionRequestDTO;
import com.fooddelivery.delivery.dto.request.DeliveryStatusUpdateRequestDTO;
import com.fooddelivery.delivery.dto.response.DeliveryResponseDTO;
import com.fooddelivery.delivery.dto.response.DeliverySummaryResponseDTO;
import com.fooddelivery.delivery.enums.DeliveryStatus;
import com.fooddelivery.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for delivery operations
 */
@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final DeliveryService deliveryService;

    /**
     * Assign delivery to a delivery partner
     */
    @PostMapping("/assign")
    public ResponseEntity<DeliveryResponseDTO> assignDelivery(@Valid @RequestBody DeliveryAssignRequestDTO request) {
        log.info("Assigning delivery for order: {}", request.getOrderId());
        DeliveryResponseDTO response = deliveryService.assignDelivery(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Accept delivery assignment (delivery partner endpoint)
     */
    @PutMapping("/{id}/accept")
    public ResponseEntity<DeliveryResponseDTO> acceptDelivery(
            @PathVariable Long id,
            @RequestHeader("X-Delivery-Partner-Id") Long deliveryPartnerId) {
        
        log.info("Accepting delivery: {} by partner: {}", id, deliveryPartnerId);
        DeliveryResponseDTO response = deliveryService.acceptDelivery(id, deliveryPartnerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Reject delivery assignment (delivery partner endpoint)
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<Void> rejectDelivery(
            @PathVariable Long id,
            @RequestHeader("X-Delivery-Partner-Id") Long deliveryPartnerId,
            @Valid @RequestBody DeliveryRejectionRequestDTO request) {
        
        log.info("Rejecting delivery: {} by partner: {}", id, deliveryPartnerId);
        deliveryService.rejectDelivery(id, deliveryPartnerId, request.getRejectionReason());
        return ResponseEntity.ok().build();
    }

    /**
     * Update delivery status (delivery partner endpoint)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryResponseDTO> updateDeliveryStatus(
            @PathVariable Long id,
            @RequestHeader("X-Delivery-Partner-Id") Long deliveryPartnerId,
            @Valid @RequestBody DeliveryStatusUpdateRequestDTO request) {
        
        log.info("Updating delivery status: {} to {} by partner: {}", id, request.getDeliveryStatus(), deliveryPartnerId);
        DeliveryResponseDTO response = deliveryService.updateDeliveryStatus(id, deliveryPartnerId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get delivery by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDTO> getDelivery(@PathVariable Long id) {
        log.info("Getting delivery: {}", id);
        DeliveryResponseDTO response = deliveryService.getDeliveryById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get delivery by order ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponseDTO> getDeliveryByOrderId(@PathVariable Long orderId) {
        log.info("Getting delivery for order: {}", orderId);
        DeliveryResponseDTO response = deliveryService.getDeliveryByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get delivery summary with partner details
     */
    @GetMapping("/{id}/summary")
    public ResponseEntity<DeliverySummaryResponseDTO> getDeliverySummary(@PathVariable Long id) {
        log.info("Getting delivery summary: {}", id);
        DeliverySummaryResponseDTO response = deliveryService.getDeliverySummary(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get deliveries assigned to delivery partner
     */
    @GetMapping("/assigned")
    public ResponseEntity<List<DeliveryResponseDTO>> getAssignedDeliveries(
            @RequestHeader("X-Delivery-Partner-Id") Long deliveryPartnerId) {
        
        log.info("Getting assigned deliveries for partner: {}", deliveryPartnerId);
        List<DeliveryResponseDTO> response = deliveryService.getAssignedDeliveries(deliveryPartnerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get active delivery for delivery partner
     */
    @GetMapping("/active")
    public ResponseEntity<DeliveryResponseDTO> getActiveDelivery(
            @RequestHeader("X-Delivery-Partner-Id") Long deliveryPartnerId) {
        
        log.info("Getting active delivery for partner: {}", deliveryPartnerId);
        Optional<DeliveryResponseDTO> response = deliveryService.getActiveDelivery(deliveryPartnerId);
        return response.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.noContent().build());
    }

    /**
     * Get delivery partner's delivery history
     */
    @GetMapping("/history")
    public ResponseEntity<Page<DeliveryResponseDTO>> getDeliveryHistory(
            @RequestHeader("X-Delivery-Partner-Id") Long deliveryPartnerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting delivery history for partner: {} (page: {}, size: {})", deliveryPartnerId, page, size);
        Page<DeliveryResponseDTO> response = deliveryService.getDeliveryHistory(deliveryPartnerId, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Get deliveries by status (admin endpoint)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DeliveryResponseDTO>> getDeliveriesByStatus(@PathVariable DeliveryStatus status) {
        log.info("Getting deliveries by status: {}", status);
        List<DeliveryResponseDTO> response = deliveryService.getDeliveriesByStatus(status);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel delivery (admin endpoint)
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<DeliveryResponseDTO> cancelDelivery(
            @PathVariable Long id,
            @RequestParam String cancellationReason) {
        
        log.info("Cancelling delivery: {} with reason: {}", id, cancellationReason);
        DeliveryResponseDTO response = deliveryService.cancelDelivery(id, cancellationReason);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Delivery Service is healthy");
    }
}
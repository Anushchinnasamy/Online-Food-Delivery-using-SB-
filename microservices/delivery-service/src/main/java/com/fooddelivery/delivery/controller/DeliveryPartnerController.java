package com.fooddelivery.delivery.controller;

import com.fooddelivery.delivery.dto.request.DeliveryPartnerCreateRequestDTO;
import com.fooddelivery.delivery.dto.request.DeliveryPartnerUpdateRequestDTO;
import com.fooddelivery.delivery.dto.response.DeliveryPartnerResponseDTO;
import com.fooddelivery.delivery.service.DeliveryPartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for delivery partner operations
 */
@RestController
@RequestMapping("/delivery-partners")
@RequiredArgsConstructor
@Slf4j
public class DeliveryPartnerController {

    private final DeliveryPartnerService deliveryPartnerService;

    /**
     * Create a new delivery partner
     */
    @PostMapping
    public ResponseEntity<DeliveryPartnerResponseDTO> createDeliveryPartner(
            @Valid @RequestBody DeliveryPartnerCreateRequestDTO request) {
        
        log.info("Creating delivery partner with phone: {}", request.getPhone());
        DeliveryPartnerResponseDTO response = deliveryPartnerService.createDeliveryPartner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update delivery partner information
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryPartnerResponseDTO> updateDeliveryPartner(
            @PathVariable Long id,
            @Valid @RequestBody DeliveryPartnerUpdateRequestDTO request) {
        
        log.info("Updating delivery partner: {}", id);
        DeliveryPartnerResponseDTO response = deliveryPartnerService.updateDeliveryPartner(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get delivery partner by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryPartnerResponseDTO> getDeliveryPartner(@PathVariable Long id) {
        log.info("Getting delivery partner: {}", id);
        DeliveryPartnerResponseDTO response = deliveryPartnerService.getDeliveryPartnerById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all delivery partners with pagination
     */
    @GetMapping
    public ResponseEntity<Page<DeliveryPartnerResponseDTO>> getAllDeliveryPartners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting all delivery partners (page: {}, size: {})", page, size);
        Page<DeliveryPartnerResponseDTO> response = deliveryPartnerService.getAllDeliveryPartners(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Get active delivery partners with pagination
     */
    @GetMapping("/active")
    public ResponseEntity<Page<DeliveryPartnerResponseDTO>> getActiveDeliveryPartners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Getting active delivery partners (page: {}, size: {})", page, size);
        Page<DeliveryPartnerResponseDTO> response = deliveryPartnerService.getActiveDeliveryPartners(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Get available delivery partners
     */
    @GetMapping("/available")
    public ResponseEntity<List<DeliveryPartnerResponseDTO>> getAvailableDeliveryPartners() {
        log.info("Getting available delivery partners");
        List<DeliveryPartnerResponseDTO> response = deliveryPartnerService.getAvailableDeliveryPartners();
        return ResponseEntity.ok(response);
    }

    /**
     * Get online delivery partners
     */
    @GetMapping("/online")
    public ResponseEntity<List<DeliveryPartnerResponseDTO>> getOnlineDeliveryPartners() {
        log.info("Getting online delivery partners");
        List<DeliveryPartnerResponseDTO> response = deliveryPartnerService.getOnlineDeliveryPartners();
        return ResponseEntity.ok(response);
    }

    /**
     * Find delivery partners within radius
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<DeliveryPartnerResponseDTO>> findDeliveryPartnersWithinRadius(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm) {
        
        log.info("Finding delivery partners within {} km of ({}, {})", radiusKm, latitude, longitude);
        List<DeliveryPartnerResponseDTO> response = deliveryPartnerService
            .findDeliveryPartnersWithinRadius(latitude, longitude, radiusKm);
        return ResponseEntity.ok(response);
    }

    /**
     * Activate delivery partner
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<DeliveryPartnerResponseDTO> activateDeliveryPartner(@PathVariable Long id) {
        log.info("Activating delivery partner: {}", id);
        DeliveryPartnerResponseDTO response = deliveryPartnerService.activateDeliveryPartner(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Deactivate delivery partner
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<DeliveryPartnerResponseDTO> deactivateDeliveryPartner(@PathVariable Long id) {
        log.info("Deactivating delivery partner: {}", id);
        DeliveryPartnerResponseDTO response = deliveryPartnerService.deactivateDeliveryPartner(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Make delivery partner available
     */
    @PutMapping("/{id}/make-available")
    public ResponseEntity<DeliveryPartnerResponseDTO> makeAvailable(@PathVariable Long id) {
        log.info("Making delivery partner available: {}", id);
        DeliveryPartnerResponseDTO response = deliveryPartnerService.makeAvailable(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Make delivery partner unavailable
     */
    @PutMapping("/{id}/make-unavailable")
    public ResponseEntity<DeliveryPartnerResponseDTO> makeUnavailable(@PathVariable Long id) {
        log.info("Making delivery partner unavailable: {}", id);
        DeliveryPartnerResponseDTO response = deliveryPartnerService.makeUnavailable(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update delivery partner location
     */
    @PutMapping("/{id}/location")
    public ResponseEntity<DeliveryPartnerResponseDTO> updateLocation(
            @PathVariable Long id,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        
        log.debug("Updating location for delivery partner: {} to ({}, {})", id, latitude, longitude);
        DeliveryPartnerResponseDTO response = deliveryPartnerService.updateLocation(id, latitude, longitude);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete delivery partner
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeliveryPartner(@PathVariable Long id) {
        log.info("Deleting delivery partner: {}", id);
        deliveryPartnerService.deleteDeliveryPartner(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get delivery partner statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<DeliveryPartnerService.DeliveryPartnerStatsDTO> getDeliveryPartnerStats() {
        log.info("Getting delivery partner statistics");
        DeliveryPartnerService.DeliveryPartnerStatsDTO stats = deliveryPartnerService.getDeliveryPartnerStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Delivery Partner Service is healthy");
    }
}
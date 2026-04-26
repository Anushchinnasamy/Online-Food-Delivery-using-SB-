package com.fooddelivery.delivery.service;

import com.fooddelivery.delivery.dto.request.DeliveryPartnerCreateRequestDTO;
import com.fooddelivery.delivery.dto.request.DeliveryPartnerUpdateRequestDTO;
import com.fooddelivery.delivery.dto.response.DeliveryPartnerResponseDTO;
import com.fooddelivery.delivery.entity.DeliveryPartner;
import com.fooddelivery.delivery.exception.DeliveryPartnerAlreadyExistsException;
import com.fooddelivery.delivery.exception.DeliveryPartnerNotFoundException;
import com.fooddelivery.delivery.mapper.DeliveryPartnerMapper;
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

/**
 * Service class for delivery partner operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryPartnerService {

    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryPartnerMapper deliveryPartnerMapper;

    /**
     * Create a new delivery partner
     */
    public DeliveryPartnerResponseDTO createDeliveryPartner(DeliveryPartnerCreateRequestDTO request) {
        log.info("Creating new delivery partner with phone: {}", request.getPhone());

        // Check if phone number already exists
        if (deliveryPartnerRepository.existsByPhone(request.getPhone())) {
            throw new DeliveryPartnerAlreadyExistsException("Delivery partner with phone " + request.getPhone() + " already exists");
        }

        // Create delivery partner entity
        DeliveryPartner deliveryPartner = deliveryPartnerMapper.toEntity(request);
        deliveryPartner.setIsActive(true);
        deliveryPartner.setIsAvailable(true);
        deliveryPartner.setLastActiveAt(LocalDateTime.now());

        // Save delivery partner
        deliveryPartner = deliveryPartnerRepository.save(deliveryPartner);

        log.info("Delivery partner created successfully with ID: {}", deliveryPartner.getId());
        return deliveryPartnerMapper.toResponseDTO(deliveryPartner);
    }

    /**
     * Update delivery partner information
     */
    public DeliveryPartnerResponseDTO updateDeliveryPartner(Long id, DeliveryPartnerUpdateRequestDTO request) {
        log.info("Updating delivery partner with ID: {}", id);

        DeliveryPartner deliveryPartner = findDeliveryPartnerById(id);

        // Check if phone number is being changed and already exists
        if (request.getPhone() != null && !request.getPhone().equals(deliveryPartner.getPhone())) {
            if (deliveryPartnerRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
                throw new DeliveryPartnerAlreadyExistsException("Phone number " + request.getPhone() + " is already in use");
            }
        }

        // Update delivery partner
        deliveryPartnerMapper.updateEntityFromDTO(request, deliveryPartner);

        // Update last active time if location is updated
        if (request.getCurrentLatitude() != null || request.getCurrentLongitude() != null) {
            deliveryPartner.setLastActiveAt(LocalDateTime.now());
        }

        deliveryPartner = deliveryPartnerRepository.save(deliveryPartner);

        log.info("Delivery partner updated successfully: {}", id);
        return deliveryPartnerMapper.toResponseDTO(deliveryPartner);
    }

    /**
     * Get delivery partner by ID
     */
    @Transactional(readOnly = true)
    public DeliveryPartnerResponseDTO getDeliveryPartnerById(Long id) {
        DeliveryPartner deliveryPartner = findDeliveryPartnerById(id);
        return deliveryPartnerMapper.toResponseDTO(deliveryPartner);
    }

    /**
     * Get all delivery partners with pagination
     */
    @Transactional(readOnly = true)
    public Page<DeliveryPartnerResponseDTO> getAllDeliveryPartners(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DeliveryPartner> deliveryPartners = deliveryPartnerRepository.findByDeletedAtIsNull(pageable);
        return deliveryPartners.map(deliveryPartnerMapper::toResponseDTO);
    }

    /**
     * Get active delivery partners with pagination
     */
    @Transactional(readOnly = true)
    public Page<DeliveryPartnerResponseDTO> getActiveDeliveryPartners(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DeliveryPartner> deliveryPartners = deliveryPartnerRepository.findByIsActiveTrueAndDeletedAtIsNull(pageable);
        return deliveryPartners.map(deliveryPartnerMapper::toResponseDTO);
    }

    /**
     * Get available delivery partners
     */
    @Transactional(readOnly = true)
    public List<DeliveryPartnerResponseDTO> getAvailableDeliveryPartners() {
        List<DeliveryPartner> deliveryPartners = deliveryPartnerRepository.findByIsActiveTrueAndIsAvailableTrue();
        return deliveryPartnerMapper.toResponseDTOList(deliveryPartners);
    }

    /**
     * Get online delivery partners (active in last 15 minutes)
     */
    @Transactional(readOnly = true)
    public List<DeliveryPartnerResponseDTO> getOnlineDeliveryPartners() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);
        List<DeliveryPartner> deliveryPartners = deliveryPartnerRepository.findOnlineDeliveryPartners(cutoffTime);
        return deliveryPartnerMapper.toResponseDTOList(deliveryPartners);
    }

    /**
     * Find delivery partners within radius
     */
    @Transactional(readOnly = true)
    public List<DeliveryPartnerResponseDTO> findDeliveryPartnersWithinRadius(
            Double latitude, Double longitude, Double radiusKm) {
        
        List<DeliveryPartner> deliveryPartners = deliveryPartnerRepository
            .findDeliveryPartnersWithinRadius(latitude, longitude, radiusKm);
        return deliveryPartnerMapper.toResponseDTOList(deliveryPartners);
    }

    /**
     * Activate delivery partner
     */
    public DeliveryPartnerResponseDTO activateDeliveryPartner(Long id) {
        log.info("Activating delivery partner: {}", id);

        DeliveryPartner deliveryPartner = findDeliveryPartnerById(id);
        deliveryPartner.activate();
        deliveryPartner = deliveryPartnerRepository.save(deliveryPartner);

        log.info("Delivery partner activated successfully: {}", id);
        return deliveryPartnerMapper.toResponseDTO(deliveryPartner);
    }

    /**
     * Deactivate delivery partner
     */
    public DeliveryPartnerResponseDTO deactivateDeliveryPartner(Long id) {
        log.info("Deactivating delivery partner: {}", id);

        DeliveryPartner deliveryPartner = findDeliveryPartnerById(id);
        deliveryPartner.deactivate();
        deliveryPartner = deliveryPartnerRepository.save(deliveryPartner);

        log.info("Delivery partner deactivated successfully: {}", id);
        return deliveryPartnerMapper.toResponseDTO(deliveryPartner);
    }

    /**
     * Make delivery partner available
     */
    public DeliveryPartnerResponseDTO makeAvailable(Long id) {
        log.info("Making delivery partner available: {}", id);

        DeliveryPartner deliveryPartner = findDeliveryPartnerById(id);
        
        // Check if delivery partner has active delivery
        if (deliveryRepository.hasActiveDelivery(id)) {
            throw new IllegalStateException("Cannot make delivery partner available while having active delivery");
        }

        deliveryPartner.makeAvailable();
        deliveryPartner = deliveryPartnerRepository.save(deliveryPartner);

        log.info("Delivery partner made available successfully: {}", id);
        return deliveryPartnerMapper.toResponseDTO(deliveryPartner);
    }

    /**
     * Make delivery partner unavailable
     */
    public DeliveryPartnerResponseDTO makeUnavailable(Long id) {
        log.info("Making delivery partner unavailable: {}", id);

        DeliveryPartner deliveryPartner = findDeliveryPartnerById(id);
        deliveryPartner.makeUnavailable();
        deliveryPartner = deliveryPartnerRepository.save(deliveryPartner);

        log.info("Delivery partner made unavailable successfully: {}", id);
        return deliveryPartnerMapper.toResponseDTO(deliveryPartner);
    }

    /**
     * Update delivery partner location
     */
    public DeliveryPartnerResponseDTO updateLocation(Long id, Double latitude, Double longitude) {
        log.debug("Updating location for delivery partner: {}", id);

        DeliveryPartner deliveryPartner = findDeliveryPartnerById(id);
        deliveryPartner.updateLocation(latitude, longitude);
        deliveryPartner = deliveryPartnerRepository.save(deliveryPartner);

        return deliveryPartnerMapper.toResponseDTO(deliveryPartner);
    }

    /**
     * Update delivery partner rating
     */
    public void updateDeliveryPartnerRating(Long id) {
        log.info("Updating rating for delivery partner: {}", id);

        DeliveryPartner deliveryPartner = findDeliveryPartnerById(id);
        
        // Calculate new rating from delivery ratings
        Double averageRating = deliveryRepository.getAverageRatingByDeliveryPartnerId(id);
        if (averageRating != null) {
            deliveryPartner.updateRating(averageRating);
            deliveryPartnerRepository.save(deliveryPartner);
        }

        log.info("Rating updated for delivery partner: {} to {}", id, averageRating);
    }

    /**
     * Delete delivery partner (soft delete)
     */
    public void deleteDeliveryPartner(Long id) {
        log.info("Deleting delivery partner: {}", id);

        DeliveryPartner deliveryPartner = findDeliveryPartnerById(id);
        
        // Check if delivery partner has active delivery
        if (deliveryRepository.hasActiveDelivery(id)) {
            throw new IllegalStateException("Cannot delete delivery partner with active delivery");
        }

        deliveryPartner.softDelete();
        deliveryPartnerRepository.save(deliveryPartner);

        log.info("Delivery partner deleted successfully: {}", id);
    }

    /**
     * Get delivery partner statistics
     */
    @Transactional(readOnly = true)
    public DeliveryPartnerStatsDTO getDeliveryPartnerStats() {
        long totalPartners = deliveryPartnerRepository.countByIsActiveTrueAndDeletedAtIsNull();
        long availablePartners = deliveryPartnerRepository.countByIsActiveTrueAndIsAvailableTrueAndDeletedAtIsNull();
        
        return new DeliveryPartnerStatsDTO(totalPartners, availablePartners);
    }

    // Private helper methods

    private DeliveryPartner findDeliveryPartnerById(Long id) {
        return deliveryPartnerRepository.findById(id)
            .filter(dp -> !dp.isDeleted())
            .orElseThrow(() -> new DeliveryPartnerNotFoundException("Delivery partner not found with ID: " + id));
    }

    /**
     * DTO for delivery partner statistics
     */
    public record DeliveryPartnerStatsDTO(long totalPartners, long availablePartners) {}
}
package com.fooddelivery.delivery.service;

import com.fooddelivery.delivery.entity.DeliveryPartner;
import com.fooddelivery.delivery.repository.DeliveryPartnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Service for intelligent delivery partner assignment
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DeliveryAssignmentService {

    private final DeliveryPartnerRepository deliveryPartnerRepository;

    /**
     * Find the best delivery partner for a delivery
     */
    public Long findBestDeliveryPartner(Double restaurantLatitude, Double restaurantLongitude,
                                       Double customerLatitude, Double customerLongitude) {
        log.info("Finding best delivery partner for delivery");

        // Get online delivery partners
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);
        List<DeliveryPartner> onlinePartners = deliveryPartnerRepository.findOnlineDeliveryPartners(cutoffTime);

        if (onlinePartners.isEmpty()) {
            log.warn("No online delivery partners available");
            return null;
        }

        // If location data is available, find partners within radius
        if (restaurantLatitude != null && restaurantLongitude != null) {
            List<DeliveryPartner> nearbyPartners = deliveryPartnerRepository
                .findDeliveryPartnersWithinRadius(restaurantLatitude, restaurantLongitude, 10.0);
            
            if (!nearbyPartners.isEmpty()) {
                onlinePartners = nearbyPartners;
            }
        }

        // Score and rank delivery partners
        DeliveryPartner bestPartner = onlinePartners.stream()
            .filter(DeliveryPartner::canAcceptDelivery)
            .max(Comparator.comparing(this::calculatePartnerScore))
            .orElse(null);

        if (bestPartner != null) {
            log.info("Best delivery partner found: {} with score: {}", 
                bestPartner.getId(), calculatePartnerScore(bestPartner));
            return bestPartner.getId();
        }

        log.warn("No suitable delivery partner found");
        return null;
    }

    /**
     * Find delivery partners within radius sorted by score
     */
    public List<Long> findDeliveryPartnersWithinRadius(Double latitude, Double longitude, Double radiusKm) {
        List<DeliveryPartner> partners = deliveryPartnerRepository
            .findDeliveryPartnersWithinRadius(latitude, longitude, radiusKm);

        return partners.stream()
            .filter(DeliveryPartner::canAcceptDelivery)
            .sorted(Comparator.comparing(this::calculatePartnerScore).reversed())
            .map(DeliveryPartner::getId)
            .toList();
    }

    /**
     * Calculate delivery partner score for assignment priority
     */
    private double calculatePartnerScore(DeliveryPartner partner) {
        double score = 0.0;

        // Rating score (0-50 points)
        if (partner.getRating() != null) {
            score += (partner.getRating() / 5.0) * 50;
        }

        // Success rate score (0-30 points)
        double successRate = partner.getSuccessRate();
        score += (successRate / 100.0) * 30;

        // Experience score (0-20 points)
        int totalDeliveries = partner.getTotalDeliveries();
        if (totalDeliveries > 0) {
            double experienceScore = Math.min(totalDeliveries / 100.0, 1.0) * 20;
            score += experienceScore;
        }

        // Availability bonus (0-10 points)
        if (partner.getIsAvailable() && partner.getIsActive()) {
            score += 10;
        }

        // Recent activity bonus (0-5 points)
        if (partner.getLastActiveAt() != null) {
            long minutesSinceActive = java.time.Duration.between(
                partner.getLastActiveAt(), LocalDateTime.now()).toMinutes();
            if (minutesSinceActive <= 5) {
                score += 5;
            } else if (minutesSinceActive <= 15) {
                score += 2;
            }
        }

        return score;
    }

    /**
     * Calculate distance between two points using Haversine formula
     */
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return Double.MAX_VALUE;
        }

        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in km
    }

    /**
     * Check if delivery partner is suitable for delivery distance
     */
    private boolean isSuitableForDistance(DeliveryPartner partner, double distanceKm) {
        return distanceKm <= partner.getVehicleType().getMaxDeliveryRadius();
    }
}
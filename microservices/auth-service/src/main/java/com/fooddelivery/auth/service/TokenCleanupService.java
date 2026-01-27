package com.fooddelivery.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service for scheduled token cleanup tasks
 */
@Service
public class TokenCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(TokenCleanupService.class);

    @Autowired
    private AuthService authService;

    /**
     * Clean up expired refresh tokens every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupExpiredTokens() {
        logger.info("Starting scheduled cleanup of expired tokens");
        try {
            authService.cleanupExpiredTokens();
            logger.info("Completed scheduled cleanup of expired tokens");
        } catch (Exception e) {
            logger.error("Error during scheduled token cleanup", e);
        }
    }
}
package com.fooddelivery.notification;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic integration test for Notification Service Application
 */
@SpringBootTest
@ActiveProfiles("test")
class NotificationServiceApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring application context loads successfully
        // It validates that all beans are properly configured and dependencies are resolved
    }
}
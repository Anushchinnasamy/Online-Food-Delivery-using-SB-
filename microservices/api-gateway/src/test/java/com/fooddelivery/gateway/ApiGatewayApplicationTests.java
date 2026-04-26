package com.fooddelivery.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Basic integration test for API Gateway application
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379",
    "jwt.secret=testSecretKey123456789012345678901234567890123456789012345678901234567890"
})
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
        // This test ensures that the Spring context loads successfully
        // and all beans are properly configured
    }
}
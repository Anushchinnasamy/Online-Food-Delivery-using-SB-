package com.fooddelivery.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Delivery Service Application
 * Handles delivery partner management, delivery assignment, and delivery tracking
 */
@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
public class DeliveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
    }
}
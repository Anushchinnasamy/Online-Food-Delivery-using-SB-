package com.fooddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Food Delivery Platform
 * 
 * Features:
 * - Complete food ordering system
 * - Multi-role authentication (Customer, Restaurant, Delivery, Admin)
 * - Real-time order tracking
 * - Payment processing
 * - Analytics and reporting
 */
@SpringBootApplication
@EnableJpaAuditing
public class FoodDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryApplication.class, args);
        System.out.println("🍕 Food Delivery Platform Started Successfully!");
        System.out.println("📍 API Base URL: http://localhost:8080/api");
        System.out.println("🔐 Default Admin: admin/admin");
    }
}
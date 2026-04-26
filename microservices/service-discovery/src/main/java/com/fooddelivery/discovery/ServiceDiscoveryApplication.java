package com.fooddelivery.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Service Discovery Server using Netflix Eureka
 * Provides service registration and discovery for the Food Delivery Platform
 */
@SpringBootApplication
@EnableEurekaServer
public class ServiceDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDiscoveryApplication.class, args);
        System.out.println("🔍 Service Discovery Server Started Successfully!");
        System.out.println("📍 Eureka Dashboard: http://localhost:8761");
    }
}
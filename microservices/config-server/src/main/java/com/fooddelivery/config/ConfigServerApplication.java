package com.fooddelivery.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Centralized Configuration Server
 * Manages configuration for all microservices in the Food Delivery Platform
 */
@SpringBootApplication
@EnableConfigServer
@EnableEurekaClient
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
        System.out.println("⚙️ Config Server Started Successfully!");
        System.out.println("📍 Config Server: http://localhost:8888");
    }
}
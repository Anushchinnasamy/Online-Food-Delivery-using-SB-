package com.fooddelivery.menu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for Menu Service
 */
@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
public class MenuServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MenuServiceApplication.class, args);
    }
}
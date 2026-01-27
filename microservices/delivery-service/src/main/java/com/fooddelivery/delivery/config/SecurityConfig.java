package com.fooddelivery.delivery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Delivery Service
 * Assumes JWT validation is handled by API Gateway
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/delivery-partners/health").permitAll()
                .requestMatchers("/deliveries/health").permitAll()
                
                // Platform admin endpoints (delivery partner management)
                .requestMatchers("/delivery-partners/**").hasRole("PLATFORM_ADMIN")
                
                // System/Platform endpoints (delivery assignment)
                .requestMatchers("/deliveries/assign").hasAnyRole("SYSTEM", "PLATFORM_ADMIN")
                .requestMatchers("/deliveries/*/cancel").hasAnyRole("SYSTEM", "PLATFORM_ADMIN")
                .requestMatchers("/deliveries/status/**").hasRole("PLATFORM_ADMIN")
                
                // Delivery partner endpoints
                .requestMatchers("/deliveries/assigned").hasRole("DELIVERY_PARTNER")
                .requestMatchers("/deliveries/active").hasRole("DELIVERY_PARTNER")
                .requestMatchers("/deliveries/history").hasRole("DELIVERY_PARTNER")
                .requestMatchers("/deliveries/*/accept").hasRole("DELIVERY_PARTNER")
                .requestMatchers("/deliveries/*/reject").hasRole("DELIVERY_PARTNER")
                .requestMatchers("/deliveries/*/status").hasRole("DELIVERY_PARTNER")
                
                // General delivery info (accessible by multiple roles)
                .requestMatchers("/deliveries/*").hasAnyRole("DELIVERY_PARTNER", "PLATFORM_ADMIN", "CUSTOMER")
                .requestMatchers("/deliveries/order/*").hasAnyRole("CUSTOMER", "RESTAURANT_ADMIN", "PLATFORM_ADMIN")
                .requestMatchers("/deliveries/*/summary").hasAnyRole("CUSTOMER", "RESTAURANT_ADMIN", "PLATFORM_ADMIN")
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
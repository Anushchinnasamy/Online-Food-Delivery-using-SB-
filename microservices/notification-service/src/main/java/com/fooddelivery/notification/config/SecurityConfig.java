package com.fooddelivery.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Notification Service
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
                .requestMatchers("/notifications/health").permitAll()
                
                // System/Platform endpoints (send notifications)
                .requestMatchers("/notifications").hasAnyRole("SYSTEM", "PLATFORM_ADMIN")
                .requestMatchers("/notifications/bulk").hasAnyRole("SYSTEM", "PLATFORM_ADMIN")
                
                // Admin endpoints (view & retry notifications)
                .requestMatchers("/notifications/stats").hasRole("ADMIN")
                .requestMatchers("/notifications/delayed").hasRole("ADMIN")
                .requestMatchers("/notifications/status/**").hasRole("ADMIN")
                .requestMatchers("/notifications/event/**").hasRole("ADMIN")
                .requestMatchers("/notifications/*/retry").hasRole("ADMIN")
                .requestMatchers("/notifications").hasRole("ADMIN")
                
                // User endpoints (view own notifications)
                .requestMatchers("/notifications/user/**").hasAnyRole("USER", "CUSTOMER", "DELIVERY_PARTNER", "RESTAURANT_ADMIN")
                
                // General notification access
                .requestMatchers("/notifications/*").hasAnyRole("USER", "CUSTOMER", "DELIVERY_PARTNER", "RESTAURANT_ADMIN", "ADMIN")
                .requestMatchers("/notifications/reference/**").hasAnyRole("USER", "CUSTOMER", "DELIVERY_PARTNER", "RESTAURANT_ADMIN", "ADMIN")
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
package com.fooddelivery.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Payment Service
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
                .requestMatchers("/payments/health").permitAll()
                .requestMatchers("/refunds/health").permitAll()
                
                // Webhook endpoints (should be secured with API keys in production)
                .requestMatchers("/payments/status").permitAll()
                .requestMatchers("/refunds/*/status").permitAll()
                
                // Admin endpoints (status queries)
                .requestMatchers("/payments/status/**").hasRole("ADMIN")
                .requestMatchers("/refunds/status/**").hasRole("ADMIN")
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
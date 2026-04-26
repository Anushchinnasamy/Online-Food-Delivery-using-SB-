package com.fooddelivery.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Order Service
 * Trusts API Gateway for authentication - no JWT validation here
 * CORS is handled by API Gateway
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Security filter chain configuration
     * Since JWT validation is handled by API Gateway, we trust the gateway
     * and allow all authenticated requests through
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints (accessible without authentication)
                .requestMatchers(HttpMethod.GET, "/orders/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                
                // All other requests are allowed - API Gateway handles authentication
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
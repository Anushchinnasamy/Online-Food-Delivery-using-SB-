package com.fooddelivery.menu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Menu Service
 * Assumes JWT validation is handled by API Gateway
 * CORS is handled by API Gateway - no CORS configuration needed here
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Security filter chain configuration
     * Since JWT validation is handled by API Gateway, we allow most requests
     * CORS is handled by API Gateway, so no CORS configuration needed
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers(HttpMethod.GET, "/menus/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                
                // Allow all menu endpoints - API Gateway handles authentication for protected operations
                .requestMatchers("/menus/**").permitAll()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
package com.fooddelivery.restaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for Restaurant Service
 * Assumes JWT validation is handled by API Gateway
 * CORS is handled by API Gateway - no CORS configuration needed here
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Security filter chain configuration
     * Since JWT validation is handled by API Gateway, we configure role-based access
     * CORS is handled by API Gateway, so no CORS configuration needed
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints (accessible without authentication)
                .requestMatchers(HttpMethod.GET, "/restaurants/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                
                // Customer endpoints (read-only access) - now public via API Gateway
                .requestMatchers(HttpMethod.GET, "/restaurants").permitAll()
                .requestMatchers(HttpMethod.GET, "/restaurants/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/restaurants/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/restaurants/city/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/restaurants/cuisine/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/restaurants/cities").permitAll()
                .requestMatchers(HttpMethod.GET, "/restaurants/cuisines").permitAll()
                
                // Restaurant Admin endpoints (require RESTAURANT_ADMIN role)
                .requestMatchers(HttpMethod.POST, "/restaurants").hasRole("RESTAURANT_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/restaurants/{id}").hasRole("RESTAURANT_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/restaurants/{id}/status").hasRole("RESTAURANT_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/restaurants/{id}").hasRole("RESTAURANT_ADMIN")
                
                // Platform Admin endpoints (require PLATFORM_ADMIN role)
                .requestMatchers(HttpMethod.PUT, "/restaurants/{id}/approve").hasRole("PLATFORM_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/restaurants/{id}/suspend").hasRole("PLATFORM_ADMIN")
                .requestMatchers(HttpMethod.GET, "/restaurants/admin/**").hasRole("PLATFORM_ADMIN")
                .requestMatchers(HttpMethod.GET, "/restaurants/{id}/admin").hasRole("PLATFORM_ADMIN")
                
                // Internal service endpoints (require authentication)
                .requestMatchers(HttpMethod.PUT, "/restaurants/{id}/rating").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
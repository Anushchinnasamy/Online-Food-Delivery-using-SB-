package com.fooddelivery.config;

import com.fooddelivery.security.JwtAuthenticationEntryPoint;
import com.fooddelivery.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the Food Delivery Platform
 * Configures JWT authentication, CORS, and role-based authorization
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Password encoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication manager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * DAO authentication provider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * CORS configuration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Use allowedOriginPatterns instead of allowedOrigins when allowCredentials is true
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:8080",
            "http://127.0.0.1:5173",
            "http://127.0.0.1:8080"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Security filter chain configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                
                // Swagger/OpenAPI endpoints
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // Restaurant browsing (public) - FIXED
                .requestMatchers(HttpMethod.GET, "/restaurants").permitAll()
                .requestMatchers(HttpMethod.GET, "/restaurants/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/restaurants/search").permitAll()
                
                // Menu items browsing (public) - FIXED
                .requestMatchers(HttpMethod.GET, "/menu-items/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/menu-items/restaurant/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/menu-items/search").permitAll()
                
                // Customer endpoints
                .requestMatchers(HttpMethod.GET, "/orders/my").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.GET, "/orders/*").hasAnyRole("CUSTOMER", "RESTAURANT_ADMIN", "PLATFORM_ADMIN")
                .requestMatchers(HttpMethod.POST, "/orders").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.PUT, "/orders/*/cancel").hasRole("CUSTOMER")
                .requestMatchers(HttpMethod.POST, "/payments").hasRole("CUSTOMER")
                
                // Restaurant admin endpoints
                .requestMatchers(HttpMethod.GET, "/restaurants/my").hasRole("RESTAURANT_ADMIN")
                .requestMatchers(HttpMethod.POST, "/restaurants").hasRole("RESTAURANT_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/restaurants/*").hasRole("RESTAURANT_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/restaurants/*").hasRole("RESTAURANT_ADMIN")
                .requestMatchers(HttpMethod.GET, "/orders/restaurant/*").hasRole("RESTAURANT_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/orders/*/status").hasRole("RESTAURANT_ADMIN")
                
                // Menu item management (Restaurant admin)
                .requestMatchers(HttpMethod.POST, "/menu-items").hasRole("RESTAURANT_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/menu-items/*").hasRole("RESTAURANT_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/menu-items/*").hasRole("RESTAURANT_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/menu-items/*/toggle-availability").hasRole("RESTAURANT_ADMIN")
                
                // Delivery partner endpoints
                .requestMatchers("/delivery/**").hasRole("DELIVERY_PARTNER")
                
                // Platform admin endpoints
                .requestMatchers("/admin/**").hasRole("PLATFORM_ADMIN")
                .requestMatchers(HttpMethod.GET, "/users").hasRole("PLATFORM_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/users/*/status").hasRole("PLATFORM_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/restaurants/*/approve").hasRole("PLATFORM_ADMIN")
                .requestMatchers(HttpMethod.GET, "/orders/status/*").hasRole("PLATFORM_ADMIN")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
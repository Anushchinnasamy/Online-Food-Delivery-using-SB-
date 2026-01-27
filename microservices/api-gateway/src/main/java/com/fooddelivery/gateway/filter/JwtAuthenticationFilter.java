package com.fooddelivery.gateway.filter;

import com.fooddelivery.gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * JWT Authentication Filter for API Gateway
 * Validates JWT tokens for protected routes
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    // Public endpoints that don't require authentication
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
        "/auth/login",
        "/auth/register",
        "/auth/refresh",
        "/auth/health",
        "/actuator/health"
    );

    // Role-based access control mapping
    private static final List<String> ADMIN_ONLY_ENDPOINTS = List.of(
        "/restaurants/admin",
        "/delivery-partners/admin",
        "/notifications/admin"
    );

    private static final List<String> RESTAURANT_ADMIN_ENDPOINTS = List.of(
        "/restaurants/",
        "/menu-items/"
    );

    private static final List<String> DELIVERY_PARTNER_ENDPOINTS = List.of(
        "/deliveries/assigned",
        "/deliveries/accept",
        "/deliveries/status"
    );

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            logger.debug("Processing request for path: {}", path);

            // Skip authentication for public endpoints
            if (isPublicEndpoint(path)) {
                logger.debug("Public endpoint, skipping authentication: {}", path);
                return chain.filter(exchange);
            }

            // Extract JWT token from Authorization header
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header for path: {}", path);
                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                // Validate JWT token
                if (!jwtUtil.validateToken(token)) {
                    logger.warn("Invalid JWT token for path: {}", path);
                    return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                }

                // Extract user information from token
                String userId = jwtUtil.extractUserId(token);
                String userRole = jwtUtil.extractRole(token);
                String userEmail = jwtUtil.extractUsername(token);

                // Check role-based access control
                if (!hasRequiredRole(path, userRole)) {
                    logger.warn("Access denied for user {} with role {} to path: {}", userEmail, userRole, path);
                    return onError(exchange, "Access denied - insufficient privileges", HttpStatus.FORBIDDEN);
                }

                // Add user information to request headers for downstream services
                ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Role", "ROLE_" + userRole) // Add ROLE_ prefix for Spring Security
                    .header("X-User-Email", userEmail)
                    .build();

                logger.debug("Authentication successful for user: {} with role: {}", userEmail, userRole);

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                logger.error("JWT validation error for path: {}", path, e);
                return onError(exchange, "Token validation failed", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    /**
     * Check if the endpoint is public (doesn't require authentication)
     */
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    /**
     * Check if user has required role for the endpoint
     */
    private boolean hasRequiredRole(String path, String userRole) {
        // Platform admin has access to everything
        if ("PLATFORM_ADMIN".equals(userRole)) {
            return true;
        }

        // Check admin-only endpoints
        if (ADMIN_ONLY_ENDPOINTS.stream().anyMatch(path::contains)) {
            return "PLATFORM_ADMIN".equals(userRole);
        }

        // Check restaurant admin endpoints (POST, PUT, DELETE operations)
        if (RESTAURANT_ADMIN_ENDPOINTS.stream().anyMatch(path::startsWith)) {
            return "RESTAURANT_ADMIN".equals(userRole) || "PLATFORM_ADMIN".equals(userRole);
        }

        // Check delivery partner endpoints
        if (DELIVERY_PARTNER_ENDPOINTS.stream().anyMatch(path::contains)) {
            return "DELIVERY_PARTNER".equals(userRole) || "PLATFORM_ADMIN".equals(userRole);
        }

        // Default: allow access for authenticated users (CUSTOMER role and others)
        return true;
    }

    /**
     * Handle authentication errors
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");
        
        String body = String.format("{\"error\":\"%s\",\"status\":%d,\"timestamp\":%d}", 
            message, httpStatus.value(), System.currentTimeMillis());
        
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    /**
     * Configuration class for the filter
     */
    public static class Config {
        // Configuration properties can be added here if needed
    }
}
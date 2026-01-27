package com.fooddelivery.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway configuration for additional settings
 */
@Configuration
public class GatewayConfig {

    /**
     * Rate limiting key resolver - limits by user ID if available, otherwise by IP
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                // Try to get user ID from header (set by JWT filter)
                String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
                if (userId != null && !userId.isEmpty()) {
                    return Mono.just("user:" + userId);
                }
                
                // Fallback to IP address
                String clientIp = getClientIp(exchange);
                return Mono.just("ip:" + clientIp);
            }
        };
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIp(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return exchange.getRequest().getRemoteAddress() != null 
            ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
            : "unknown";
    }
}
package com.fooddelivery.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Global request logging filter
 * Logs all incoming requests and outgoing responses for monitoring and debugging
 */
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String START_TIME_ATTR = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Generate request ID if not present
        String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        // Add request ID to response headers
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add(REQUEST_ID_HEADER, requestId);

        // Log incoming request
        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put(START_TIME_ATTR, startTime);

        String clientIp = getClientIp(request);
        String userAgent = request.getHeaders().getFirst("User-Agent");
        String userId = request.getHeaders().getFirst("X-User-Id");

        logger.info("Incoming Request - ID: {}, Method: {}, URI: {}, IP: {}, User: {}, UserAgent: {}, Time: {}", 
            requestId, 
            request.getMethod(), 
            request.getURI(), 
            clientIp,
            userId != null ? userId : "anonymous",
            userAgent != null ? userAgent.substring(0, Math.min(userAgent.length(), 100)) : "unknown",
            LocalDateTime.now()
        );

        // Create final variables for lambda
        final String finalRequestId = requestId;
        final ServerHttpRequest finalRequest = request;

        // Continue with the filter chain and log response
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTimeAttr = exchange.getAttribute(START_TIME_ATTR);
            long endTime = System.currentTimeMillis();
            long duration = startTimeAttr != null ? endTime - startTimeAttr : 0;

            logger.info("Outgoing Response - ID: {}, Status: {}, Duration: {}ms, Time: {}", 
                finalRequestId, 
                response.getStatusCode(), 
                duration,
                LocalDateTime.now()
            );

            // Log slow requests (> 2 seconds)
            if (duration > 2000) {
                logger.warn("Slow Request Detected - ID: {}, Duration: {}ms, URI: {}", 
                    finalRequestId, duration, finalRequest.getURI());
            }
        }));
    }

    /**
     * Extract client IP address from request
     */
    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddress() != null 
            ? request.getRemoteAddress().getAddress().getHostAddress()
            : "unknown";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
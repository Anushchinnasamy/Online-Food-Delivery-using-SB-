package com.fooddelivery.gateway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Global exception handler for API Gateway
 * Handles all unhandled exceptions and provides consistent error responses
 */
@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // Set content type
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        HttpStatus status;
        String message;

        if (ex instanceof ResponseStatusException) {
            ResponseStatusException rse = (ResponseStatusException) ex;
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            message = rse.getReason();
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            message = "Invalid request: " + ex.getMessage();
        } else if (ex instanceof SecurityException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Authentication failed: " + ex.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Internal server error occurred";
            logger.error("Unhandled exception in API Gateway", ex);
        }

        response.setStatusCode(status);

        String errorResponse = createErrorResponse(status, message, exchange.getRequest().getPath().value());
        DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes());

        return response.writeWith(Mono.just(buffer));
    }

    /**
     * Create standardized error response JSON
     */
    private String createErrorResponse(HttpStatus status, String message, String path) {
        return String.format(
            "{\"error\":\"%s\",\"message\":\"%s\",\"status\":%d,\"path\":\"%s\",\"timestamp\":\"%s\"}",
            status.getReasonPhrase(),
            message,
            status.value(),
            path,
            LocalDateTime.now()
        );
    }
}
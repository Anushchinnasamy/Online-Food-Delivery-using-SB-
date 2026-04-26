# API Gateway - Food Delivery Platform

## Overview

The API Gateway serves as the single entry point for all client requests in the Food Delivery Platform. It handles routing, authentication, authorization, CORS, rate limiting, circuit breaking, and request logging.

## Features

- **Request Routing**: Routes requests to appropriate microservices
- **JWT Authentication**: Validates JWT tokens and extracts user information
- **Role-Based Access Control**: Enforces role-based permissions
- **CORS Support**: Handles cross-origin requests from frontend applications
- **Circuit Breaker**: Provides fallback responses when services are unavailable
- **Rate Limiting**: Prevents abuse with configurable rate limits
- **Request Logging**: Comprehensive logging for monitoring and debugging
- **Health Checks**: Multiple health check endpoints for monitoring

## Architecture

```
Frontend Applications
        ↓
   API Gateway (Port 8080)
        ↓
┌─────────────────────────────────────┐
│  Auth Service      (Port 8081)     │
│  Restaurant Service (Port 8082)    │
│  Menu Service      (Port 8083)     │
│  Order Service     (Port 8084)     │
│  Payment Service   (Port 8085)     │
│  Delivery Service  (Port 8086)     │
│  Notification Service (Port 8087)  │
└─────────────────────────────────────┘
```

## Routes Configuration

### Public Routes (No Authentication Required)
- `POST /auth/login` → Auth Service
- `POST /auth/register` → Auth Service
- `POST /auth/refresh` → Auth Service
- `GET /auth/health` → Auth Service
- `GET /health` → API Gateway Health
- `GET /actuator/health` → API Gateway Actuator

### Protected Routes (JWT Authentication Required)

#### Restaurant Service
- `GET /restaurants/**` → Restaurant Service
- `POST /restaurants/**` → Restaurant Service (RESTAURANT_ADMIN, PLATFORM_ADMIN)
- `PUT /restaurants/**` → Restaurant Service (RESTAURANT_ADMIN, PLATFORM_ADMIN)
- `DELETE /restaurants/**` → Restaurant Service (RESTAURANT_ADMIN, PLATFORM_ADMIN)

#### Menu Service
- `GET /menu-items/**` → Menu Service
- `POST /menu-items/**` → Menu Service (RESTAURANT_ADMIN, PLATFORM_ADMIN)
- `PUT /menu-items/**` → Menu Service (RESTAURANT_ADMIN, PLATFORM_ADMIN)
- `DELETE /menu-items/**` → Menu Service (RESTAURANT_ADMIN, PLATFORM_ADMIN)

#### Order Service
- `GET /orders/**` → Order Service
- `POST /orders/**` → Order Service
- `PUT /orders/**` → Order Service

#### Payment Service
- `GET /payments/**` → Payment Service
- `POST /payments/**` → Payment Service
- `GET /refunds/**` → Payment Service
- `POST /refunds/**` → Payment Service

#### Delivery Service
- `GET /deliveries/**` → Delivery Service
- `POST /deliveries/**` → Delivery Service
- `GET /delivery-partners/**` → Delivery Service
- `POST /delivery-partners/**` → Delivery Service (PLATFORM_ADMIN)

#### Notification Service
- `GET /notifications/**` → Notification Service
- `POST /notifications/**` → Notification Service

## Role-Based Access Control

### User Roles
- **CUSTOMER**: Default role for end users
- **RESTAURANT_ADMIN**: Restaurant owners and managers
- **DELIVERY_PARTNER**: Delivery personnel
- **PLATFORM_ADMIN**: System administrators

### Access Control Rules
1. **PLATFORM_ADMIN**: Full access to all endpoints
2. **RESTAURANT_ADMIN**: Access to restaurant and menu management
3. **DELIVERY_PARTNER**: Access to delivery-related endpoints
4. **CUSTOMER**: Access to ordering and general endpoints

## Configuration

### Application Properties (application.yml)

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        # Service routes configuration
      globalcors:
        # CORS configuration
      default-filters:
        # Rate limiting and retry configuration

jwt:
  secret: mySecretKey123456789012345678901234567890123456789012345678901234567890

# Circuit breaker, logging, and other configurations
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Gateway port | 8080 |
| `JWT_SECRET` | JWT signing secret | (from config) |
| `REDIS_HOST` | Redis host for rate limiting | localhost |
| `REDIS_PORT` | Redis port | 6379 |

## Building and Running

### Prerequisites
- Java 17+
- Maven 3.6+
- Redis (optional, for rate limiting)

### Build
```bash
# Windows
build-and-test.bat

# Linux/Mac
mvn clean package
```

### Run
```bash
# Using Maven
mvn spring-boot:run

# Using JAR
java -jar target/api-gateway-1.0.0.jar
```

### Docker (Optional)
```bash
# Build image
docker build -t food-delivery/api-gateway .

# Run container
docker run -p 8080:8080 food-delivery/api-gateway
```

## Testing

### Health Checks
```bash
# Gateway health
curl http://localhost:8080/health

# Readiness probe
curl http://localhost:8080/health/ready

# Liveness probe
curl http://localhost:8080/health/live

# Actuator health
curl http://localhost:8080/actuator/health
```

### Route Testing
```bash
# View all routes
curl http://localhost:8080/actuator/gateway/routes

# Test authentication flow
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'

# Test protected endpoint
curl -X GET http://localhost:8080/restaurants \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### CORS Testing
```bash
# Preflight request
curl -X OPTIONS http://localhost:8080/restaurants \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Authorization"
```

## Monitoring and Observability

### Endpoints
- `/health` - Basic health check
- `/health/ready` - Readiness probe
- `/health/live` - Liveness probe
- `/actuator/health` - Detailed health information
- `/actuator/gateway/routes` - Current route configuration
- `/actuator/metrics` - Application metrics

### Logging
The gateway provides comprehensive request/response logging:
- Request ID tracking
- User identification
- Response times
- Error tracking
- Slow request detection

### Circuit Breaker
Circuit breakers are configured for all downstream services:
- Failure threshold: 50%
- Minimum calls: 5
- Wait duration: 30 seconds
- Sliding window: 10 requests

## Security

### JWT Validation
- Validates JWT signature using shared secret
- Extracts user information (ID, role, email)
- Passes user context to downstream services via headers

### Headers Added to Downstream Requests
- `X-User-Id`: User identifier
- `X-User-Role`: User role
- `X-User-Email`: User email
- `X-Request-ID`: Unique request identifier

### Rate Limiting
- Default: 100 requests per second per user/IP
- Burst capacity: 200 requests
- Uses Redis for distributed rate limiting

## Troubleshooting

### Common Issues

1. **JWT Validation Failures**
   - Ensure JWT secret matches auth service
   - Check token expiration
   - Verify token format (Bearer prefix)

2. **CORS Issues**
   - Check allowed origins configuration
   - Verify preflight request handling
   - Ensure credentials are properly configured

3. **Circuit Breaker Activation**
   - Check downstream service health
   - Review failure thresholds
   - Monitor service response times

4. **Rate Limiting**
   - Check Redis connectivity
   - Review rate limit configuration
   - Monitor request patterns

### Logs Analysis
```bash
# View gateway logs
docker logs api-gateway

# Filter authentication logs
docker logs api-gateway | grep "Authentication"

# Monitor slow requests
docker logs api-gateway | grep "Slow Request"
```

## Development

### Adding New Routes
1. Update `application.yml` routes configuration
2. Add route to circuit breaker configuration
3. Update role-based access control if needed
4. Test the new route

### Custom Filters
Implement `GatewayFilter` or `GlobalFilter` interfaces:
```java
@Component
public class CustomFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Custom logic
        return chain.filter(exchange);
    }
}
```

## Production Considerations

1. **Security**
   - Use strong JWT secrets
   - Enable HTTPS
   - Configure proper CORS origins
   - Implement API key authentication for service-to-service calls

2. **Performance**
   - Configure appropriate connection pools
   - Tune circuit breaker settings
   - Monitor and adjust rate limits
   - Use Redis cluster for high availability

3. **Monitoring**
   - Set up health check monitoring
   - Configure alerting for circuit breaker trips
   - Monitor response times and error rates
   - Track rate limiting metrics

4. **Scaling**
   - Deploy multiple gateway instances
   - Use load balancer in front of gateways
   - Configure session affinity if needed
   - Monitor resource usage

## API Documentation

For detailed API documentation of downstream services, refer to:
- [Auth Service API](../auth-service/README.md)
- [Restaurant Service API](../restaurant-service/README.md)
- [Menu Service API](../menu-service/README.md)
- [Order Service API](../order-service/README.md)
- [Payment Service API](../payment-service/README.md)
- [Delivery Service API](../delivery-service/README.md)
- [Notification Service API](../notification-service/README.md)
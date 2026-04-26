# Food Delivery Platform - Microservices Architecture

## 🏗️ Architecture Overview

This is a production-ready microservices implementation following real-world standards used by companies like Swiggy and Zomato.

### 🎯 Microservices

1. **API Gateway** (Port 8080) - Single entry point, JWT validation, routing
2. **Service Discovery** (Port 8761) - Eureka Server for service registration
3. **Config Server** (Port 8888) - Centralized configuration management
4. **Auth Service** (Port 8081) - Authentication, JWT tokens, user roles
5. **User Service** (Port 8082) - User profile management
6. **Restaurant Service** (Port 8083) - Restaurant and menu management
7. **Cart Service** (Port 8084) - Shopping cart operations
8. **Order Service** (Port 8085) - Order processing and lifecycle
9. **Payment Service** (Port 8086) - Payment processing and refunds
10. **Delivery Service** (Port 8087) - Delivery assignment and tracking
11. **Reporting Service** (Port 8088) - Analytics and reports

### 🔄 Communication Patterns

- **Synchronous**: REST APIs via OpenFeign
- **Asynchronous**: RabbitMQ for events
- **Security**: JWT tokens validated at gateway
- **Resilience**: Circuit breaker pattern

### 🗄️ Database Strategy

- Each microservice has its own database
- PostgreSQL for transactional data
- Redis for caching
- Event sourcing for order workflows

### 🚀 Getting Started

```bash
# Start infrastructure services
docker-compose up -d

# Start microservices in order:
1. Service Discovery (8761)
2. Config Server (8888)
3. Auth Service (8081)
4. Other services (8082-8088)
5. API Gateway (8080)
```

### 📊 Service Dependencies

```
API Gateway (8080)
├── Auth Service (8081)
├── User Service (8082)
├── Restaurant Service (8083)
├── Cart Service (8084)
├── Order Service (8085)
│   ├── Restaurant Service
│   ├── User Service
│   └── Payment Service
├── Payment Service (8086)
├── Delivery Service (8087)
│   ├── Order Service
│   └── User Service
└── Reporting Service (8088)
    └── All Services
```

### 🔐 Security Model

- JWT tokens issued by Auth Service
- Gateway validates all requests
- Service-to-service communication secured
- Role-based authorization at service level

### 📈 Scalability Features

- Horizontal scaling ready
- Load balancing via gateway
- Database sharding support
- Caching strategies implemented
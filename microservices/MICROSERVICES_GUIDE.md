# 🏗️ Food Delivery Platform - Microservices Architecture Guide

## 📋 Overview

This is a production-ready microservices implementation of a Food Delivery Platform following real-world standards used by companies like Swiggy and Zomato. The architecture is designed for scalability, maintainability, and high availability.

## 🎯 Architecture Principles

### ✅ **Implemented Patterns**
- **Database per Service**: Each microservice has its own database
- **API Gateway Pattern**: Single entry point for all client requests
- **Service Discovery**: Dynamic service registration and discovery
- **Circuit Breaker**: Fault tolerance and resilience
- **CQRS**: Command Query Responsibility Segregation
- **Event Sourcing**: For order and delivery workflows
- **Saga Pattern**: Distributed transaction management
- **JWT Authentication**: Stateless authentication
- **Rate Limiting**: API protection and throttling

### 🔧 **Technology Stack**
- **Framework**: Spring Boot 3.2.0, Spring Cloud 2023.0.0
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Database**: PostgreSQL (separate DB per service)
- **Caching**: Redis
- **Message Queue**: RabbitMQ
- **Monitoring**: Prometheus + Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **Security**: JWT tokens, BCrypt password hashing
- **Documentation**: OpenAPI 3.0 (Swagger)

## 🏢 **Microservices Architecture**

### 🔍 **Service Discovery (Port 8761)**
- **Purpose**: Service registration and discovery
- **Technology**: Netflix Eureka Server
- **Features**:
  - Dynamic service registration
  - Health checking
  - Load balancing support
  - Service metadata management

### ⚙️ **Config Server (Port 8888)**
- **Purpose**: Centralized configuration management
- **Technology**: Spring Cloud Config
- **Features**:
  - Environment-specific configurations
  - Dynamic configuration refresh
  - Git-based configuration storage
  - Encryption support

### 🚪 **API Gateway (Port 8080)**
- **Purpose**: Single entry point for all client requests
- **Technology**: Spring Cloud Gateway
- **Features**:
  - JWT token validation
  - Request routing
  - Rate limiting
  - Circuit breaker integration
  - CORS handling
  - Request/response transformation

### 🔐 **Auth Service (Port 8081)**
- **Purpose**: Authentication and authorization
- **Database**: `auth_service_db`
- **Features**:
  - User registration and login
  - JWT token generation and validation
  - Refresh token management
  - Role-based access control
  - Account lockout protection
  - Password hashing with BCrypt

**Entities:**
- `User` - Authentication data
- `RefreshToken` - Token management

**APIs:**
```
POST /auth/register     - User registration
POST /auth/login        - User authentication
POST /auth/refresh      - Token refresh
GET  /auth/validate     - Token validation
POST /auth/logout       - User logout
POST /auth/change-password - Password change
```

### 👤 **User Service (Port 8082)**
- **Purpose**: User profile management
- **Database**: `user_service_db`
- **Features**:
  - Customer profile management
  - Restaurant admin profiles
  - Delivery partner profiles
  - Address management
  - Profile verification

### 🍽️ **Restaurant Service (Port 8083)**
- **Purpose**: Restaurant and menu management
- **Database**: `restaurant_service_db`
- **Features**:
  - Restaurant onboarding
  - Menu item management
  - Restaurant approval workflow
  - Operating hours management
  - Location-based search

### 🛒 **Cart Service (Port 8084)**
- **Purpose**: Shopping cart operations
- **Database**: In-memory (Redis) + PostgreSQL for persistence
- **Features**:
  - Add/remove items
  - Quantity updates
  - Cart validation
  - Session management
  - Cart abandonment handling

### 📦 **Order Service (Port 8085)**
- **Purpose**: Order processing and lifecycle management
- **Database**: `order_service_db`
- **Features**:
  - Order placement
  - Order status tracking
  - Order history
  - Order cancellation
  - Integration with payment and delivery

### 💳 **Payment Service (Port 8086)**
- **Purpose**: Payment processing
- **Database**: `payment_service_db`
- **Features**:
  - Payment initiation
  - Payment status tracking
  - Refund processing
  - Multiple payment methods
  - Payment gateway integration

### 🚚 **Delivery Service (Port 8087)**
- **Purpose**: Delivery management
- **Database**: `delivery_service_db`
- **Features**:
  - Delivery partner assignment
  - Real-time tracking
  - Delivery status updates
  - Route optimization
  - Delivery history

### 📊 **Reporting Service (Port 8088)**
- **Purpose**: Analytics and reporting
- **Database**: Read replicas + Data warehouse
- **Features**:
  - Sales reports
  - Performance analytics
  - Business intelligence
  - Data aggregation
  - Dashboard APIs

## 🗄️ **Database Strategy**

### **Database per Service**
Each microservice has its own PostgreSQL database:

```
auth_service_db      (Port 5433)
user_service_db      (Port 5434)
restaurant_service_db (Port 5435)
order_service_db     (Port 5436)
payment_service_db   (Port 5437)
delivery_service_db  (Port 5438)
```

### **Data Consistency**
- **Eventual Consistency**: Between services
- **Strong Consistency**: Within service boundaries
- **Saga Pattern**: For distributed transactions
- **Event Sourcing**: For audit trails

## 🔄 **Communication Patterns**

### **Synchronous Communication**
- **REST APIs**: Service-to-service calls
- **OpenFeign**: Declarative REST clients
- **Circuit Breaker**: Fault tolerance

### **Asynchronous Communication**
- **RabbitMQ**: Message queuing
- **Event-Driven**: Domain events
- **Pub/Sub**: Event notifications

### **Data Flow Example: Order Placement**
```
1. Customer → API Gateway → Order Service
2. Order Service → Restaurant Service (validate menu)
3. Order Service → User Service (validate customer)
4. Order Service → Payment Service (process payment)
5. Order Service → Delivery Service (assign delivery)
6. Order Service → Event Bus (order created event)
```

## 🛡️ **Security Implementation**

### **Authentication Flow**
1. User logs in via API Gateway
2. Auth Service validates credentials
3. JWT token generated and returned
4. Subsequent requests include JWT token
5. API Gateway validates token
6. User context passed to downstream services

### **Authorization**
- **Role-Based Access Control (RBAC)**
- **Method-level security**
- **Resource-level permissions**
- **Service-to-service authentication**

### **Security Headers**
```
X-User-Id: User identifier
X-User-Role: User role
X-User-Email: User email
```

## 📈 **Scalability Features**

### **Horizontal Scaling**
- Stateless services
- Load balancing via API Gateway
- Database connection pooling
- Caching strategies

### **Performance Optimization**
- **Redis Caching**: Frequently accessed data
- **Database Indexing**: Optimized queries
- **Connection Pooling**: Efficient resource usage
- **Async Processing**: Non-blocking operations

### **Monitoring & Observability**
- **Prometheus**: Metrics collection
- **Grafana**: Metrics visualization
- **ELK Stack**: Centralized logging
- **Distributed Tracing**: Request tracking
- **Health Checks**: Service monitoring

## 🚀 **Getting Started**

### **Prerequisites**
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 15+
- Redis 7+

### **Quick Start**
```bash
# 1. Clone the repository
git clone <repository-url>
cd microservices

# 2. Start infrastructure services
docker-compose up -d

# 3. Start all microservices
chmod +x start-microservices.sh
./start-microservices.sh

# 4. Access the application
# API Gateway: http://localhost:8080
# Service Discovery: http://localhost:8761
```

### **Service Startup Order**
1. **Infrastructure**: PostgreSQL, Redis, RabbitMQ
2. **Service Discovery** (8761)
3. **Config Server** (8888)
4. **Auth Service** (8081)
5. **Business Services** (8082-8088)
6. **API Gateway** (8080)

### **Health Checks**
```bash
# Check all services
curl http://localhost:8080/actuator/health

# Check specific service
curl http://localhost:8081/actuator/health
```

## 🧪 **Testing**

### **Demo Accounts**
All accounts use password: `password123`

| Role | Email | Purpose |
|------|-------|---------|
| Platform Admin | admin@fooddelivery.com | System administration |
| Restaurant Admin | raj@spicepalace.com | Restaurant management |
| Delivery Partner | ravi.delivery@gmail.com | Delivery operations |
| Customer | john.doe@gmail.com | Food ordering |

### **API Testing**
```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fooddelivery.com","password":"password123"}'

# Get restaurants
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/restaurants

# Place order
curl -X POST http://localhost:8080/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"restaurantId":1,"items":[{"menuItemId":1,"quantity":2}]}'
```

## 📊 **Monitoring & Management**

### **Service Discovery Dashboard**
- URL: http://localhost:8761
- View registered services
- Monitor service health

### **RabbitMQ Management**
- URL: http://localhost:15672
- Username: admin
- Password: password

### **Grafana Dashboard**
- URL: http://localhost:3000
- Username: admin
- Password: admin

### **Kibana Logs**
- URL: http://localhost:5601
- View centralized logs
- Search and analyze

## 🔧 **Configuration Management**

### **Environment-Specific Configs**
```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/auth_service_db

# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://prod-db:5432/auth_service_db
```

### **Dynamic Configuration Refresh**
```bash
# Refresh configuration
curl -X POST http://localhost:8081/actuator/refresh
```

## 🚨 **Troubleshooting**

### **Common Issues**

1. **Service Not Registering with Eureka**
   - Check Eureka server is running
   - Verify network connectivity
   - Check application.yml configuration

2. **Database Connection Issues**
   - Ensure PostgreSQL is running
   - Verify connection parameters
   - Check database exists

3. **JWT Token Issues**
   - Verify JWT secret consistency
   - Check token expiration
   - Validate token format

### **Log Locations**
```
microservices/logs/
├── service-discovery.log
├── config-server.log
├── auth-service.log
├── user-service.log
├── restaurant-service.log
├── cart-service.log
├── order-service.log
├── payment-service.log
├── delivery-service.log
├── reporting-service.log
└── api-gateway.log
```

### **Useful Commands**
```bash
# View service logs
tail -f logs/auth-service.log

# Check service status
curl http://localhost:8081/actuator/health

# View Eureka registered services
curl http://localhost:8761/eureka/apps

# Stop all services
./stop-microservices.sh

# Clean restart
docker-compose down -v
./start-microservices.sh
```

## 🎯 **Production Deployment**

### **Docker Images**
Each service can be containerized:
```dockerfile
FROM openjdk:17-jre-slim
COPY target/auth-service-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### **Kubernetes Deployment**
- Helm charts for easy deployment
- ConfigMaps for configuration
- Secrets for sensitive data
- Ingress for external access

### **CI/CD Pipeline**
- Automated testing
- Docker image building
- Deployment automation
- Rolling updates

## 📚 **API Documentation**

Each service exposes OpenAPI documentation:
- Auth Service: http://localhost:8081/swagger-ui.html
- User Service: http://localhost:8082/swagger-ui.html
- Restaurant Service: http://localhost:8083/swagger-ui.html

## 🤝 **Contributing**

1. Follow microservice boundaries
2. Implement proper error handling
3. Add comprehensive tests
4. Update documentation
5. Follow coding standards

## 📄 **License**

This project is licensed under the MIT License.

---

**🎉 Congratulations! You now have a production-ready microservices architecture for a Food Delivery Platform!**
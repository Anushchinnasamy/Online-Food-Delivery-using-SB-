# Payment Service

A standalone Spring Boot microservice for handling payment processing, status tracking, and refunds in the food delivery platform.

## Overview

The Payment Service is responsible for:
- Payment initiation and processing
- Payment status tracking and updates
- Refund processing and management
- Integration with payment gateways
- Payment history and reporting

## Features

### Payment Management
- **Payment Initiation**: Create new payment transactions
- **Multiple Payment Methods**: Support for UPI, Card, Wallet, and COD
- **Status Tracking**: Real-time payment status updates
- **Gateway Integration**: Simulated payment gateway processing
- **Webhook Support**: Handle payment gateway callbacks

### Refund Management
- **Refund Processing**: Full and partial refunds
- **Refund Status Tracking**: Monitor refund progress
- **Multiple Refund Sources**: Support refunds initiated by users, restaurants, or admins
- **Refund History**: Complete refund audit trail

### Security & Compliance
- **Role-based Access Control**: Different permissions for users, restaurants, and admins
- **Transaction Security**: Secure transaction ID generation
- **Audit Trail**: Complete payment and refund history
- **Data Validation**: Comprehensive input validation

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA with Hibernate
- **Mapping**: MapStruct
- **Service Discovery**: Netflix Eureka
- **Inter-service Communication**: OpenFeign
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test

## API Endpoints

### Payment Endpoints

#### Create Payment
```http
POST /payments
Content-Type: application/json
X-User-Id: {userId}

{
  "orderId": 1,
  "amount": 299.50,
  "paymentMethod": "UPI",
  "currency": "INR",
  "metadata": "Additional payment info"
}
```

#### Get Payment Details
```http
GET /payments/{transactionId}
```

#### Get Payment Summary with Refunds
```http
GET /payments/{transactionId}/summary
```

#### Get Payments by Order
```http
GET /payments/order/{orderId}
```

#### Get User Payments
```http
GET /payments/user/{userId}?page=0&size=20
```

#### Update Payment Status (Webhook)
```http
PUT /payments/status
Content-Type: application/json

{
  "transactionId": "PAY_1234567890_ABCD1234",
  "paymentStatus": "SUCCESS",
  "gatewayTransactionId": "GW_1234567890_5678",
  "gatewayName": "RazorPay",
  "gatewayResponse": "Payment processed successfully"
}
```

### Refund Endpoints

#### Create Refund
```http
POST /refunds
Content-Type: application/json

{
  "paymentTransactionId": "PAY_1234567890_ABCD1234",
  "refundAmount": 150.00,
  "refundReason": "Order cancelled by customer",
  "initiatedBy": "USER"
}
```

#### Get Refund Details
```http
GET /refunds/{refundTransactionId}
```

#### Get Refunds by Payment
```http
GET /refunds/payment/{paymentTransactionId}
```

#### Get User Refunds
```http
GET /refunds/user/{userId}?page=0&size=20
```

## Database Schema

### Payments Table
- `id` (Primary Key)
- `transaction_id` (Unique)
- `order_id`
- `user_id`
- `amount`
- `payment_method` (UPI, CARD, WALLET, COD)
- `payment_status` (INITIATED, PROCESSING, SUCCESS, FAILED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED)
- `gateway_transaction_id`
- `gateway_name`
- `gateway_response`
- `failure_reason`
- `processed_at`
- `refunded_amount`
- `currency`
- `metadata`
- `created_at`
- `updated_at`

### Refunds Table
- `id` (Primary Key)
- `refund_transaction_id` (Unique)
- `payment_id` (Foreign Key)
- `refund_amount`
- `refund_status` (INITIATED, PROCESSING, SUCCESS, FAILED, CANCELLED)
- `refund_reason`
- `gateway_refund_id`
- `gateway_response`
- `failure_reason`
- `processed_at`
- `initiated_by`
- `metadata`
- `created_at`
- `updated_at`

## Configuration

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/payment_service_db
    username: payment_user
    password: payment_password
```

### Service Discovery
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### Custom Properties
```yaml
payment:
  gateway:
    timeout: 30000
    retry-attempts: 3
  cleanup:
    pending-payment-timeout-minutes: 30
    pending-refund-timeout-hours: 24
```

## Running the Service

### Prerequisites
- Java 17
- Maven 3.6+
- PostgreSQL 12+
- Eureka Server (for service discovery)

### Local Development
1. **Start PostgreSQL** and create database:
   ```sql
   CREATE DATABASE payment_service_db;
   CREATE USER payment_user WITH PASSWORD 'payment_password';
   GRANT ALL PRIVILEGES ON DATABASE payment_service_db TO payment_user;
   ```

2. **Start Eureka Server** (if not already running):
   ```bash
   cd ../service-discovery
   mvn spring-boot:run
   ```

3. **Build and run the service**:
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

4. **Verify service is running**:
   ```bash
   curl http://localhost:8085/payments/health
   ```

### Using Build Script
```bash
# Make script executable
chmod +x build-and-test.bat

# Run build and test
./build-and-test.bat
```

## Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
The service includes:
- Unit tests for service layer
- Integration tests for repositories
- Controller tests with MockMvc
- End-to-end API tests

## Payment Gateway Integration

The service includes a simulated payment gateway for demonstration purposes. In production, you would integrate with real payment gateways like:

- **Razorpay**: For UPI, cards, and wallets
- **Stripe**: For international card payments
- **PayU**: For comprehensive payment solutions
- **Paytm**: For wallet and UPI payments

### Gateway Integration Points
1. **Payment Processing**: Initiate payments with gateway
2. **Status Updates**: Handle webhook callbacks
3. **Refund Processing**: Process refunds through gateway
4. **Status Verification**: Verify payment/refund status

## Monitoring and Observability

### Health Checks
- `/actuator/health` - Service health status
- `/payments/health` - Payment service specific health
- `/refunds/health` - Refund service specific health

### Metrics
- Payment success/failure rates
- Average payment processing time
- Refund processing metrics
- Gateway response times

### Logging
- Structured logging with correlation IDs
- Payment transaction audit logs
- Error tracking and alerting
- Performance monitoring

## Security Considerations

### Authentication & Authorization
- JWT token validation (handled by API Gateway)
- Role-based access control
- User context validation

### Data Security
- Sensitive data encryption
- PCI DSS compliance considerations
- Secure webhook endpoints
- Transaction ID generation

### Audit & Compliance
- Complete transaction audit trail
- Payment status change tracking
- Refund approval workflows
- Regulatory compliance logging

## Deployment

### Docker Support
```dockerfile
FROM openjdk:17-jre-slim
COPY target/payment-service-1.0.0.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
- `SPRING_PROFILES_ACTIVE`: Active profile (dev, staging, prod)
- `DATABASE_URL`: PostgreSQL connection URL
- `EUREKA_SERVER_URL`: Service discovery server URL
- `PAYMENT_GATEWAY_API_KEY`: Payment gateway API key
- `WEBHOOK_SECRET_KEY`: Webhook validation secret

## Inter-service Communication

### Dependencies
- **Order Service**: Validate orders and update payment status
- **User Service**: Validate user information
- **Notification Service**: Send payment confirmations

### Service Contracts
- Feign clients with fallback mechanisms
- Circuit breaker patterns
- Retry logic for failed calls
- Graceful degradation

## Future Enhancements

1. **Real Payment Gateway Integration**
2. **Advanced Fraud Detection**
3. **Payment Analytics Dashboard**
4. **Subscription Payment Support**
5. **Multi-currency Support**
6. **Payment Scheduling**
7. **Advanced Refund Workflows**
8. **Payment Method Tokenization**

## Support

For issues and questions:
- Check the logs in `logs/payment-service.log`
- Review the API documentation
- Contact the development team

## License

This project is part of the Food Delivery Platform microservices architecture.
# Notification Service

A standalone Spring Boot microservice for sending notifications across multiple channels in the food delivery platform.

## Overview

The Notification Service is responsible for:
- Sending notifications for system events across multiple channels
- Supporting Email, SMS, and Push notification types
- Maintaining comprehensive notification history and audit trails
- Handling retries and failures with intelligent backoff strategies
- Processing bulk notifications efficiently
- Providing notification analytics and monitoring

## Features

### Multi-Channel Notifications
- **Email Notifications**: Welcome emails, payment confirmations, promotional offers
- **SMS Notifications**: Order updates, delivery status, critical alerts
- **Push Notifications**: Real-time updates, delivery tracking, promotional messages

### Event-Driven Architecture
- **Comprehensive Event Types**: 20+ predefined event types covering user, order, payment, and delivery events
- **Flexible Messaging**: Support for both simple text and template-based notifications
- **Reference Tracking**: Link notifications to specific orders, payments, or deliveries

### Reliability & Resilience
- **Retry Mechanism**: Intelligent exponential backoff (1min, 5min, 15min)
- **Failure Handling**: Comprehensive error tracking and failure reason logging
- **Asynchronous Processing**: Non-blocking notification sending with async execution
- **Status Tracking**: Complete lifecycle tracking (PENDING → SENT/FAILED)

### Bulk Operations
- **Bulk Notifications**: Send notifications to multiple users efficiently
- **Batch Processing**: Optimized processing for large notification volumes
- **Error Reporting**: Detailed success/failure reporting for bulk operations

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA with Hibernate
- **Async Processing**: Spring's @Async with custom thread pool
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test

## Domain Model

### Notification Entity
- **User Context**: User ID and recipient contact information
- **Event Context**: Event type, reference ID, and message content
- **Channel**: Notification type (EMAIL, SMS, PUSH)
- **Status Tracking**: Complete lifecycle with timestamps
- **Retry Logic**: Configurable retry attempts with exponential backoff
- **Template Support**: Template name and data for dynamic content
- **Metadata**: Additional context and tracking information

### Event Types
```
User Events: USER_REGISTERED, USER_VERIFIED
Order Events: ORDER_PLACED, ORDER_ACCEPTED, ORDER_DELIVERED, ORDER_CANCELLED
Payment Events: PAYMENT_SUCCESS, PAYMENT_FAILED, PAYMENT_REFUNDED
Delivery Events: DELIVERY_ASSIGNED, DELIVERY_PICKED_UP, DELIVERY_ON_THE_WAY
Promotional: PROMOTIONAL_OFFER, DISCOUNT_AVAILABLE
System Events: SYSTEM_MAINTENANCE, ACCOUNT_LOCKED, PASSWORD_RESET
```

### Notification Status Flow
```
PENDING ──→ SENT (Success)
   │
   └──→ RETRYING ──→ SENT (Retry Success)
           │
           └──→ FAILED (Max Retries Exceeded)
```

## API Endpoints

### Send Notifications

#### Single Notification
```http
POST /notifications
Content-Type: application/json

{
  "userId": 123,
  "referenceId": 456,
  "notificationType": "EMAIL",
  "eventType": "ORDER_PLACED",
  "message": "Your order has been placed successfully!",
  "recipientEmail": "user@example.com",
  "subject": "Order Confirmation",
  "templateName": "order_confirmation",
  "templateData": "{\"orderNumber\":\"ORD001\",\"amount\":\"299.50\"}",
  "maxRetryAttempts": 3
}
```

#### Bulk Notifications
```http
POST /notifications/bulk
Content-Type: application/json

{
  "userIds": [123, 456, 789],
  "notificationType": "PUSH",
  "eventType": "PROMOTIONAL_OFFER",
  "message": "Special offer! 30% off on your next order.",
  "subject": "Limited Time Offer",
  "maxRetryAttempts": 3
}
```

### Query Notifications

#### Get User Notifications
```http
GET /notifications/user/{userId}?page=0&size=20
GET /notifications/user/{userId}/recent?limit=10
```

#### Get Notification Details
```http
GET /notifications/{id}
GET /notifications/reference/{referenceId}
```

#### Admin Queries
```http
GET /notifications?page=0&size=20
GET /notifications/status/FAILED
GET /notifications/event/ORDER_PLACED
GET /notifications/stats
GET /notifications/delayed
```

### Management Operations

#### Retry Failed Notification
```http
PUT /notifications/{id}/retry
```

## Database Schema

### Notifications Table
- `id` (Primary Key)
- `user_id` (Required)
- `reference_id` (Optional - links to orders, payments, etc.)
- `notification_type` (EMAIL, SMS, PUSH)
- `event_type` (Predefined event types)
- `message` (Notification content)
- `status` (PENDING, SENT, FAILED, RETRYING)
- `recipient_email`
- `recipient_phone`
- `subject`
- `template_name`
- `template_data`
- `retry_count`
- `max_retry_attempts`
- `next_retry_at`
- `sent_at`
- `failed_at`
- `failure_reason`
- `external_id` (Provider reference)
- `metadata` (Additional context)
- `created_at`
- `updated_at`

## Configuration

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notification_service_db
    username: notification_user
    password: notification_password
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
notification:
  retry:
    max-attempts: 3
    delay-minutes:
      first: 1
      second: 5
      third: 15
  processing:
    batch-size: 100
    delay-threshold-minutes:
      realtime: 5
      email: 30
  sender:
    email:
      enabled: true
      timeout-seconds: 30
    sms:
      enabled: true
      timeout-seconds: 10
    push:
      enabled: true
      timeout-seconds: 5
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
   CREATE DATABASE notification_service_db;
   CREATE USER notification_user WITH PASSWORD 'notification_password';
   GRANT ALL PRIVILEGES ON DATABASE notification_service_db TO notification_user;
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
   curl http://localhost:8087/notifications/health
   ```

### Using Build Script
```bash
# Make script executable
chmod +x build-and-test.bat

# Run build and test
./build-and-test.bat
```

## Notification Channels

### Email Notifications
- **Use Cases**: Welcome emails, payment confirmations, promotional offers
- **Features**: HTML templates, subject lines, attachment support (future)
- **Delivery Time**: 1-3 seconds (simulated)
- **Success Rate**: 95% (simulated)

### SMS Notifications
- **Use Cases**: Order updates, delivery status, OTP, critical alerts
- **Features**: Short message format, international support
- **Delivery Time**: 0.5-1.5 seconds (simulated)
- **Success Rate**: 92% (simulated)

### Push Notifications
- **Use Cases**: Real-time updates, delivery tracking, promotional messages
- **Features**: Rich content, action buttons, deep linking (future)
- **Delivery Time**: 100-300ms (simulated)
- **Success Rate**: 98% (simulated)

## Retry Strategy

### Exponential Backoff
- **First Retry**: 1 minute after failure
- **Second Retry**: 5 minutes after first retry failure
- **Third Retry**: 15 minutes after second retry failure
- **Max Attempts**: Configurable (default: 3)

### Retry Conditions
- Network timeouts
- Temporary service unavailability
- Rate limiting from external providers
- Transient errors

### Non-Retryable Failures
- Invalid recipient information
- Malformed message content
- Authentication failures
- Permanent blocks/blacklists

## Security & Authorization

### Role-based Access Control
- **SYSTEM/PLATFORM**: Send notifications and bulk operations
- **ADMIN**: View all notifications, retry failed notifications, access statistics
- **USER/CUSTOMER**: View own notifications only
- **DELIVERY_PARTNER/RESTAURANT_ADMIN**: View relevant notifications

### Data Protection
- **PII Handling**: Secure storage of email addresses and phone numbers
- **Message Content**: Encrypted storage for sensitive notifications
- **Audit Trail**: Complete tracking of who sent what to whom
- **Retention Policy**: Configurable data retention periods

## Integration Patterns

### Event-Driven Integration
Other services trigger notifications by calling the REST API:

```java
// Order Service example
NotificationRequestDTO notification = new NotificationRequestDTO();
notification.setUserId(order.getUserId());
notification.setReferenceId(order.getId());
notification.setNotificationType(NotificationType.SMS);
notification.setEventType(EventType.ORDER_PLACED);
notification.setMessage("Your order #" + order.getOrderNumber() + " has been placed!");

notificationServiceClient.sendNotification(notification);
```

### Decoupled Architecture
- **No Business Logic**: Service only handles notification sending
- **No External Dependencies**: Doesn't call other business services
- **Event Agnostic**: Accepts any event type and message content
- **Channel Agnostic**: Supports multiple notification channels

## Monitoring and Observability

### Health Checks
- `/actuator/health` - Service health status
- `/notifications/health` - Notification service specific health

### Metrics and Statistics
- **Notification Volume**: Total notifications sent by type and status
- **Success Rates**: Delivery success rates by channel
- **Processing Times**: Average time from creation to delivery
- **Failure Analysis**: Common failure reasons and patterns
- **Retry Statistics**: Retry success rates and patterns

### Logging
- **Structured Logging**: JSON format with correlation IDs
- **Notification Lifecycle**: Complete audit trail for each notification
- **Error Tracking**: Detailed error logs with stack traces
- **Performance Monitoring**: Processing time and throughput metrics

## Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
The service includes:
- Unit tests for service layer business logic
- Integration tests for repositories and database operations
- Controller tests with MockMvc
- Async processing tests
- Retry mechanism tests
- Notification sending simulation tests

## Deployment

### Docker Support
```dockerfile
FROM openjdk:17-jre-slim
COPY target/notification-service-1.0.0.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
- `SPRING_PROFILES_ACTIVE`: Active profile (dev, staging, prod)
- `DATABASE_URL`: PostgreSQL connection URL
- `EUREKA_SERVER_URL`: Service discovery server URL
- `NOTIFICATION_EMAIL_ENABLED`: Enable/disable email notifications
- `NOTIFICATION_SMS_ENABLED`: Enable/disable SMS notifications
- `NOTIFICATION_PUSH_ENABLED`: Enable/disable push notifications

## Future Enhancements

1. **Real Provider Integration**
   - SendGrid/AWS SES for email
   - Twilio/AWS SNS for SMS
   - Firebase/OneSignal for push notifications

2. **Advanced Features**
   - Rich HTML email templates
   - Notification preferences per user
   - A/B testing for notification content
   - Delivery time optimization

3. **Analytics & Intelligence**
   - Open/click tracking for emails
   - Delivery confirmation for SMS
   - Push notification engagement metrics
   - ML-based optimal delivery time prediction

4. **Scalability Enhancements**
   - Message queue integration (RabbitMQ/Kafka)
   - Horizontal scaling with load balancing
   - Caching for frequently accessed data
   - Database sharding for high volume

## Support

For issues and questions:
- Check the logs in `logs/notification-service.log`
- Review the API documentation
- Monitor notification statistics via `/notifications/stats`
- Contact the development team

## License

This project is part of the Food Delivery Platform microservices architecture.
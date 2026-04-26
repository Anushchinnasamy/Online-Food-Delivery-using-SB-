#!/bin/bash

# Food Delivery Platform - Microservices Startup Script
# This script starts all microservices in the correct order

echo "🚀 Starting Food Delivery Platform Microservices..."
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}$1${NC}"
}

# Function to check if a service is running
check_service() {
    local service_name=$1
    local port=$2
    local max_attempts=30
    local attempt=1

    print_status "Waiting for $service_name to start on port $port..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:$port/actuator/health > /dev/null 2>&1; then
            print_status "$service_name is running on port $port ✅"
            return 0
        fi
        
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    print_error "$service_name failed to start on port $port ❌"
    return 1
}

# Function to start a service
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    
    print_header "Starting $service_name..."
    
    if [ ! -d "$service_dir" ]; then
        print_error "Service directory $service_dir not found!"
        return 1
    fi
    
    cd "$service_dir"
    
    # Start the service in background
    nohup mvn spring-boot:run > "../logs/${service_name}.log" 2>&1 &
    local pid=$!
    echo $pid > "../logs/${service_name}.pid"
    
    print_status "$service_name started with PID $pid"
    
    # Wait for service to be ready
    check_service "$service_name" "$port"
    
    cd ..
}

# Create logs directory
mkdir -p logs

print_header "Step 1: Starting Infrastructure Services"
print_status "Starting Docker containers (databases, Redis, RabbitMQ)..."
docker-compose up -d

# Wait for databases to be ready
print_status "Waiting for databases to be ready..."
sleep 10

print_header "Step 2: Starting Core Services"

# Start Service Discovery (Eureka Server)
start_service "Service Discovery" "service-discovery" "8761"

# Wait a bit for Eureka to be fully ready
sleep 5

# Start Config Server
start_service "Config Server" "config-server" "8888"

# Wait for Config Server to be ready
sleep 5

print_header "Step 3: Starting Business Services"

# Start Auth Service
start_service "Auth Service" "auth-service" "8081"

# Start other services in parallel (they can start independently)
start_service "User Service" "user-service" "8082" &
start_service "Restaurant Service" "restaurant-service" "8083" &
start_service "Cart Service" "cart-service" "8084" &

# Wait for the parallel services to complete
wait

# Start services that depend on others
start_service "Order Service" "order-service" "8085"
start_service "Payment Service" "payment-service" "8086"
start_service "Delivery Service" "delivery-service" "8087"
start_service "Reporting Service" "reporting-service" "8088"

print_header "Step 4: Starting API Gateway"

# Start API Gateway last
start_service "API Gateway" "api-gateway" "8080"

print_header "🎉 All Services Started Successfully!"
echo "=================================================="
print_status "Service Discovery: http://localhost:8761"
print_status "Config Server: http://localhost:8888"
print_status "API Gateway: http://localhost:8080"
print_status "Auth Service: http://localhost:8081"
print_status "User Service: http://localhost:8082"
print_status "Restaurant Service: http://localhost:8083"
print_status "Cart Service: http://localhost:8084"
print_status "Order Service: http://localhost:8085"
print_status "Payment Service: http://localhost:8086"
print_status "Delivery Service: http://localhost:8087"
print_status "Reporting Service: http://localhost:8088"
echo ""
print_status "Infrastructure Services:"
print_status "RabbitMQ Management: http://localhost:15672 (admin/password)"
print_status "Kibana: http://localhost:5601"
print_status "Grafana: http://localhost:3000 (admin/admin)"
print_status "Prometheus: http://localhost:9090"
echo ""
print_status "Demo Accounts (password: password123):"
print_status "Admin: admin@fooddelivery.com"
print_status "Customer: john.doe@gmail.com"
print_status "Restaurant: raj@spicepalace.com"
print_status "Delivery: ravi.delivery@gmail.com"
echo ""
print_status "Logs are available in the 'logs' directory"
print_status "Use 'stop-microservices.sh' to stop all services"

echo "🚀 Food Delivery Platform is ready for use!"
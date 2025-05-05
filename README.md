# Restaurant Order System

A comprehensive microservices-based system for restaurant order management, built with Spring Boot, Hexagonal Architecture, and event-driven communication.

![Restaurant System Architecture](docs/images/architecture.png)

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
  - [Hexagonal Architecture](#hexagonal-architecture)
  - [Microservices](#microservices)
  - [Event-Driven Communication](#event-driven-communication)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Services Description](#services-description)
  - [Menu Service](#menu-service)
  - [Order Service](#order-service)
  - [Kitchen Service (Planned)](#kitchen-service-planned)
  - [Delivery Service (Planned)](#delivery-service-planned)
  - [Notification Service (Planned)](#notification-service-planned)
- [Infrastructure Components](#infrastructure-components)
  - [API Gateway](#api-gateway)
  - [Service Discovery (Eureka)](#service-discovery-eureka)
  - [Config Server](#config-server)
  - [Keycloak (Security)](#keycloak-security)
  - [Kafka](#kafka)
- [Complete System Flow](#complete-system-flow)
- [Local Setup](#local-setup)
  - [Prerequisites](#prerequisites)
  - [Clone Repository](#clone-repository)
  - [Configure Environment](#configure-environment)
  - [Infrastructure Setup](#infrastructure-setup)
  - [Building & Running Services](#building--running-services)
- [Testing the System](#testing-the-system)
  - [Access Points](#access-points)
  - [Authentication](#authentication)
  - [Sample API Requests](#sample-api-requests)
- [Development Guidelines](#development-guidelines)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

## Overview

The Restaurant Order System is a modern, cloud-native application designed to manage the complete lifecycle of food orders in a restaurant. Built using microservices architecture and following domain-driven design principles, it separates different business capabilities into autonomous services that communicate through event-driven patterns.

This system demonstrates how to implement complex business processes using hexagonal architecture within each microservice, allowing for clean separation between business logic and technical concerns.

## Architecture

### Hexagonal Architecture

Each microservice in the system is implemented using the Hexagonal Architecture (also known as Ports and Adapters) pattern. This architecture places the domain model and business logic at the center, with various adapters connecting it to the outside world.

The key components of the hexagonal architecture in our services are:

- **Domain Layer**: Contains pure business entities and logic with no dependencies on external systems
- **Application Layer**: Contains use cases and orchestrates the domain layer
- **Infrastructure Layer**: Contains adapters that implement ports defined in the application layer
- **API Layer**: Provides REST endpoints and converts external requests to internal commands

![Hexagonal Architecture](docs/images/hexagonal.png)

This approach allows for:
- Isolated business logic that can be tested independently
- Easy swapping of infrastructure components like databases or messaging systems
- Clearer separation of concerns within each service

### Microservices

The system is divided into the following microservices, each responsible for a specific business capability:

1. **Menu Service**: Manages product catalog and categories
2. **Order Service**: Handles order creation and lifecycle management
3. **Kitchen Service** (Planned): Manages food preparation workflow
4. **Delivery Service** (Planned): Manages delivery assignments and tracking
5. **Notification Service** (Planned): Sends updates to customers through various channels

Supporting infrastructure services include:
- API Gateway
- Service Discovery (Eureka)
- Configuration Server
- Keycloak (Authentication Server)

### Event-Driven Communication

Services communicate through events published to Apache Kafka:

1. When a state changes in one service, it publishes an event to Kafka
2. Other services subscribe to relevant events and react accordingly
3. This creates a loosely coupled architecture where services can evolve independently

Main event flows:
- Order Created → Kitchen Preparation → Delivery → Order Completed
- Status updates at each stage trigger notifications to customers

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.x
- **Build Tool**: Maven
- **Databases**:
  - PostgreSQL (Order Service)
  - MongoDB (Menu Service)
- **Messaging**: Apache Kafka
- **Service Discovery**: Eureka Server
- **API Gateway**: Spring Cloud Gateway
- **Configuration**: Spring Cloud Config
- **Security**: Keycloak (OAuth2/OpenID Connect)
- **Containerization**: Docker, Docker Compose
- **Documentation**: SpringDoc OpenAPI (Swagger)

## Project Structure

The project follows a modular Maven structure:

```
restaurant-order-system/
├── restaurant-common/                     # Common libraries
│   ├── restaurant-common-security/        # Security components
│   └── restaurant-common-messaging/       # Messaging components
│
├── restaurant-infrastructure/             # Infrastructure services
│   ├── service-discovery/                 # Eureka Server
│   ├── config-server/                     # Config Server
│   ├── api-gateway/                       # API Gateway
│   └── keycloak-config/                   # Keycloak setup
│
├── restaurant-menu-service/               # Menu Service
│   ├── menu-domain/                       # Domain layer
│   ├── menu-application/                  # Application layer
│   ├── menu-infrastructure/               # Infrastructure layer
│   └── menu-boot/                         # Service bootstrap
│
├── restaurant-order-service/              # Order Service
│   ├── order-domain/                      # Domain layer
│   ├── order-application/                 # Application layer
│   ├── order-infrastructure/              # Infrastructure layer
│   └── order-boot/                        # Service bootstrap
│
└── docker/                                # Docker configurations
```

Each service follows the same modular structure aligned with hexagonal architecture:

- **Domain**: Contains the business entities and logic
- **Application**: Contains use cases and ports
- **Infrastructure**: Contains adapters for repositories, messaging, etc.
- **Boot**: Contains the service bootstrap and configurations

## Services Description

### Menu Service

The Menu Service manages the restaurant's product catalog, including food items and categories.

**Key Features**:
- Create, read, update, and delete menu items and categories
- Check availability of products
- Retrieve product details including prices
- Search and filter products by category

**Technical Details**:
- MongoDB for flexible schema management
- Hexagonal architecture with:
  - Domain models: Product, Category
  - Application ports: Repository interfaces, Use cases
  - Infrastructure adapters: MongoDB repositories, REST controllers

**API Endpoints**:
- `GET /categories` - List all categories
- `GET /categories/{id}` - Get category by ID
- `POST /categories` - Create a new category
- `PUT /categories/{id}` - Update a category
- `DELETE /categories/{id}` - Delete a category
- `GET /menu-items` - List all menu items
- `GET /menu-items/{id}` - Get menu item by ID
- `POST /menu-items` - Create a new menu item
- `PUT /menu-items/{id}` - Update a menu item
- `DELETE /menu-items/{id}` - Delete a menu item

### Order Service

The Order Service handles the entire lifecycle of a food order, from creation to completion.

**Key Features**:
- Create new orders
- Update order status
- Calculate order totals with tax
- Manage the order lifecycle (created → paid → preparing → ready → delivered)
- Cancel orders

**Technical Details**:
- PostgreSQL for transactional integrity
- Hexagonal architecture with:
  - Domain models: Order, OrderItem, OrderStatus
  - Application ports: Repository interfaces, Use cases, Event publishers
  - Infrastructure adapters: JPA repositories, REST controllers, Kafka producers

**API Endpoints**:
- `POST /orders` - Create a new order
- `GET /orders/{id}` - Get order details by ID
- `GET /orders/customer/{customerId}` - Get orders by customer
- `GET /orders/status/{status}` - Get orders by status
- `PUT /orders/{id}/status` - Update order status
- `PUT /orders/{id}/cancel` - Cancel an order

### Kitchen Service (Planned)

The Kitchen Service will manage the food preparation workflow.

**Key Features**:
- Receive new orders from Order Service
- Track preparation status
- Update orders when ready for delivery
- Manage kitchen workload

**Technical Details**:
- MongoDB for flexible workflow schema
- Kafka consumer for order events
- Kafka producer for kitchen events

### Delivery Service (Planned)

The Delivery Service will manage the delivery of prepared orders.

**Key Features**:
- Assign deliveries to drivers
- Track delivery status
- Update order status on delivery completion
- Optimize delivery routes

**Technical Details**:
- PostgreSQL for delivery tracking
- MongoDB for real-time location data
- Kafka integration for event-driven workflow

### Notification Service (Planned)

The Notification Service will handle all customer communications.

**Key Features**:
- Send order confirmations
- Notify customers of status changes
- Support multiple notification channels (email, SMS)

**Technical Details**:
- Kafka consumer for event subscription
- Integration with email and SMS services
- Template-based message generation

## Infrastructure Components

### API Gateway

The API Gateway serves as the entry point for all client requests, providing:

- Routing to appropriate microservices
- Authentication and authorization
- Request logging and monitoring
- Cross-cutting concerns

**Configuration**:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: menu-service
          uri: lb://menu-service
          predicates:
            - Path=/api/menu/**
          filters:
            - RewritePath=/api/menu/(?<segment>.*), /$\{segment}
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - RewritePath=/api/orders/(?<segment>.*), /$\{segment}
```

### Service Discovery (Eureka)

Eureka Server enables service discovery and registration:

- Microservices register themselves with Eureka
- Services locate each other without hardcoded URLs
- Health monitoring and load balancing

### Config Server

The Config Server centralizes configuration management:

- Externalized configuration properties
- Environment-specific configurations
- Runtime configuration updates

### Keycloak (Security)

Keycloak provides authentication and authorization:

- OAuth2/OpenID Connect implementation
- User management and role-based access
- Token-based authentication
- Resource protection

### Kafka

Apache Kafka enables event-driven communication:

- Publishing and subscribing to events
- Reliable message delivery
- Event persistence
- Stream processing

## Complete System Flow

The complete order flow in the system works as follows:

1. **Order Creation**:
   - Customer places an order via API Gateway
   - Order Service validates products with Menu Service
   - Order Service creates order and publishes `OrderCreatedEvent`
   - Notification Service sends order confirmation

2. **Kitchen Processing**:
   - Kitchen Service consumes `OrderCreatedEvent`
   - Kitchen staff prepares food
   - Kitchen Service updates status and publishes `OrderReadyEvent`
   - Notification Service notifies customer that order is ready

3. **Delivery**:
   - Delivery Service consumes `OrderReadyEvent`
   - Delivery Service assigns a driver
   - Driver delivers the food
   - Delivery Service publishes `OrderDeliveredEvent`
   - Order Service updates order status
   - Notification Service confirms delivery to customer

![Event Flow](docs/images/event-flow.png)

## Local Setup

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- Docker and Docker Compose
- Git

### Clone Repository

```bash
git clone https://github.com/diegomnDev/restaurant-order-system.git
cd restaurant-order-system
```

### Infrastructure Setup

Start the infrastructure services using Docker Compose:

```bash
cd docker
docker-compose up -d
```

This will start:
- PostgreSQL database
- MongoDB database
- Zookeeper and Kafka
- Keycloak

### Building & Running Services

Build all services:

```bash
cd ..
mvn clean install
```

Run each service:

```bash
# Service Discovery
cd restaurant-infrastructure/service-discovery
mvn spring-boot:run

# Config Server
cd ../config-server
mvn spring-boot:run

# API Gateway
cd ../api-gateway
mvn spring-boot:run

# Menu Service
cd ../../restaurant-menu-service/menu-boot
mvn spring-boot:run

# Order Service
cd ../../restaurant-order-service/order-boot
mvn spring-boot:run
```

## Testing the System

### Access Points

- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Keycloak Admin**: http://localhost:8090/admin
- **MongoDB Express**: http://localhost:8001
- **Swagger UI**:
  - Menu Service: http://localhost:8081/swagger-ui.html
  - Order Service: http://localhost:8082/swagger-ui.html
  - API Gateway: http://localhost:8080/swagger-ui.html

### Authentication

To obtain an authentication token from Keycloak:

```bash
curl -X POST http://localhost:8090/realms/restaurant/protocol/openid-connect/token \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'grant_type=password' \
  --data-urlencode 'client_id=restaurant-order-app' \
  --data-urlencode 'client_secret=your-client-secret' \
  --data-urlencode 'username=admin1' \
  --data-urlencode 'password=password' \
  --data-urlencode 'scope=openid roles'
```

### Sample API Requests

Here are some sample curl commands to test the API:

#### 1. Create a Category

```bash
curl -X POST http://localhost:8080/api/menu/categories \
  -H 'Authorization: Bearer YOUR_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Pizzas",
    "description": "Delicious Italian pizzas",
    "active": true
  }'
```

#### 2. Create a Menu Item

```bash
curl -X POST http://localhost:8080/api/menu/menu-items \
  -H 'Authorization: Bearer YOUR_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Pizza Margherita",
    "description": "Classic pizza with tomato sauce, mozzarella and basil",
    "price": 10.99,
    "categoryId": "cat-1",
    "available": true
  }'
```

#### 3. Create an Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H 'Authorization: Bearer YOUR_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{
    "customerId": "cust-1",
    "customerName": "John Doe",
    "items": [
      {
        "productId": "prod-1",
        "quantity": 2
      }
    ],
    "notes": "Test order"
  }'
```

## Development Guidelines

When contributing to this project, please follow these guidelines:

1. **Hexagonal Architecture**: Maintain the separation between domain, application, and infrastructure layers
2. **Domain-Driven Design**: Focus on the domain model and business rules
3. **Test Coverage**: Write unit tests for domain and application layers, and integration tests for infrastructure
4. **Event-Driven**: Use events for communication between services
5. **Documentation**: Update API documentation and this README when adding features

## Future Enhancements

The following enhancements are planned for future releases:

1. Implementation of Kitchen Service
2. Implementation of Delivery Service
3. Implementation of Notification Service
4. Advanced monitoring and observability with Prometheus and Grafana
5. Deployment configurations for Kubernetes
6. Implementation of CQRS pattern for read models
7. Integration with payment gateways

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

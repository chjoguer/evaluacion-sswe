# MSA Movement WebFlux

Reactive Microservice for Account Movements using Spring WebFlux.

## Overview

This microservice manages account movements with the following features:

- **Reactive Programming**: Built with Spring WebFlux for non-blocking I/O
- **Database**: PostgreSQL with R2DBC for reactive database access
- **API-First Design**: OpenAPI 3.0 specification with auto-generated code
- **Idempotency**: Unique key support for safe operation retries
- **Comprehensive Filtering**: Filter movements by account, date range, and type
- **Error Handling**: Global exception handling with structured error responses

## Key Features

### Movement Types
- **DEBIT**: Withdrawals and debits
- **CREDIT**: Deposits and credits

### API Endpoints

- `GET /api/movements` - Get all movements with optional filters
- `POST /api/movements` - Create a new movement (idempotent)
- `GET /api/movements/{movementId}` - Get movement by ID
- `PUT /api/movements/{movementId}` - Update movement metadata
- `DELETE /api/movements/{movementId}` - Delete movement
- `GET /api/movements/unique/{uniqueKey}` - Get movement by unique key
- `GET /api/movements/account/{accountId}` - Get movements by account
- `GET /api/movements/type/{movementType}` - Get movements by type

### Filtering Options

- **accountId**: Filter by specific account
- **from/to**: Date range filtering
- **movementType**: Filter by DEBIT or CREDIT

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring WebFlux** (Reactive Web)
- **Spring Data R2DBC** (Reactive Database Access)
- **PostgreSQL** (Database)
- **MapStruct** (Object Mapping)
- **Lombok** (Boilerplate Reduction)
- **OpenAPI Generator** (Code Generation)
- **Docker & Docker Compose** (Containerization)

## Prerequisites

- Java 21+
- Docker and Docker Compose
- Gradle 8+

## Quick Start

### 1. Clone and Navigate
```bash
cd msa-movement-wflux
```

### 2. Start Database
```bash
docker-compose up postgres -d
```

### 3. Build Application
```bash
./gradlew build
```

### 4. Run Application
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8081`

### 5. API Documentation
Access Swagger UI at: `http://localhost:8081/swagger-ui.html`

## Database Setup

The PostgreSQL database will be automatically initialized with:
- Database: `msa_movement_db`
- User: `msa_movement_user`
- Password: `msa_movement_password`
- Port: `5433` (host) -> `5432` (container)

### Database Schema

The `movements` table includes:
- `movement_id`: Primary key
- `unique_key`: Idempotency key (8-64 chars)
- `account_id`: Associated account
- `occurred_at`: Movement timestamp
- `movement_type`: DEBIT or CREDIT
- `amount`: Movement amount (> 0)
- `balance`: Account balance after movement
- `description`: Optional description
- `reference`: Optional external reference
- `created_at`/`updated_at`: Audit timestamps

## API Examples

### Create Movement
```bash
curl -X POST http://localhost:8081/api/movements \
  -H "Content-Type: application/json" \
  -d '{
    "uniqueKey": "mov-2025-001",
    "accountId": 1,
    "occurredAt": "2025-09-08T10:00:00Z",
    "movementType": "CREDIT",
    "amount": 500.00,
    "balance": 1500.00,
    "description": "Initial deposit",
    "reference": "DEP-001"
  }'
```

### Get Movements by Account
```bash
curl "http://localhost:8081/api/movements/account/1?from=2025-09-01T00:00:00Z&to=2025-09-30T23:59:59Z&movementType=CREDIT"
```

### Get Movement by Unique Key
```bash
curl "http://localhost:8081/api/movements/unique/mov-2025-001"
```

## Configuration

### Application Properties
- **Server Port**: 8081
- **Database**: PostgreSQL on port 5433
- **Connection Pool**: 10-20 connections
- **Logging**: DEBUG level for R2DBC and application

### Environment Variables
You can override default settings using environment variables:
- `SPRING_R2DBC_URL`
- `SPRING_R2DBC_USERNAME`
- `SPRING_R2DBC_PASSWORD`
- `SERVER_PORT`

## Development

### Code Generation
The project uses OpenAPI Generator to create API interfaces and DTOs:
```bash
./gradlew buildSpringServer
```

### Database Management
Access pgAdmin at `http://localhost:8082`
- Email: `admin@msa-movement.com`
- Password: `admin123`

### Testing
```bash
./gradlew test
```

## Architecture

### Reactive Flow
```
HTTP Request  Controller  Service  Repository  Database
                    
HTTP Response  DTO  Mapper  Entity  R2DBC
```

### Key Components
- **MovementController**: REST endpoints
- **MovementService**: Business logic
- **MovementRepository**: Data access
- **MovementMapper**: Entity-DTO conversion
- **MovementEntity**: Database entity
- **GlobalExceptionHandler**: Error handling

## Error Handling

The API returns structured error responses:
```json
{
  "timestamp": "2025-09-08T15:04:05Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid 'amount' (must be > 0)",
  "path": "/api/movements"
}
```

## Idempotency

Movements use a `uniqueKey` field to ensure idempotency:
- Each movement must have a unique key (8-64 characters)
- Duplicate keys return 409 Conflict
- Supports safe retry scenarios

## Monitoring

Health check endpoint: `http://localhost:8081/actuator/health`

Available actuator endpoints:
- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`

## Production Considerations

1. **Security**: Add authentication/authorization
2. **Validation**: Enhanced input validation
3. **Monitoring**: Add metrics and tracing
4. **Caching**: Consider Redis for frequently accessed data
5. **Rate Limiting**: Implement API rate limiting
6. **Database**: Configure connection pooling for production load

## License

This project is part of the MSA (Microservices Architecture) suite for banking operations.

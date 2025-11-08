# DDD Spring Boot Application Setup Guide
## Auth Service - Domain-Driven Design Architecture

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Module Structure](#2-module-structure)
3. [Technology Stack](#3-technology-stack)
4. [Project Setup Instructions](#4-project-setup-instructions)
5. [Module Configuration](#5-module-configuration)
6. [DDD Principles Implementation](#6-ddd-principles-implementation)
7. [Dependency Management](#7-dependency-management)
8. [Build Configuration](#8-build-configuration)
9. [Best Practices](#9-best-practices)

---

## 1. Project Overview

### 1.1 Architecture Pattern
This project follows **Domain-Driven Design (DDD)** principles with a **multi-module Gradle** structure, separating concerns into distinct layers:

- **Application Layer**: REST controllers, API endpoints, request/response handling
- **Domain Layer**: Core business logic, domain models, ports, and adapters
- **Data Layer**: Database configurations, repositories, external service integrations

### 1.2 Module Hierarchy

```
auth-service (Root Project)
├── authservice-application     # Application/API Layer
├── authservice-domain         # Domain Layer (Parent Module)
│   ├── domain-application     # Ports & Adapters (Application Services)
│   └── domain-core            # Core Domain Logic (Entities, Value Objects)
└── authservice-data           # Infrastructure/Data Layer
```

---

## 2. Module Structure

### 2.1 Root Project (`auth-service`)

**Purpose**: 
- Main Gradle build file with shared dependencies
- Project-wide configuration
- Common build scripts and plugins

**Key Responsibilities**:
- Define all dependency versions
- Configure common plugins (Spring Boot, MapStruct, Lombok)
- Set up code quality tools
- Define shared build configurations

### 2.2 Application Module (`authservice-application`)

**Purpose**: 
- REST API layer
- HTTP request/response handling
- API documentation (OpenAPI/Swagger)
- Security configuration (Spring Security)
- Exception handling
- Input validation

**Key Components**:
- REST Controllers
- DTOs (Data Transfer Objects)
- Request/Response models
- Exception handlers
- API documentation configuration
- Security filters and interceptors

**Dependencies**:
- Depends on `authservice-domain` (domain-application submodule)
- Spring Web, Spring Security
- OpenAPI/Swagger
- Validation API

### 2.3 Domain Module (`authservice-domain`)

**Purpose**: 
- Core business logic
- Domain models and rules
- Application services (ports)
- Adapter interfaces

#### 2.3.1 Domain Core (`domain-core`)

**Purpose**: 
- Pure domain logic
- Business rules and invariants
- Domain entities and aggregates
- Value objects
- Domain events
- Domain exceptions

**Key Components**:
- Entity classes (JPA-free domain models)
- Value Objects
- Aggregate Roots
- Domain Services
- Domain Events
- Business logic validations

**Dependencies**:
- Minimal dependencies (no Spring, no JPA)
- Only domain-related libraries
- Jakarta Validation (for domain rules)

#### 2.3.2 Domain Application (`domain-application`)

**Purpose**: 
- Application services (use cases)
- Port interfaces (input/output ports)
- Adapter interfaces
- Domain-to-DTO mapping interfaces
- Use case orchestration

**Key Components**:
- Port interfaces (Input/Output)
- Application Services
- Use Case implementations
- Domain-to-DTO mapper interfaces (MapStruct)
- Command/Query handlers

**Dependencies**:
- Depends on `domain-core`
- MapStruct (for mapping interfaces)
- Spring (for dependency injection)
- Jakarta Validation

### 2.4 Data Module (`authservice-data`)

**Purpose**: 
- Infrastructure implementations
- Database configurations
- Repository implementations
- External service adapters
- Redis configuration
- PostgreSQL configuration
- JPA entities and repositories

**Key Components**:
- JPA Entity classes (persistence models)
- Repository implementations
- Database configuration classes
- Redis configuration and clients
- PostgreSQL connection settings
- Adapter implementations (for domain ports)
- Entity-to-Domain mappers (MapStruct)

**Dependencies**:
- Depends on `authservice-domain` (domain-application)
- Spring Data JPA
- PostgreSQL driver
- Redis client (Lettuce/Jedis)
- MapStruct (for entity-domain mapping)
- Flyway/Liquibase (for migrations)

---

## 3. Technology Stack

### 3.1 Core Framework
- **Spring Boot**: 3.x
- **Java**: 22+
- **Build Tool**: Gradle 8.x+
- **Package Manager**: Gradle Wrapper

### 3.2 Domain & Mapping
- **MapStruct**: For DTO/Entity/Domain object mapping
- **Lombok**: For reducing boilerplate code
- **Jakarta Validation**: For validation rules

### 3.3 Data Layer
- **Spring Data JPA**: For database access
- **PostgreSQL**: Primary database
- **Redis**: Caching and session storage
- **HikariCP**: Connection pooling
- **Flyway/Liquibase**: Database migrations

### 3.4 Security
- **Spring Security**: Authentication and authorization
- **Spring Security OAuth2**: OIDC integration
- **JWT**: Token handling

### 3.5 API & Documentation
- **Spring Web**: REST API
- **OpenAPI 3.0 / SpringDoc**: API documentation
- **Jackson**: JSON serialization

### 3.6 Testing
- **JUnit 5**: Unit testing
- **Mockito**: Mocking
- **Testcontainers**: Integration testing
- **Spring Boot Test**: Integration tests

### 3.7 Code Quality
- **Checkstyle**: Code style checking
- **SpotBugs**: Static analysis
- **JaCoCo**: Code coverage

---

## 4. Project Setup Instructions

### 4.1 Prerequisites

1. **Java Development Kit (JDK)**: Version 22 or higher
2. **Gradle**: Version 8.5 or higher (or use Gradle Wrapper)
3. **IDE**: IntelliJ IDEA (recommended) or Eclipse/VS Code
4. **Docker**: For running PostgreSQL and Redis locally
5. **Git**: For version control

### 4.2 Initial Project Structure

Create the following directory structure:

```
auth-service/
├── settings.gradle.kts          # Module declarations
├── build.gradle.kts             # Root build file with dependencies
├── gradle/
│   └── wrapper/                 # Gradle wrapper files
├── authservice-application/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/authservice/application/
│       │   │       ├── controller/
│       │   │       ├── dto/
│       │   │       ├── config/
│       │   │       └── exception/
│       │   └── resources/
│       │       ├── application.yml
│       │       └── application-dev.yml
│       └── test/
├── authservice-domain/
│   ├── build.gradle.kts         # Parent module build
│   ├── domain-core/
│   │   ├── build.gradle.kts
│   │   └── src/
│   │       └── main/java/com/authservice/domain/core/
│   │           ├── model/
│   │           ├── entity/
│   │           │   ├── valueobject/
│   │           │   ├── service/
│   │           │   └── event/
│   └── domain-application/
│       ├── build.gradle.kts
│       └── src/
│           └── main/java/com/authservice/domain/application/
│               ├── port/
│               │   ├── input/
│               │   └── output/
│               ├── service/
│               └── mapper/
└── authservice-data/
    ├── build.gradle.kts
    └── src/
        ├── main/
        │   ├── java/com/authservice/data/
        │   │   ├── entity/
        │   │   ├── repository/
        │   │   ├── adapter/
        │   │   ├── config/
        │   │   └── mapper/
        │   └── resources/
        │       ├── db/migration/    # Flyway migrations
        │       └── application-data.yml
        └── test/
```

### 4.3 Root Build File Configuration

**File**: `build.gradle.kts` (root)

**Key Sections**:
1. **Plugins Block**: Apply common plugins to all subprojects
2. **Dependency Management**: Define all dependency versions in `extra` or `libs.versions.toml`
3. **Subprojects Block**: Common configuration for all modules
4. **Repositories**: Maven repositories configuration

**Dependency Version Management**:
- Use `libs.versions.toml` (Gradle Version Catalog) for centralized version management
- Define versions for: Spring Boot, MapStruct, Lombok, PostgreSQL, Redis, etc.

### 4.4 Module Build Files

Each module should have its own `build.gradle.kts` with:
- Module-specific dependencies
- Plugin configurations
- Compilation settings

---

## 5. Module Configuration

### 5.1 Application Module Configuration

**Application Properties** (`application.yml`):
- Server port configuration
- Spring profiles
- API documentation settings
- Security configuration
- CORS settings

**Key Configuration Areas**:
- Server port and context path
- Spring profiles (dev, test, prod)
- OpenAPI/Swagger documentation
- Exception handling configuration
- Request/response logging

### 5.2 Domain Module Configuration

**Domain Core**:
- No Spring configuration needed (pure domain)
- Only domain-related annotations
- Validation annotations for business rules

**Domain Application**:
- Spring component scanning for application services
- MapStruct processor configuration
- Dependency injection setup

### 5.3 Data Module Configuration

**Database Configuration**:
- PostgreSQL connection settings
- Connection pool configuration (HikariCP)
- JPA/Hibernate settings
- Transaction management

**Redis Configuration**:
- Redis connection settings
- Redis template configuration
- Cache configuration
- Session store settings

**Migration Configuration**:
- Flyway/Liquibase settings
- Migration scripts location
- Schema version management

---

## 6. DDD Principles Implementation

### 6.1 Layer Separation

**Application Layer** (`authservice-application`):
- Handles HTTP concerns only
- Delegates to domain application services
- Maps DTOs to domain objects
- No business logic

**Domain Layer** (`authservice-domain`):
- Contains all business logic
- Independent of infrastructure
- Pure domain models (domain-core)
- Application services orchestrate use cases (domain-application)

**Infrastructure Layer** (`authservice-data`):
- Implements domain ports
- Handles persistence concerns
- Maps between domain and persistence models
- External service integrations

### 6.2 Ports and Adapters Pattern

**Input Ports** (in `domain-application`):
- Define use case interfaces
- Called by application layer
- Example: `UserManagementPort`, `AuthenticationPort`

**Output Ports** (in `domain-application`):
- Define repository interfaces
- Define external service interfaces
- Example: `UserRepositoryPort`, `TokenStoragePort`

**Adapters** (in `authservice-data`):
- Implement output ports
- Handle infrastructure concerns
- Example: `JpaUserRepositoryAdapter`, `RedisTokenStorageAdapter`

### 6.3 Dependency Direction

```
Application → Domain (Application) → Domain (Core)
     ↓                ↓
   Data ──────────────┘
```

**Rules**:
- Application depends on Domain
- Data depends on Domain
- Domain Core has no dependencies on other modules
- Domain Application depends only on Domain Core
- Application and Data never depend on each other

### 6.4 Domain Model Guidelines

**Domain Core**:
- Entities: Rich domain models with behavior
- Value Objects: Immutable objects representing concepts
- Aggregates: Consistency boundaries
- Domain Services: Operations that don't belong to a single entity
- Domain Events: Important business occurrences

**Domain Application**:
- Use Cases: Application-level operations
- Commands: Intent to change state
- Queries: Intent to read data
- Application Services: Orchestrate domain operations

---

## 7. Dependency Management

### 7.1 Version Catalog (`libs.versions.toml`)

**Location**: `gradle/libs.versions.toml`

**Structure**:
- `[versions]`: All dependency versions
- `[libraries]`: Dependency coordinates
- `[bundles]`: Dependency groups
- `[plugins]`: Plugin versions

**Benefits**:
- Centralized version management
- Type-safe dependency access
- Easy version updates
- Consistent versions across modules

### 7.2 Dependency Groups

**Spring Boot BOM**:
- Manages Spring dependency versions
- Included via `platform()` dependency

**MapStruct**:
- Annotation processor for mapping
- Configure in `kapt` or `annotationProcessor`

**Lombok**:
- Compile-time code generation
- Configure as annotation processor

**Database Drivers**:
- PostgreSQL JDBC driver
- Redis client (Lettuce recommended)

### 7.3 Module Dependencies

**Application Module**:
- `implementation(project(":authservice-domain:domain-application"))`
- Spring Web, Security, Validation
- OpenAPI/Swagger
- MapStruct (for DTO mapping)

**Domain Core Module**:
- Minimal dependencies
- Jakarta Validation (optional)
- No Spring, no JPA

**Domain Application Module**:
- `implementation(project(":authservice-domain:domain-core"))`
- Spring (for DI)
- MapStruct (for mapping interfaces)
- Jakarta Validation

**Data Module**:
- `implementation(project(":authservice-domain:domain-application"))`
- Spring Data JPA
- PostgreSQL driver
- Redis client
- MapStruct (for entity-domain mapping)
- Flyway/Liquibase

---

## 8. Build Configuration

### 8.1 Gradle Settings

**File**: `settings.gradle.kts`

**Configuration**:
- Root project name
- Include all modules
- Plugin management
- Repository configuration

### 8.2 Build Scripts

**Root `build.gradle.kts`**:
- Common plugins for all subprojects
- Dependency version management
- Code quality plugins
- Test configuration

**Module `build.gradle.kts`**:
- Module-specific plugins
- Module-specific dependencies
- Compilation settings
- Test configuration

### 8.3 MapStruct Configuration

**Setup**:
- Add MapStruct dependency
- Configure annotation processor
- Set up in both application and data modules
- Create mapper interfaces in appropriate modules

**Mapper Locations**:
- DTO ↔ Domain: Application module or Domain Application module
- Entity ↔ Domain: Data module
- Use `@Mapper` annotation with appropriate component model

### 8.4 Lombok Configuration

**Setup**:
- Add Lombok dependency
- Configure annotation processor
- Enable in IDE
- Use for reducing boilerplate in entities, DTOs, and configuration classes

**Best Practices**:
- Use `@Data` for simple DTOs
- Use `@Builder` for complex object construction
- Use `@Slf4j` for logging
- Avoid in domain core if it obscures domain logic

---

## 9. Best Practices

### 9.1 Module Organization

**Do**:
- Keep domain core pure (no infrastructure dependencies)
- Use ports and adapters pattern consistently
- Separate concerns clearly between layers
- Keep application layer thin (delegation only)

**Don't**:
- Put business logic in application layer
- Put infrastructure code in domain layer
- Create circular dependencies between modules
- Mix persistence models with domain models

### 9.2 Mapping Strategy

**MapStruct Usage**:
- Use for DTO ↔ Domain mapping (application layer)
- Use for Entity ↔ Domain mapping (data layer)
- Create separate mappers for each direction if needed
- Use `@Mapping` for complex field mappings

**Mapping Flow**:
```
HTTP Request → DTO → Domain Object → Entity → Database
HTTP Response ← DTO ← Domain Object ← Entity ← Database
```

### 9.3 Dependency Injection

**Spring Configuration**:
- Use `@Component` for adapters in data module
- Use `@Service` for application services in domain-application
- Use constructor injection (Lombok `@RequiredArgsConstructor`)
- Avoid field injection

### 9.4 Testing Strategy

**Unit Tests**:
- Domain core: Pure unit tests (no Spring)
- Domain application: Mock dependencies
- Application: Mock domain services
- Data: Use Testcontainers for integration tests

**Integration Tests**:
- Use Testcontainers for PostgreSQL and Redis
- Test full request/response flow
- Test adapter implementations

### 9.5 Code Organization

**Package Structure**:
- Follow Java package naming conventions
- Group by feature/domain concept
- Separate ports (input/output) clearly
- Keep mappers close to their usage

**Naming Conventions**:
- Entities: `User`, `Role`, `Permission`
- Value Objects: `Email`, `UserId`, `PasswordHash`
- Ports: `UserManagementPort`, `UserRepositoryPort`
- Adapters: `JpaUserRepositoryAdapter`, `RedisTokenStorageAdapter`
- Mappers: `UserDtoMapper`, `UserEntityMapper`

### 9.6 Configuration Management

**Profiles**:
- `dev`: Development environment
- `test`: Testing environment
- `prod`: Production environment

**External Configuration**:
- Use `application.yml` for default configuration
- Use profile-specific files for environment overrides
- Externalize sensitive data (use environment variables or secrets management)

### 9.7 Error Handling

**Exception Strategy**:
- Domain exceptions in domain-core
- Application exceptions in application layer
- Map domain exceptions to HTTP responses
- Use global exception handlers

### 9.8 Documentation

**API Documentation**:
- Use OpenAPI/Swagger annotations
- Document all endpoints
- Include request/response examples
- Document error responses

**Code Documentation**:
- Document complex domain logic
- Document port interfaces
- Document adapter implementations
- Use JavaDoc for public APIs

---

## 10. Development Workflow

### 10.1 Initial Setup Steps

1. **Create Project Structure**: Set up all modules and directories
2. **Configure Root Build**: Set up dependency versions and common plugins
3. **Configure Module Builds**: Add module-specific dependencies
4. **Set Up Version Catalog**: Create `libs.versions.toml`
5. **Configure IDE**: Import Gradle project, enable annotation processors
6. **Set Up Database**: Configure PostgreSQL connection
7. **Set Up Redis**: Configure Redis connection
8. **Create Initial Domain Models**: Start with domain-core entities
9. **Create Ports**: Define input/output ports in domain-application
10. **Implement Adapters**: Create adapter implementations in data module
11. **Create Controllers**: Set up REST endpoints in application module
12. **Configure Mappers**: Set up MapStruct mappers

### 10.2 Development Process

1. **Start with Domain**: Define domain models and business rules
2. **Define Ports**: Create port interfaces for use cases
3. **Implement Application Services**: Orchestrate domain operations
4. **Create Adapters**: Implement infrastructure adapters
5. **Build Controllers**: Create REST endpoints
6. **Add Mappers**: Connect layers with MapStruct
7. **Test**: Write unit and integration tests
8. **Document**: Add API documentation and code comments

### 10.3 Build Commands

**Build All Modules**:
```bash
./gradlew build
```

**Build Specific Module**:
```bash
./gradlew :authservice-application:build
```

**Run Application**:
```bash
./gradlew :authservice-application:bootRun
```

**Run Tests**:
```bash
./gradlew test
```

**Generate API Documentation**:
```bash
./gradlew :authservice-application:bootRun
# Access Swagger UI at http://localhost:8080/swagger-ui.html
```

---

## 11. Additional Resources

### 11.1 Recommended Reading

- Domain-Driven Design by Eric Evans
- Implementing Domain-Driven Design by Vaughn Vernon
- Clean Architecture by Robert C. Martin
- Spring Boot Reference Documentation
- MapStruct Documentation
- Gradle User Guide

### 11.2 Useful Tools

- **IDE Plugins**: Lombok, MapStruct, Spring Boot
- **Database Tools**: pgAdmin, DBeaver
- **API Testing**: Postman, Insomnia
- **Redis Tools**: RedisInsight, Redis Commander

### 11.3 Project-Specific Notes

- Ensure all modules compile independently
- Keep domain core testable without Spring
- Use feature branches for development
- Follow semantic versioning for releases
- Maintain clear separation of concerns

---

## Conclusion

This setup guide provides the foundation for building a DDD-based Spring Boot application with clear module separation, proper dependency management, and adherence to domain-driven design principles. Follow the structure and guidelines outlined here to maintain a clean, maintainable, and scalable codebase.

For specific implementation details, refer to the individual module documentation and the main project plan document.


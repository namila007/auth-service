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
10. [Development Workflow](#10-development-workflow)
11. [Common Module Implementation Details](#11-common-module-implementation-details)
12. [API Design Patterns](#12-api-design-patterns)
13. [Additional Resources](#13-additional-resources)
14. [Summary of Key Architectural Decisions](#14-summary-of-key-architectural-decisions)

---

## 1. Project Overview

### 1.1 Architecture Pattern
This project follows **Domain-Driven Design (DDD)** principles with a **multi-module Gradle** structure, separating concerns into distinct layers:

- **Application Layer**: REST controllers, API endpoints, request/response handling
- **Domain Layer**: Core business logic, domain models, ports, and adapters
- **Data Layer**: Database configurations, repositories, external service integrations
- **Common Layer**: Shared base classes, utilities, and common domain abstractions

### 1.2 Module Hierarchy

```
auth-service (Root Project)
├── auth-service-common        # Common/Shared Module
│   └── domain/                # Base domain classes (BaseId, BaseEntity, BaseAggregate)
├── authservice-application    # Application/API Layer
├── authservice-domain         # Domain Layer (Parent Module)
│   ├── domain-core            # Core Domain Logic (Aggregates, Entities, Value Objects)
│   └── domain-application     # Ports & Adapters (Application Services)
└── authservice-data           # Data Layer (Single Module)
    └── data/                  # Java package structure
        ├── identity/          # Identity Context Package
        ├── authorization/     # Authorization Context Package
        ├── configuration/     # Configuration Context Package
        └── governance/        # Governance Context Package
```

### 1.3 Domain Context Modules
Each bounded context has its own set of modules organized by DDD patterns:

```
Context Module Structure:
├── domain-core/{context}/
│   ├── model/
│   │   ├── aggregate/         # XAggregate classes
│   │   ├── entity/            # XEntity classes
│   │   └── value/             # XValue classes
│   ├── service/               # Domain services
│   └── event/                 # Domain events
├── domain-application/{context}/
│   ├── port/
│   │   ├── input/             # Use case interfaces
│   │   └── output/            # Repository interfaces
│   ├── service/               # Application services
│   ├── dto/
│   │   ├── request/           # Request DTOs
│   │   └── response/          # Response DTOs
│   └── mapper/                # Domain-DTO mappers
└── data/{context}/
    ├── entity/                # JPA entities
    ├── repository/            # JPA repositories
    ├── projection/            # Spring Data projections
    ├── adapter/               # Port implementations
    ├── mapper/                # Entity-Domain mappers
    └── config/                # Context-specific configuration
```

**Context-Based Java Packages**:
- `data.identity`: Identity context (User, Profile, FederatedIdentity)
- `data.authorization`: Authorization context (Role, Permission, Policy, UserRoleAssignment)
- `data.configuration`: Configuration context (OIDCProviderConfig, AttributeMapping, etc.)
- `data.governance`: Governance context (AuditLog, AccessCertification)

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

### 2.2 Common Module (`auth-service-common`)

**Purpose**: 
- Shared base classes and abstractions
- Common domain building blocks
- Reusable utilities across all contexts

**Key Components**:

#### 2.2.1 BaseId<T> Interface
Generic identifier interface for all domain identifiers.

**Features**:
- Generic type parameter `T` for ID type (e.g., UUID, Long)
- Static factory methods:
  - `BaseId<T> of(String id)` - Create from string representation
  - `BaseId<T> of(T id)` - Create from typed value
  - `T generate()` - Generate new ID
- Getter and setter for ID value

**Implementation**:
- Uses **UUIDv7** (time-ordered) for ID generation
- Dependency: `com.github.f4b6a3:uuid-creator:5.2.0`
- Generation method: `UuidCreator.getTimeOrderedEpoch()`

**Example Usage**:
```java
public class UserId implements BaseId<UUID> {
    private UUID value;
    
    public static UserId of(String id) {
        return new UserId(UUID.fromString(id));
    }
    
    public static UserId of(UUID id) {
        return new UserId(id);
    }
    
    public static UserId generate() {
        return new UserId(UuidCreator.getTimeOrderedEpoch());
    }
}
```

#### 2.2.2 BaseEntity<ID extends BaseId<?>>
Base class for all domain entities.

**Features**:
- Generic ID type parameter
- Common entity behavior
- Identity equality
- Audit fields (created, modified timestamps)

**Usage**:
- Extended by all entity classes in domain contexts
- Provides consistent identity management

#### 2.2.3 BaseAggregate<ID extends BaseId<?>>
Base class for aggregate roots, extends BaseEntity.

**Features**:
- Aggregate root marker
- Domain event management
- Consistency boundary enforcement
- Transaction boundary marker

**Usage**:
- Extended by all aggregate root classes
- Manages domain events
- Enforces aggregate invariants

**Dependencies**:
- Minimal dependencies (no Spring, no JPA)
- UUID Creator library for ID generation
- Jakarta Validation (optional)

### 2.3 Application Module (`authservice-application`)

**Purpose**: 
- REST API layer
- HTTP request/response handling
- API documentation (OpenAPI/Swagger)
- Security configuration (Spring Security)
- Exception handling
- Input validation
- Pagination support

**Key Components**:
- REST Controllers (with pagination support)
- Request DTOs (separate package)
- Response DTOs (separate package)
- Exception handlers
- API documentation configuration
- Security filters and interceptors

**DTO Structure**:
```
dto/
├── request/          # All request DTOs
│   ├── CreateUserRequest
│   ├── UpdateUserRequest
│   └── LoginRequest
└── response/         # All response DTOs
    ├── UserResponse
    ├── PagedResponse<T>
    └── AuthTokenResponse
```

**Pagination Support**:
- All list endpoints support pagination parameters
- Standard pagination response wrapper
- Page, size, sort parameters
- Total elements and pages in response

**Dependencies**:
- Depends on `auth-service-common`
- Depends on `authservice-domain` (domain-application submodule)
- Spring Web, Spring Security
- OpenAPI/Swagger
- Validation API

### 2.4 Domain Module (`authservice-domain`)

**Purpose**: 
- Core business logic organized by bounded contexts
- Domain models and rules
- Application services (ports)
- Adapter interfaces

**Context-Based Organization**:
Each bounded context has its own module structure within the domain layer.

#### 2.4.1 Domain Core (`domain-core`)

**Purpose**: 
- Pure domain logic per context
- Business rules and invariants
- Domain aggregates, entities, and value objects
- Domain events
- Domain exceptions

**Naming Conventions**:
- **Aggregates**: `XAggregate` (e.g., `UserAggregate`, `RoleAggregate`)
- **Entities**: `XEntity` (e.g., `ProfileEntity`, `AddressEntity`)
- **Value Objects**: `XValue` (e.g., `EmailValue`, `PasswordValue`)

**Package Structure per Context**:
```
{context}-domain/
├── model/
│   ├── aggregate/
│   │   ├── UserAggregate.java      # extends BaseAggregate<UserId>
│   │   └── RoleAggregate.java
│   ├── entity/
│   │   ├── ProfileEntity.java      # extends BaseEntity<ProfileId>
│   │   └── PermissionEntity.java
│   └── value/
│       ├── EmailValue.java
│       ├── PasswordValue.java
│       └── UsernameValue.java
├── service/
│   └── UserDomainService.java      # Domain services
└── event/
    ├── UserCreatedEvent.java
    └── UserActivatedEvent.java
```

**Key Components**:
- Aggregate classes extending `BaseAggregate<ID>`
- Entity classes extending `BaseEntity<ID>`
- Value Objects (immutable)
- Domain Services (stateless business logic)
- Domain Events
- Business logic validations
- ID classes implementing `BaseId<UUID>`

**Dependencies**:
- Depends on `auth-service-common`
- Minimal dependencies (no Spring, no JPA)
- UUID Creator (via common module)
- Jakarta Validation (for domain rules)

#### 2.4.2 Domain Application (`domain-application`)

**Purpose**: 
- Application services (use cases) per context
- Port interfaces (input/output ports)
- Adapter interfaces
- Request/Response DTOs
- Domain-to-DTO mapping interfaces
- Use case orchestration

**Package Structure per Context**:
```
{context}-application/
├── port/
│   ├── input/
│   │   ├── UserManagementUseCase.java
│   │   └── AuthenticationUseCase.java
│   └── output/
│       ├── UserRepositoryPort.java
│       └── TokenStoragePort.java
├── service/
│   └── UserApplicationService.java
├── dto/
│   ├── request/
│   │   ├── CreateUserRequest.java
│   │   ├── UpdateUserRequest.java
│   │   └── SearchUserRequest.java
│   └── response/
│       ├── UserResponse.java
│       ├── UserDetailResponse.java
│       └── UserSummaryResponse.java
└── mapper/
    └── UserDtoMapper.java          # MapStruct mapper
```

**DTO Organization**:
- **Request DTOs**: All API request models in `dto/request/`
- **Response DTOs**: All API response models in `dto/response/`
- Separate DTOs for different use cases (create, update, summary, detail)
- Pagination support in response DTOs

**Key Components**:
- Port interfaces (Input/Output)
- Application Services
- Use Case implementations
- Request DTOs (validation annotations)
- Response DTOs (with pagination support)
- Domain-to-DTO mapper interfaces (MapStruct)
- Command/Query handlers

**Dependencies**:
- Depends on `auth-service-common`
- Depends on `domain-core`
- MapStruct (for mapping interfaces)
- Spring (for dependency injection)
- Jakarta Validation

### 2.5 Data Module (`authservice-data`)

**Purpose**: 
- Single Gradle module with Java packages organized by bounded context
- Infrastructure implementations per bounded context
- Database configurations (shared)
- Repository implementations per context
- External service adapters per context
- JPA entities and repositories per context
- Spring Data Projections for optimized queries per context

**Package Structure**:
```
authservice-data/              # Single Gradle Module
└── src/main/java/me/namila/service/auth/data/
    ├── identity/              # Identity Context Package
    │   ├── entity/
    │   │   ├── UserJpaEntity.java
    │   │   ├── UserProfileJpaEntity.java
    │   │   └── FederatedIdentityJpaEntity.java
    │   ├── repository/
    │   │   ├── UserJpaRepository.java
    │   │   ├── UserProfileJpaRepository.java
    │   │   └── FederatedIdentityJpaRepository.java
    │   ├── projection/
    │   │   ├── UserSummaryProjection.java
    │   │   ├── UserProfileSummaryProjection.java
    │   │   └── FederatedIdentitySummaryProjection.java
    │   ├── adapter/
    │   │   ├── UserRepositoryAdapter.java
    │   │   └── FederatedIdentityRepositoryAdapter.java
    │   ├── mapper/
    │   │   ├── UserEntityMapper.java
    │   │   └── FederatedIdentityEntityMapper.java
    │   └── config/
    │       └── IdentityDataConfig.java
    │
    ├── authorization/         # Authorization Context Package
    │   ├── entity/
    │   │   ├── RoleJpaEntity.java
    │   │   ├── PermissionJpaEntity.java
    │   │   ├── PolicyJpaEntity.java
    │   │   └── UserRoleAssignmentJpaEntity.java
    │   ├── repository/
    │   │   ├── RoleJpaRepository.java
    │   │   ├── PermissionJpaRepository.java
    │   │   ├── PolicyJpaRepository.java
    │   │   └── UserRoleAssignmentJpaRepository.java
    │   ├── projection/
    │   │   ├── RoleSummaryProjection.java
    │   │   ├── PermissionSummaryProjection.java
    │   │   └── UserRoleAssignmentSummaryProjection.java
    │   ├── adapter/
    │   │   ├── RoleRepositoryAdapter.java
    │   │   ├── PermissionRepositoryAdapter.java
    │   │   ├── PolicyRepositoryAdapter.java
    │   │   └── UserRoleAssignmentRepositoryAdapter.java
    │   ├── mapper/
    │   │   ├── RoleEntityMapper.java
    │   │   ├── PermissionEntityMapper.java
    │   │   ├── PolicyEntityMapper.java
    │   │   └── UserRoleAssignmentEntityMapper.java
    │   └── config/
    │       └── AuthorizationDataConfig.java
    │
    ├── configuration/         # Configuration Context Package
    │   ├── entity/
    │   │   ├── OIDCProviderConfigJpaEntity.java
    │   │   └── AttributeMappingJpaEntity.java
    │   ├── repository/
    │   │   ├── OIDCProviderConfigJpaRepository.java
    │   │   └── AttributeMappingJpaRepository.java
    │   ├── projection/
    │   │   └── OIDCProviderConfigSummaryProjection.java
    │   ├── adapter/
    │   │   └── OIDCProviderConfigRepositoryAdapter.java
    │   ├── mapper/
    │   │   └── OIDCProviderConfigEntityMapper.java
    │   └── config/
    │       └── ConfigurationDataConfig.java
    │
    ├── governance/            # Governance Context Package
    │   ├── entity/
    │   │   ├── AuditLogJpaEntity.java
    │   │   └── AccessCertificationJpaEntity.java
    │   ├── repository/
    │   │   ├── AuditLogJpaRepository.java
    │   │   └── AccessCertificationJpaRepository.java
    │   ├── projection/
    │   │   ├── AuditLogSummaryProjection.java
    │   │   └── AccessCertificationSummaryProjection.java
    │   ├── adapter/
    │   │   ├── AuditLogRepositoryAdapter.java
    │   │   └── AccessCertificationRepositoryAdapter.java
    │   ├── mapper/
    │   │   ├── AuditLogEntityMapper.java
    │   │   └── AccessCertificationEntityMapper.java
    │   └── config/
    │       └── GovernanceDataConfig.java
    │
    └── config/                # Shared Configuration Package
        ├── DatabaseConfig.java
        ├── RedisConfig.java
        └── JpaConfig.java
```

**Shared Configuration** (in `data.config` package):
- PostgreSQL connection configuration
- Redis configuration
- Database migration configuration (Flyway)
- Common JPA/Hibernate settings
- Transaction management configuration

**Benefits of Context-Based Data Modules**:
- **Clear Separation**: Each bounded context has its own data module, making dependencies explicit
- **Independent Evolution**: Contexts can evolve independently without affecting others
- **Reduced Coupling**: Data modules only depend on their corresponding domain-application context
- **Better Organization**: Entities, repositories, projections, adapters, and mappers are grouped by context
- **Easier Testing**: Each context can be tested in isolation
- **Scalability**: New contexts can be added without modifying existing modules
- **Maintainability**: Changes to one context don't impact others

**Spring Data Projections**:
- Used for parent-child relationships
- Child entities returned as projections with minimal data
- Reduces data transfer and improves performance
- Full child details available via separate API endpoints

**Projection Strategy**:
```java
// Example: User with Role projections
public interface UserSummaryProjection {
    UUID getId();
    String getUsername();
    List<RoleSummaryProjection> getRoles();  // Minimal role data
}

public interface RoleSummaryProjection {
    UUID getId();
    String getName();
    // No permissions or other heavy data
}

// Full role details available via: GET /api/roles/{id}
```

**Parent-Child Relationship Handling**:
- Parent entities include child projections (minimal data)
- Child details fetched separately when needed
- Avoids N+1 query problems
- Supports pagination for both parent and child collections

**Key Components**:
- JPA Entity classes (persistence models)
- Spring Data JPA repositories
- Projection interfaces for optimized queries
- Database configuration classes
- Redis configuration and clients
- PostgreSQL connection settings
- Adapter implementations (for domain ports)
- Entity-to-Domain mappers (MapStruct)

**Dependencies**:
- Depends on `auth-service-common`
- Depends on `authservice-domain:domain-application` (all contexts)
- Spring Data JPA
- PostgreSQL driver
- Redis client (Lettuce/Jedis)
- MapStruct (for entity-domain mapping)
- Flyway/Liquibase (for migrations)

**Package Organization**:
- Each context package (`data.identity`, `data.authorization`, etc.) is self-contained
- Each context package has its own entity, repository, projection, adapter, mapper, and config sub-packages
- Shared configuration in `data.config` package
- Clear separation of concerns through Java package structure

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
- **UUID Creator**: `com.github.f4b6a3:uuid-creator:5.2.0` for UUIDv7 generation

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
├── auth-service-common/         # Common/Shared Module
│   ├── build.gradle.kts
│   └── src/
│       └── main/java/me/namila/service/auth/common/
│           └── domain/
│               ├── BaseId.java          # Generic ID interface
│               ├── BaseEntity.java      # Base entity class
│               └── BaseAggregate.java   # Base aggregate class
├── authservice-application/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── java/me/namila/service/auth/application/
│       │   │   ├── controller/
│       │   │   ├── dto/
│       │   │   │   ├── request/         # Request DTOs
│       │   │   │   └── response/        # Response DTOs
│       │   │   ├── config/
│       │   │   └── exception/
│       │   └── resources/
│       │       ├── application.yml
│       │       └── application-dev.yml
│       └── test/
├── authservice-domain/
│   ├── build.gradle.kts         # Parent module build
│   ├── domain-core/
│   │   ├── build.gradle.kts
│   │   └── src/
│   │       └── main/java/me/namila/service/auth/domain/core/
│   │           └── {context}/   # e.g., identity, authorization
│   │               ├── model/
│   │               │   ├── aggregate/   # XAggregate classes
│   │               │   ├── entity/      # XEntity classes
│   │               │   └── value/       # XValue classes
│   │               ├── service/         # Domain services
│   │               └── event/           # Domain events
│   └── domain-application/
│       ├── build.gradle.kts
│       └── src/
│           └── main/java/me/namila/service/auth/domain/application/
│               └── {context}/   # e.g., identity, authorization
│                   ├── port/
│                   │   ├── input/       # Use case interfaces
│                   │   └── output/      # Repository interfaces
│                   ├── service/         # Application services
│                   ├── dto/
│                   │   ├── request/     # Request DTOs
│                   │   └── response/    # Response DTOs
│                   └── mapper/          # MapStruct mappers
└── authservice-data/            # Data Layer (Single Module)
    ├── build.gradle.kts
    └── src/
        └── main/java/me/namila/service/auth/data/
            ├── identity/           # Identity Context Package
            │   ├── entity/          # JPA entities
            │   ├── repository/     # Spring Data repositories
            │   ├── projection/     # Spring Data projections
            │   ├── adapter/        # Port implementations
            │   ├── mapper/         # Entity-Domain mappers
            │   └── config/         # Context-specific config
            ├── authorization/      # Authorization Context Package
            │   ├── entity/
            │   ├── repository/
            │   ├── projection/
            │   ├── adapter/
            │   ├── mapper/
            │   └── config/
            ├── configuration/      # Configuration Context Package
            │   ├── entity/
            │   ├── repository/
            │   ├── projection/
            │   ├── adapter/
            │   ├── mapper/
            │   └── config/
            ├── governance/         # Governance Context Package
            │   ├── entity/
            │   ├── repository/
            │   ├── projection/
            │   ├── adapter/
            │   ├── mapper/
            │   └── config/
            └── config/             # Shared Configuration Package
                ├── DatabaseConfig.java
                ├── RedisConfig.java
                └── JpaConfig.java
```

**Key Structure Notes**:
- Each bounded context (e.g., `identity`, `authorization`) has its own package structure
- Common base classes in `auth-service-common` module
- Consistent naming: `XAggregate`, `XEntity`, `XValue`
- Separate `request` and `response` DTO packages
- Projection interfaces for optimized queries

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
- **Aggregates** (`XAggregate`): Extend `BaseAggregate<ID>`, consistency boundaries, transaction boundaries
- **Entities** (`XEntity`): Extend `BaseEntity<ID>`, rich domain models with behavior
- **Value Objects** (`XValue`): Immutable objects representing domain concepts
- **ID Classes**: Implement `BaseId<UUID>`, use UUIDv7 for generation
- **Domain Services**: Operations that don't belong to a single entity
- **Domain Events**: Important business occurrences

**Naming Convention Examples**:
```java
// Aggregates - Must use @SuperBuilder
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserAggregate extends BaseAggregate<UserId> {
    private UsernameValue username;
    private EmailValue email;
    // ... other fields
}

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAggregate extends BaseAggregate<RoleId> {
    private String roleName;
    // ... other fields
}

// Entities - Must use @SuperBuilder
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEntity extends BaseEntity<ProfileId> {
    private String firstName;
    private String lastName;
    // ... other fields
}

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntity extends BaseEntity<AddressId> {
    private String street;
    // ... other fields
}

// Value Objects - Use regular @Builder (no inheritance)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailValue {
    private String value;
}

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordValue {
    private String value;
}

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsernameValue {
    private String value;
}

// ID Classes
public class UserId implements BaseId<UUID> {
    private UUID value;
    
    public static UserId generate() {
        return new UserId(UuidCreator.getTimeOrderedEpoch());
    }
}
```

**Important**: 
- **Aggregates and Entities** must use `@SuperBuilder` (not `@Builder`) to inherit builder capabilities from base classes
- **Value Objects** use regular `@Builder` since they don't extend base classes
- Using `@SuperBuilder` allows setting inherited fields (`id`, `createdAt`, `updatedAt`) via builder pattern
- This eliminates the need for reflection in unit tests to set protected fields

**Domain Application**:
- **Use Cases**: Application-level operations (input ports)
- **Commands**: Intent to change state
- **Queries**: Intent to read data
- **Application Services**: Orchestrate domain operations
- **Request DTOs**: Separate package for all request models
- **Response DTOs**: Separate package for all response models

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

**UUID Creator**:
- Library: `com.github.f4b6a3:uuid-creator:5.2.0`
- Used in `auth-service-common` module
- Provides UUIDv7 generation via `UuidCreator.getTimeOrderedEpoch()`

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

**Common Module** (`auth-service-common`):
- UUID Creator: `com.github.f4b6a3:uuid-creator:5.2.0`
- Jakarta Validation (optional)
- No Spring, no JPA
- Pure domain abstractions

**Application Module**:
- `implementation(project(":auth-service-common"))`
- `implementation(project(":authservice-domain:domain-application"))`
- Spring Web, Security, Validation
- OpenAPI/Swagger
- MapStruct (for DTO mapping)

**Domain Core Module**:
- `implementation(project(":auth-service-common"))`
- Minimal dependencies
- Jakarta Validation (optional)
- No Spring, no JPA

**Domain Application Module**:
- `implementation(project(":auth-service-common"))`
- `implementation(project(":authservice-domain:domain-core"))`
- Spring (for DI)
- MapStruct (for mapping interfaces)
- Jakarta Validation

**Data Module** (`authservice-data`):
- `implementation(project(":auth-service-common"))`
- `implementation(project(":authservice-domain:domain-application"))`
- Spring Data JPA
- PostgreSQL driver
- Redis client
- Flyway/Liquibase
- MapStruct (for entity-domain mapping)
- Common JPA/Hibernate configuration
- All context packages share the same dependencies

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
- Use for Request DTO → Domain mapping (domain-application layer)
- Use for Domain → Response DTO mapping (domain-application layer)
- Use for JPA Entity ↔ Domain mapping (data layer)
- Create separate mappers for each direction if needed
- Use `@Mapping` for complex field mappings
- **Always configure**: `@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)`
- **Always ignore lazy collections**: `@Mapping(target = "lazyCollection", ignore = true)`

**MapStruct Configuration**:
```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserEntityMapper {
    // MapStruct automatically skips uninitialized lazy collections
    @Mapping(target = "orders", ignore = true) // Explicitly ignore lazy collection
    UserDTO toDTO(UserEntity entity);
}
```

**Mapping Flow**:
```
HTTP Request → Request DTO → Domain Aggregate/Entity → JPA Entity → Database
HTTP Response ← Response DTO ← Domain Aggregate/Entity ← JPA Entity ← Database
                                     ↓
                              (with Projections for child data)
```

**DTO Organization**:
- Request DTOs in `{context}-application/dto/request/`
- Response DTOs in `{context}-application/dto/response/`
- Separate DTOs for create, update, summary, detail views
- Pagination wrappers for list responses
- **Never include lazy collections in DTOs** - use projections or separate endpoints

**Projection Usage**:
- Define projection interfaces in data layer
- Use for parent-child relationships
- Return minimal child data in parent responses
- Full child details via separate endpoints
- **DB-level projection is more efficient** than entity + MapStruct for list views

**MapStruct vs Projection Decision**:
```
Need full entity for updates/deletes?
├─ YES → Use Entity + MapStruct (ignore lazy collections)
└─ NO → Need complex mapping logic?
    ├─ YES → Use Entity + MapStruct
    └─ NO → Use Projection (best performance)
```

### 9.3 JPA Best Practices

**Transaction Management**:
- Default to `@Transactional(readOnly = true)` for all read operations
- Use `@Transactional` (read-write) only for write operations
- Service-level transactions are preferred over repository-level

**N+1 Query Prevention**:
- **Use Projections** for list views (most efficient)
- **Use JOIN FETCH** when you need full entities with relationships
- **Use ID-First Pattern** for complex searches:
  1. Search IDs (lightweight query)
  2. Batch fetch entities with JOIN FETCH
  3. Map to DTOs preserving order
- **Never use FetchType.EAGER** - always use LAZY
- **Never access lazy collections** without JOIN FETCH or explicit initialization

**Repository Patterns**:
```java
// ID-First Pattern for complex searches
@Query("SELECT u.id FROM User u WHERE u.name LIKE %:keyword%")
Page<UUID> searchIds(@Param("keyword") String keyword, Pageable pageable);

@Query("SELECT u FROM User u JOIN FETCH u.profile WHERE u.id IN :ids")
List<User> findByIdsWithProfile(@Param("ids") List<UUID> ids);

// Projection for list views
@Query("SELECT u.id as id, u.name as name, u.email as email FROM User u")
Page<UserProjection> findAllProjected(Pageable pageable);
```

**Pagination Best Practices**:
- Always use Spring Data `Pageable` for pagination
- Return `Page<DTO>` or `Page<Projection>` from services
- Use `PagedResponse<T>` wrapper for consistent API responses
- Default page size: 20, max: 100
- Always sort by indexed columns

**Soft Delete Pattern** (if needed):
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseJpaEntity {
    @Column(nullable = false)
    private Boolean deleted = false;
    
    private LocalDateTime deletedAt;
}

@Entity
@SQLDelete(sql = "UPDATE users SET deleted = true, deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted = false")
public class UserJpaEntity extends BaseJpaEntity { }
```

**Performance Optimization**:
- Add indexes on filtered/sorted columns
- Use composite indexes for multi-column filters
- Configure batch size: `hibernate.jdbc.batch_size=20`
- Enable `hibernate.order_inserts=true` and `hibernate.order_updates=true`
- Use covering indexes for frequent queries

**MapStruct with Lazy Collections**:
- MapStruct automatically skips uninitialized lazy collections
- Always use `unmappedTargetPolicy = ReportingPolicy.IGNORE`
- Explicitly ignore collections: `@Mapping(target = "orders", ignore = true)`
- Only map collections when explicitly JOIN FETCHed

### 9.4 Dependency Injection

**Spring Configuration**:
- Use `@Component` for adapters in data module
- Use `@Service` for application services in domain-application
- Use constructor injection (Lombok `@RequiredArgsConstructor`)
- Avoid field injection

### 9.5 Testing Strategy

**Unit Tests**:
- Domain core: Pure unit tests (no Spring)
- Domain application: Mock dependencies
- Application: Mock domain services
- Data: Use Testcontainers for integration tests

**Integration Tests**:
- Use Testcontainers for PostgreSQL and Redis
- Test full request/response flow
- Test adapter implementations

### 9.6 Code Organization

**Package Structure**:
- Follow Java package naming conventions
- Group by bounded context (e.g., `identity`, `authorization`)
- Separate ports (input/output) clearly
- Keep mappers close to their usage
- Separate request and response DTOs

**Naming Conventions**:
- **Aggregates**: `UserAggregate`, `RoleAggregate`, `PermissionAggregate` (must use `@SuperBuilder`)
- **Entities**: `ProfileEntity`, `AddressEntity`, `AuditEntity` (must use `@SuperBuilder`)
- **Value Objects**: `EmailValue`, `PasswordValue`, `UsernameValue` (use regular `@Builder`)
- **ID Classes**: `UserId`, `RoleId`, `PermissionId` (implement `BaseId<UUID>`)
- **Ports**: `UserManagementUseCase`, `UserRepositoryPort`
- **Adapters**: `UserRepositoryAdapter`, `TokenStorageAdapter`
- **Mappers**: `UserDtoMapper`, `UserEntityMapper`
- **Request DTOs**: `CreateUserRequest`, `UpdateUserRequest`
- **Response DTOs**: `UserResponse`, `UserDetailResponse`, `UserSummaryResponse`
- **Projections**: `UserSummaryProjection`, `RoleSummaryProjection`

**Builder Pattern**:
- **BaseEntity** and **BaseAggregate** use `@SuperBuilder` to enable builder pattern for inherited fields
- **All aggregates and entities** must use `@SuperBuilder` (not `@Builder`) to inherit builder capabilities
- **Value objects** use regular `@Builder` since they don't extend base classes
- **Benefits**:
  - Can set inherited fields (`id`, `createdAt`, `updatedAt`) via builder
  - No need for reflection in unit tests
  - Cleaner test code
  - Better MapStruct integration

**Context Organization**:
```
domain/core/{context}/model/
  ├── aggregate/    # XAggregate
  ├── entity/       # XEntity
  └── value/        # XValue

domain/application/{context}/dto/
  ├── request/      # Request DTOs
  └── response/     # Response DTOs

data/{context}/
  ├── entity/       # JPA entities
  ├── repository/   # Spring Data repositories
  ├── projection/    # Projection interfaces
  ├── adapter/      # Port implementations
  ├── mapper/       # Entity-Domain mappers
  └── config/       # Context-specific configuration
```

### 9.7 Configuration Management

**Profiles**:
- `dev`: Development environment
- `test`: Testing environment
- `prod`: Production environment

**External Configuration**:
- Use `application.yml` for default configuration
- Use profile-specific files for environment overrides
- Externalize sensitive data (use environment variables or secrets management)

### 9.8 Error Handling

**Exception Strategy**:
- Domain exceptions in domain-core
- Application exceptions in application layer
- Map domain exceptions to HTTP responses
- Use global exception handlers

### 9.9 Documentation

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
4. **Set Up Version Catalog**: Create `libs.versions.toml` with UUID Creator dependency
5. **Create Common Module**: Implement `BaseId<T>`, `BaseEntity<ID>`, `BaseAggregate<ID>`
6. **Configure IDE**: Import Gradle project, enable annotation processors
7. **Set Up Database**: Configure PostgreSQL connection
8. **Set Up Redis**: Configure Redis connection
9. **Identify Bounded Contexts**: Define context boundaries (e.g., identity, authorization)
10. **Create Initial Domain Models**: 
    - Define ID classes implementing `BaseId<UUID>`
    - Create aggregates extending `BaseAggregate<ID>`
    - Create entities extending `BaseEntity<ID>`
    - Create value objects (XValue)
11. **Create Ports**: Define input/output ports in domain-application per context
12. **Create DTOs**: 
    - Request DTOs in `dto/request/`
    - Response DTOs in `dto/response/`
    - Pagination response wrappers
13. **Implement Adapters**: Create adapter implementations in data module per context
14. **Create Projections**: Define projection interfaces for parent-child relationships
15. **Create Controllers**: Set up REST endpoints with pagination support
16. **Configure Mappers**: Set up MapStruct mappers for DTO and Entity mappings

### 10.2 Development Process

**For Each Bounded Context**:

1. **Start with Domain Core**:
   - Define ID classes implementing `BaseId<UUID>` with UUIDv7 generation
   - Create aggregates (XAggregate) extending `BaseAggregate<ID>`
   - Create entities (XEntity) extending `BaseEntity<ID>`
   - Create value objects (XValue)
   - Define domain services and events

2. **Define Application Layer**:
   - Create input ports (use case interfaces)
   - Create output ports (repository interfaces)
   - Define Request DTOs in `dto/request/`
   - Define Response DTOs in `dto/response/`
   - Implement application services

3. **Implement Data Layer**:
   - Create JPA entities
   - Define projection interfaces for parent-child relationships
   - Implement Spring Data repositories
   - Create adapters implementing output ports
   - Configure entity-domain mappers

4. **Build API Layer**:
   - Create REST controllers with pagination support
   - Configure domain-DTO mappers
   - Add validation and exception handling
   - Document endpoints with OpenAPI

5. **Test**:
   - Unit test domain logic (no Spring)
   - Integration test with Testcontainers
   - Test pagination and projections
   - Test API endpoints

6. **Document**:
   - Add API documentation
   - Document domain invariants
   - Document projection usage

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

## 11. Common Module Implementation Details

### 11.1 BaseId<T> Interface

**Purpose**: Generic identifier interface for type-safe domain identifiers.

**Implementation**:
```java
public interface BaseId<T> {
    T getValue();
    void setValue(T value);
    
    // Static factory methods to be implemented by concrete classes
    static <ID extends BaseId<T>, T> ID of(String id) {
        throw new UnsupportedOperationException("Must be implemented by concrete class");
    }
    
    static <ID extends BaseId<T>, T> ID of(T id) {
        throw new UnsupportedOperationException("Must be implemented by concrete class");
    }
    
    static <ID extends BaseId<T>, T> ID generate() {
        throw new UnsupportedOperationException("Must be implemented by concrete class");
    }
}
```

**Concrete Implementation Example**:
```java
public class UserId implements BaseId<UUID> {
    private UUID value;
    
    private UserId(UUID value) {
        this.value = value;
    }
    
    public static UserId of(String id) {
        return new UserId(UUID.fromString(id));
    }
    
    public static UserId of(UUID id) {
        return new UserId(id);
    }
    
    public static UserId generate() {
        return new UserId(UuidCreator.getTimeOrderedEpoch());
    }
    
    @Override
    public UUID getValue() {
        return value;
    }
    
    @Override
    public void setValue(UUID value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
```

### 11.2 BaseEntity<ID extends BaseId<?>>

**Purpose**: Base class for all domain entities providing identity and audit fields.

**Implementation**:
```java
@SuperBuilder
public abstract class BaseEntity<ID extends BaseId<?>> {
    private ID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    protected BaseEntity() {
    }
    
    protected BaseEntity(ID id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public ID getId() {
        return id;
    }
    
    protected void setId(ID id) {
        this.id = id;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    protected void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    protected void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    protected void markAsUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

**SuperBuilder Pattern**:
- `BaseEntity` uses `@SuperBuilder` to enable builder pattern for inherited fields
- All entities extending `BaseEntity` must use `@SuperBuilder` (not `@Builder`)
- This allows setting `createdAt` and `updatedAt` via builder in tests and mappers
- Example usage:
```java
UserProfileEntity profile = UserProfileEntity.builder()
    .id(profileId)
    .firstName("John")
    .lastName("Doe")
    .createdAt(LocalDateTime.now())  // Can set superclass fields
    .updatedAt(LocalDateTime.now())  // Can set superclass fields
    .build();
```

### 11.3 BaseAggregate<ID extends BaseId<?>>

**Purpose**: Base class for aggregate roots, extends BaseEntity with domain event support.

**Implementation**:
```java
@SuperBuilder
public abstract class BaseAggregate<ID extends BaseId<?>> extends BaseEntity<ID> {
    private final List<Object> domainEvents = new ArrayList<>();
    
    protected BaseAggregate() {
        super();
    }
    
    protected BaseAggregate(ID id) {
        super(id);
    }
    
    protected void registerDomainEvent(Object event) {
        if (event != null) {
            domainEvents.add(event);
        }
    }
    
    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    public void clearDomainEvents() {
        domainEvents.clear();
    }
    
    public boolean hasDomainEvents() {
        return !domainEvents.isEmpty();
    }
}
```

**SuperBuilder Pattern**:
- `BaseAggregate` uses `@SuperBuilder` to enable builder pattern for inherited fields from both `BaseAggregate` and `BaseEntity`
- All aggregates extending `BaseAggregate` must use `@SuperBuilder` (not `@Builder`)
- This allows setting `id`, `createdAt`, and `updatedAt` via builder in tests and mappers
- Example usage:
```java
UserAggregate user = UserAggregate.builder()
    .id(userId)
    .username(UsernameValue.of("testuser"))
    .email(EmailValue.of("test@example.com"))
    .createdAt(LocalDateTime.now())  // Can set BaseEntity fields
    .updatedAt(LocalDateTime.now())  // Can set BaseEntity fields
    .build();
```

### 11.4 UUIDv7 Generation

**Why UUIDv7?**
- Time-ordered: Better database indexing performance
- Sortable: Natural ordering by creation time
- Compatible: Standard UUID format
- Unique: Globally unique identifiers

**Usage**:
```java
import com.github.f4b6a3.uuid.UuidCreator;

// Generate time-ordered UUID (v7)
UUID id = UuidCreator.getTimeOrderedEpoch();
```

**Dependency**:
```xml
<dependency>
    <groupId>com.github.f4b6a3</groupId>
    <artifactId>uuid-creator</artifactId>
    <version>5.2.0</version>
</dependency>
```

---

## 12. API Design Patterns

### 12.1 Pagination Support

**Standard Pagination Parameters**:
- `page`: Page number (0-based)
- `size`: Number of items per page
- `sort`: Sort field and direction (e.g., `createdAt,desc`)

**Pagination Response Wrapper**:
```java
public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    
    // Constructor, getters, setters
}
```

**Controller Example**:
```java
@GetMapping("/users")
public ResponseEntity<PagedResponse<UserResponse>> getUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size,
    @RequestParam(defaultValue = "createdAt,desc") String sort) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
    Page<UserResponse> users = userService.findAll(pageable);
    
    return ResponseEntity.ok(new PagedResponse<>(users));
}
```

### 12.2 Parent-Child Relationship API Pattern

**Strategy**: Use projections for child data in parent responses, provide separate endpoints for full child details.

**Example**:

**Get User with Roles (Projection)**:
```
GET /api/users/{userId}
Response:
{
  "id": "uuid",
  "username": "john.doe",
  "email": "john@example.com",
  "roles": [
    {
      "id": "uuid",
      "name": "ADMIN"
      // Minimal role data only
    }
  ]
}
```

**Get Full Role Details**:
```
GET /api/roles/{roleId}
Response:
{
  "id": "uuid",
  "name": "ADMIN",
  "description": "Administrator role",
  "permissions": [
    {
      "id": "uuid",
      "name": "USER_WRITE",
      "resource": "USER",
      "action": "WRITE"
    }
  ],
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

**Benefits**:
- Reduced payload size
- Avoids N+1 query problems
- Clear separation of concerns
- Better performance
- Supports pagination for both parent and child collections

### 12.3 DTO Separation Pattern

**Request DTOs** (`dto/request/`):
- Validation annotations
- Only fields needed for the operation
- No ID for create operations
- Required ID for update operations

**Response DTOs** (`dto/response/`):
- Always include ID
- Include timestamps
- May include computed fields
- Different DTOs for different views (summary, detail)

**Example**:
```java
// Request DTO
public class CreateUserRequest {
    @NotBlank
    private String username;
    
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 8)
    private String password;
}

// Response DTO (Summary)
public class UserSummaryResponse {
    private UUID id;
    private String username;
    private String email;
}

// Response DTO (Detail)
public class UserDetailResponse {
    private UUID id;
    private String username;
    private String email;
    private List<RoleSummaryProjection> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

---

## 13. Additional Resources

### 13.1 Recommended Reading

- Domain-Driven Design by Eric Evans
- Implementing Domain-Driven Design by Vaughn Vernon
- Clean Architecture by Robert C. Martin
- Spring Boot Reference Documentation
- MapStruct Documentation
- Gradle User Guide
- UUID Creator Documentation: https://github.com/f4b6a3/uuid-creator

### 13.2 Useful Tools

- **IDE Plugins**: Lombok, MapStruct, Spring Boot
- **Database Tools**: pgAdmin, DBeaver
- **API Testing**: Postman, Insomnia
- **Redis Tools**: RedisInsight, Redis Commander

### 13.3 Project-Specific Notes

- Ensure all modules compile independently
- Keep domain core testable without Spring
- Use feature branches for development
- Follow semantic versioning for releases
- Maintain clear separation of concerns
- All ID classes must implement `BaseId<UUID>` and use UUIDv7
- All aggregates must extend `BaseAggregate<ID>`
- All entities must extend `BaseEntity<ID>`
- Use naming conventions: `XAggregate`, `XEntity`, `XValue`
- Separate request and response DTOs in distinct packages
- Use Spring Data Projections for parent-child relationships
- Support pagination on all list endpoints
- Organize code by bounded context

---

## 14. Summary of Key Architectural Decisions

### 14.1 Base Classes
- **BaseId<T>**: Generic identifier interface with UUIDv7 generation
- **BaseEntity<ID>**: Base class for entities with identity and audit fields (uses `@SuperBuilder`)
- **BaseAggregate<ID>**: Base class for aggregates with domain event support (uses `@SuperBuilder`)
- **SuperBuilder Pattern**: All aggregates and entities use `@SuperBuilder` to enable builder pattern for inherited fields (`id`, `createdAt`, `updatedAt`)

### 14.2 Naming Conventions
- Aggregates: `XAggregate` (e.g., `UserAggregate`) - must use `@SuperBuilder`
- Entities: `XEntity` (e.g., `ProfileEntity`) - must use `@SuperBuilder`
- Value Objects: `XValue` (e.g., `EmailValue`) - use regular `@Builder`
- ID Classes: `XId` implementing `BaseId<UUID>`

### 14.3 Module Organization
- **auth-service-common**: Shared base classes and utilities
- **domain-core**: Domain models organized by bounded context
- **domain-application**: Ports, services, and DTOs per context
- **data**: Single Gradle module with Java packages organized by bounded context
  - **data.identity**: Identity context JPA entities, repositories, projections, adapters, and mappers
  - **data.authorization**: Authorization context JPA entities, repositories, projections, adapters, and mappers
  - **data.configuration**: Configuration context JPA entities, repositories, projections, adapters, and mappers
  - **data.governance**: Governance context JPA entities, repositories, projections, adapters, and mappers
  - **data.config**: Shared database and infrastructure configuration
- **application**: REST controllers and API layer

### 14.4 DTO Strategy
- Separate `request` and `response` packages
- Multiple response DTOs for different views (summary, detail)
- Pagination wrappers for list responses
- Request DTOs with validation annotations

### 14.5 Data Access Patterns
- Spring Data Projections for parent-child relationships
- Minimal child data in parent responses
- Separate endpoints for full child details
- Pagination support on all collection endpoints
- Avoid N+1 query problems

### 14.6 ID Generation
- UUIDv7 (time-ordered) via `UuidCreator.getTimeOrderedEpoch()`
- Better database indexing performance
- Natural sorting by creation time
- Library: `com.github.f4b6a3:uuid-creator:5.2.0`

---

## Conclusion

This setup guide provides the foundation for building a DDD-based Spring Boot application with:
- Clear module separation and bounded context organization
- Type-safe identifiers using `BaseId<T>` with UUIDv7
- Consistent base classes for entities and aggregates
- Proper dependency management and layer isolation
- Efficient data access with projections and pagination
- Adherence to domain-driven design principles

Follow the structure and guidelines outlined here to maintain a clean, maintainable, and scalable codebase. The common module provides reusable abstractions, while context-specific modules ensure clear boundaries and separation of concerns.

For specific implementation details, refer to the individual module documentation and the code examples provided throughout this guide.


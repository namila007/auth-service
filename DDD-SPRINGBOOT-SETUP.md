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
15. [Aggregate Relationship Mapping](#15-aggregate-relationship-mapping)

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

**JPA Entity Naming Convention**:
- **Use `XJpaEntity` suffix**: All JPA entities in the data module must use the `JpaEntity` suffix
- **Examples**: `UserJpaEntity`, `RoleJpaEntity`, `PolicyJpaEntity`, `AuditLogJpaEntity`
- **Purpose**: Clear distinction between domain entities (`XEntity`) and JPA persistence entities (`XJpaEntity`)
- **Benefits**:
  - Prevents naming conflicts with domain entities
  - Makes it clear which layer the entity belongs to
  - Improves code readability and maintainability
  - Aligns with DDD separation of concerns

**Key Components**:
- JPA Entity classes (persistence models) - named as `XJpaEntity`
- Spring Data JPA repositories
- Projection interfaces for optimized queries
- Database configuration classes
- Redis configuration and clients
- PostgreSQL connection settings
- Adapter implementations (for domain ports)
- Entity-to-Domain mappers (MapStruct) - maps `XJpaEntity` ↔ `XAggregate`/`XEntity`

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
            │   ├── entity/          # JPA entities (XJpaEntity)
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

### 9.3 Aggregate Relationship Mapping Pattern

**Pattern Overview**:
When a domain aggregate references another aggregate, the mapping follows a specific pattern to maintain aggregate boundaries while allowing efficient queries. This pattern ensures that domain aggregates only reference other aggregates by ID, not by object references.

**Key Principles**:
1. **JPA Entity**: Has both FK columns AND lazy relationships
   - FK columns (`userId`, `roleId`) are what get persisted to the database
   - `@ManyToOne` relationships marked as `insertable=false, updatable=false` for read convenience only
2. **Domain Aggregate**: References other aggregates by ID only (no object references)
3. **MapStruct Mapping**:
   - **Entity → Domain**: Extract IDs from relationships (`user.userId` → `userId`) OR from FK columns (`userId` → `userId`)
   - **Domain → Entity**: Map IDs to FK columns (`userId.value` → `userId` FK column), ignore relationships
4. **Updates**: Only modify the aggregate root's own fields, never foreign key IDs after creation

**JPA Entity Structure** (Ideal Pattern):
```java
@Entity
@Table(name = "user_role_assignments")
public class UserRoleAssignmentJpaEntity {
    @Id
    @Column(name = "assignment_id")
    private UUID assignmentId;
    
    // FK column - this is what gets persisted
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    // Lazy relationship - for queries only, never updated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserJpaEntity user;
    
    @Column(name = "role_id", nullable = false)
    private UUID roleId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private RoleJpaEntity role;
    
    // Own fields
    private String scope;
    private Instant effectiveFrom;
}
```

**Domain Aggregate Structure**:
```java
public class UserRoleAssignmentAggregate extends BaseAggregate<UserRoleAssignmentId> {
    private UserId userId;        // ID only, not UserAggregate object
    private RoleId roleId;        // ID only, not RoleAggregate object
    private AssignmentScope scope;
    private Instant effectiveFrom;
    
    // Business logic methods that don't modify foreign aggregate IDs
    public void updateScope(AssignmentScope newScope) {
        this.scope = newScope;
    }
}
```

**MapStruct Mapper Pattern**:
```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRoleAssignmentEntityMapper {
    
    // Entity → Domain: Extract IDs from relationships OR FK columns
    @Mapping(target = "id", source = "assignmentId", qualifiedByName = "uuidToUserRoleAssignmentId")
    // Option 1: Extract from relationship (when relationship is loaded)
    @Mapping(target = "userId", source = "user.id", qualifiedByName = "uuidToUserId")
    // Option 2: Extract from FK column directly (preferred - always works)
    // @Mapping(target = "userId", source = "userId", qualifiedByName = "uuidToUserId")
    @Mapping(target = "roleId", source = "role.id", qualifiedByName = "uuidToRoleId")
    // OR: @Mapping(target = "roleId", source = "roleId", qualifiedByName = "uuidToRoleId")
    UserRoleAssignmentAggregate toDomain(UserRoleAssignmentJpaEntity entity);
    
    // Domain → Entity: Map IDs to FK columns, ignore relationships
    @Mapping(target = "assignmentId", source = "id.value")
    @Mapping(target = "userId", source = "userId.value")  // Map to FK column
    @Mapping(target = "roleId", source = "roleId.value")  // Map to FK column
    @Mapping(target = "user", ignore = true)   // Never set relationship
    @Mapping(target = "role", ignore = true)   // Never set relationship
    UserRoleAssignmentJpaEntity toEntity(UserRoleAssignmentAggregate domain);
}
```

**Why This Pattern Works**:
- **Aggregate Boundaries Preserved**: Domain only knows IDs, not full objects
- **No Accidental Cascades**: `insertable=false, updatable=false` prevents JPA from modifying FKs through relationships
- **Query Convenience**: Can use `entity.getUser().getUsername()` in queries when relationships are loaded via JOIN FETCH
- **Clear Intent**: Explicit that relationships are read-only
- **MapStruct Safety**: Compiler-checked mappings, no runtime surprises
- **Round-trip Integrity**: Domain → Entity → Domain preserves all ID references

**Rules to Follow**:
1. **Never call setters on relationship objects** in entity (user, role)
2. **Never modify FK IDs** after entity creation (userId, roleId) - they represent aggregate references
3. **Only update aggregate's own fields** (scope, effectiveFrom, etc.)
4. **Use FK column directly** when creating new entities via mapper
5. **Round-trip mapping preserves IDs**: Domain → Entity → Domain should preserve userId/roleId values

**Common Scenarios**:

**Creating New Assignment**:
```java
// Domain
UserRoleAssignmentAggregate assignment = UserRoleAssignmentAggregate.builder()
    .id(UserRoleAssignmentId.generate())
    .userId(UserId.of("user-123"))  // Just ID
    .roleId(RoleId.of("role-456"))  // Just ID
    .scope(AssignmentScope.GLOBAL)
    .build();

// MapStruct maps IDs to FK columns
UserRoleAssignmentJpaEntity entity = mapper.toEntity(assignment);
// entity.userId = UUID("user-123")  // FK column set
// entity.roleId = UUID("role-456")  // FK column set
// entity.user = null (ignored)
// entity.role = null (ignored)
```

**Updating Existing Assignment**:
```java
// Load from DB
UserRoleAssignmentAggregate assignment = repository.findById(id).orElseThrow();

// Modify only own fields
assignment.updateScope(AssignmentScope.TENANT);

// Save - FK columns unchanged
repository.save(assignment);
// Updates scope only, userId/roleId untouched
```

**Querying with Relationships**:
```java
// JPA Query using lazy relationship
@Query("SELECT ura FROM UserRoleAssignmentJpaEntity ura " +
       "JOIN FETCH ura.user " +
       "WHERE ura.scope = :scope")
List<UserRoleAssignmentJpaEntity> findAssignmentsWithUser(@Param("scope") String scope);

// Map to domain - only IDs extracted
List<UserRoleAssignmentAggregate> assignments = entities.stream()
    .map(mapper::toDomain)
    .collect(Collectors.toList());
```

**Anti-Patterns to Avoid**:
- ❌ Setting relationship objects: `entity.setUser(userEntity)` - violates aggregate boundary
- ❌ Modifying FK after creation: `entity.setUserId("new-user")` - changing aggregate reference
- ❌ Mapping full objects to domain: `new UserRoleAssignment(id, userAggregate, ...)` - domain shouldn't know other aggregates
- ✅ Set FK column on creation only: `entity.setUserId(userId)` via mapper
- ✅ Update only own fields: `entity.setScope(newScope)`
- ✅ Domain only knows IDs: `new UserRoleAssignment(id, UserId.of("user-123"), ...)`

**Implementation Notes**:
- If JPA entity only has `@JoinColumn` relationships without separate FK column fields, the mapper cannot set FK values when mapping domain → entity
- To follow this pattern correctly, JPA entities must have both:
  1. FK column fields (`@Column(name = "user_id") private UUID userId;`)
  2. Lazy relationships (`@ManyToOne @JoinColumn(name = "user_id", insertable=false, updatable=false)`)
- When FK columns exist, MapStruct can map domain IDs directly to FK columns, preserving values in round-trip mapping

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

**Testing Framework**:
- **JUnit 5**: Primary testing framework
- **Mockito**: For mocking dependencies
- **Testcontainers**: For integration tests with real databases
- **Assertions**: Use JUnit Jupiter assertions (`org.junit.jupiter.api.Assertions`)

**Unit Test Structure**:
- Use `@DisplayName` for descriptive test names
- Follow Arrange-Act-Assert (AAA) pattern
- Use `@BeforeEach` for test setup
- Keep tests focused on single behavior

**Assertion Library**:
- **Use JUnit Jupiter Assertions**: `org.junit.jupiter.api.Assertions.*`
- **Do NOT use AssertJ**: Avoid `org.assertj.core.api.Assertions.assertThat()`
- Import: `import static org.junit.jupiter.api.Assertions.*;`

**Common Assertions**:
- Null checks: `assertNotNull()`, `assertNull()`
- Equality checks: `assertEquals(expected, actual)` (note: expected value comes first)
- Boolean checks: `assertTrue()`, `assertFalse()`
- Type checks: `assertInstanceOf(ExpectedClass.class, object)`
- Collection checks: `assertEquals(expectedSize, collection.size())`, `assertTrue(collection.contains(element))`
- Exception checks: `assertThrows(ExpectedException.class, () -> methodThatThrows())`

**Unit Test Strategy by Layer**:

**1. Domain Core Tests**:
- Pure unit tests without Spring framework
- Test domain logic, business rules, and invariants
- Test aggregate and entity creation, validation, and behavior
- Test value object immutability and validation
- Use builder pattern with `@SuperBuilder` for test data setup
- Test exception scenarios and edge cases

**2. Domain Application Tests**:
- Mock dependencies using Mockito (`@Mock`, `@InjectMocks`)
- Use `@ExtendWith(MockitoExtension.class)` for JUnit 5
- Test application service orchestration logic
- Verify interactions with mocked repositories and mappers
- Test use case scenarios and error handling
- Verify DTO mapping and transformation

**3. Mapper Tests**:
- Test MapStruct mapper implementations
- Initialize mapper using `Mappers.getMapper(MapperClass.class)`
- Test bidirectional mapping (entity ↔ domain)
- Test null handling and default values
- Test enum conversions and value object mappings
- Test timestamp conversions (Instant ↔ LocalDateTime)
- Perform round-trip mapping tests to ensure data integrity

**4. Adapter Tests**:
- Mock JPA repositories and mappers
- Test adapter implementations of domain ports
- Verify correct conversion between domain IDs and UUIDs
- Test repository method delegation and error handling
- Verify mapper calls and result transformation

**Integration Tests**:
- Use `@SpringBootTest` for full application context
- Use Testcontainers for PostgreSQL and Redis
- Test full request/response flow through all layers
- Test adapter implementations with real database
- Test transaction boundaries and rollback scenarios
- Test pagination and projection queries

**Mocking Best Practices**:
- Use `@Mock` for dependencies
- Use `@InjectMocks` for class under test
- Use `@ExtendWith(MockitoExtension.class)` for JUnit 5
- Verify interactions with `verify()`
- Use `when().thenReturn()` for stubbing
- Use `any()`, `anyString()`, `anyList()` for flexible matching
- Reset mocks in `@BeforeEach` if needed

**Test Naming Conventions**:
- Test methods: `should[ExpectedBehavior]When[Condition]()`
- Use `@DisplayName` for readable test descriptions
- Group related tests in same test class
- One assertion per test when possible (or related assertions)

### 9.6 Code Organization

**Package Structure**:
- Follow Java package naming conventions
- Group by bounded context (e.g., `identity`, `authorization`)
- Separate ports (input/output) clearly
- Keep mappers close to their usage
- Separate request and response DTOs

**Naming Conventions**:
- **Aggregates**: `UserAggregate`, `RoleAggregate`, `PermissionAggregate` (must use `@SuperBuilder`)
- **Domain Entities**: `ProfileEntity`, `AddressEntity`, `AuditEntity` (must use `@SuperBuilder`)
- **JPA Entities**: `UserJpaEntity`, `RoleJpaEntity`, `PermissionJpaEntity` (use `XJpaEntity` suffix)
- **Value Objects**: `EmailValue`, `PasswordValue`, `UsernameValue` (use regular `@Builder`)
- **ID Classes**: `UserId`, `RoleId`, `PermissionId` (implement `BaseId<UUID>`)
- **Ports**: `UserManagementUseCase`, `UserRepositoryPort`
- **Adapters**: `UserRepositoryAdapter`, `TokenStorageAdapter`
- **Mappers**: `UserDtoMapper`, `UserEntityMapper` (maps between domain and data entities)
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
- All domain entities must extend `BaseEntity<ID>`
- Use naming conventions: `XAggregate`, `XEntity` (domain), `XJpaEntity` (JPA), `XValue`
- Separate request and response DTOs in distinct packages
- Use Spring Data Projections for parent-child relationships
- Support pagination on all list endpoints
- Organize code by bounded context
- Use JUnit Jupiter assertions (`org.junit.jupiter.api.Assertions`) for all tests
- Mock dependencies using Mockito in unit tests

---

## 14. Summary of Key Architectural Decisions

### 14.1 Base Classes
- **BaseId<T>**: Generic identifier interface with UUIDv7 generation
- **BaseEntity<ID>**: Base class for entities with identity and audit fields (uses `@SuperBuilder`)
- **BaseAggregate<ID>**: Base class for aggregates with domain event support (uses `@SuperBuilder`)
- **SuperBuilder Pattern**: All aggregates and entities use `@SuperBuilder` to enable builder pattern for inherited fields (`id`, `createdAt`, `updatedAt`)

### 14.2 Naming Conventions
- Aggregates: `XAggregate` (e.g., `UserAggregate`) - must use `@SuperBuilder`
- Domain Entities: `XEntity` (e.g., `ProfileEntity`) - must use `@SuperBuilder`
- JPA Entities: `XJpaEntity` (e.g., `UserJpaEntity`, `RoleJpaEntity`) - persistence layer entities
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

### 14.7 Aggregate Relationship Mapping
- Domain aggregates reference other aggregates by ID only (not object references)
- JPA entities have both FK columns and lazy relationships
- FK columns are what get persisted (`userId`, `roleId`)
- Lazy relationships are read-only (`insertable=false, updatable=false`)
- MapStruct maps: Entity relationships → Domain IDs, Domain IDs → Entity FK columns
- Round-trip mapping preserves all ID references

---

## 15. Aggregate Relationship Mapping

### 15.1 Pattern Overview

When a domain aggregate needs to reference another aggregate, we use **ID-only references** in the domain layer while maintaining **lazy relationships** in the JPA layer for query convenience. This pattern preserves aggregate boundaries while allowing efficient database queries.

### 15.2 Core Concept

**Domain Layer** (Aggregate Boundaries):
- Aggregates reference other aggregates by **ID only**
- No object references between aggregates
- Maintains clear aggregate boundaries
- Prevents tight coupling

**Data Layer** (Query Convenience):
- JPA entities have **FK columns** (what gets persisted)
- JPA entities have **lazy relationships** (for queries only)
- Relationships marked as `insertable=false, updatable=false`
- Allows efficient JOIN FETCH queries

### 15.3 JPA Entity Structure

**Required Structure**:
```java
@Entity
@Table(name = "user_role_assignments")
public class UserRoleAssignmentJpaEntity {
    @Id
    @Column(name = "assignment_id")
    private UUID assignmentId;
    
    // 1. FK column - this is what gets persisted
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    // 2. Lazy relationship - for queries only, never updated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserJpaEntity user;
    
    @Column(name = "role_id", nullable = false)
    private UUID roleId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private RoleJpaEntity role;
    
    // Own aggregate fields
    private String scope;
    private Instant effectiveFrom;
}
```

**Key Points**:
- **FK columns** (`userId`, `roleId`) are separate fields, not just `@JoinColumn`
- **Relationships** (`user`, `role`) share the same column name but are read-only
- `insertable=false, updatable=false` ensures relationships never modify FK columns
- FK columns are what MapStruct maps to/from

### 15.4 Domain Aggregate Structure

```java
public class UserRoleAssignmentAggregate extends BaseAggregate<UserRoleAssignmentId> {
    private UserId userId;        // ID only, not UserAggregate object
    private RoleId roleId;        // ID only, not RoleAggregate object
    private AssignmentScope scope;
    private Instant effectiveFrom;
    
    // Business logic - never modifies foreign aggregate IDs
    public void updateScope(AssignmentScope newScope) {
        this.scope = newScope;
    }
}
```

**Key Points**:
- Domain only knows **IDs**, not full aggregate objects
- Maintains aggregate boundaries
- No dependencies on other aggregate implementations

### 15.5 MapStruct Mapper Implementation

**Entity → Domain Mapping**:
```java
@Mapping(target = "id", source = "assignmentId", qualifiedByName = "uuidToUserRoleAssignmentId")
// Prefer FK column directly (always works, even if relationship not loaded)
@Mapping(target = "userId", source = "userId", qualifiedByName = "uuidToUserId")
@Mapping(target = "roleId", source = "roleId", qualifiedByName = "uuidToRoleId")
// OR extract from relationship (only works if relationship is loaded)
// @Mapping(target = "userId", source = "user.userId", qualifiedByName = "uuidToUserId")
UserRoleAssignmentAggregate toDomain(UserRoleAssignmentJpaEntity entity);
```

**Domain → Entity Mapping**:
```java
@Mapping(target = "assignmentId", source = "id.value")
@Mapping(target = "userId", source = "userId.value")  // Map to FK column
@Mapping(target = "roleId", source = "roleId.value")  // Map to FK column
@Mapping(target = "user", ignore = true)   // Never set relationship
@Mapping(target = "role", ignore = true)   // Never set relationship
UserRoleAssignmentJpaEntity toEntity(UserRoleAssignmentAggregate domain);
```

**Why This Works**:
- **FK columns are set directly** from domain IDs
- **Relationships are ignored** - they're read-only anyway
- **Round-trip preserves IDs**: Domain → Entity → Domain maintains all ID references
- **No dependency on relationship loading** - FK columns always exist

### 15.6 Repository Implementation

```java
@Repository
public class UserRoleAssignmentRepositoryAdapter implements UserRoleAssignmentRepositoryPort {
    
    private final UserRoleAssignmentJpaRepository jpaRepository;
    private final UserRoleAssignmentEntityMapper mapper;
    
    @Override
    public UserRoleAssignmentAggregate save(UserRoleAssignmentAggregate assignment) {
        // Map domain to entity - FK columns set, relationships ignored
        UserRoleAssignmentJpaEntity entity = mapper.toEntity(assignment);
        UserRoleAssignmentJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<UserRoleAssignmentAggregate> findById(UserRoleAssignmentId id) {
        return jpaRepository.findById(id.getValue())
            .map(mapper::toDomain);  // Maps FK columns to domain IDs
    }
    
    @Override
    public List<UserRoleAssignmentAggregate> findByUserId(UserId userId) {
        // Query uses FK column directly
        return jpaRepository.findByUser_UserId(userId.getValue()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
}
```

### 15.7 Benefits

**✅ Aggregate Boundaries**:
- Domain aggregates don't know about other aggregate implementations
- Clear separation of concerns
- Easy to test domain logic in isolation

**✅ Query Efficiency**:
- Can use JOIN FETCH for efficient queries
- Relationships available when needed
- No N+1 problems when properly used

**✅ Safety**:
- `insertable=false, updatable=false` prevents accidental FK modifications
- MapStruct compiler checks ensure correct mappings
- FK columns explicitly managed

**✅ Round-trip Integrity**:
- Domain → Entity → Domain preserves all ID references
- No data loss in mapping cycles
- Predictable behavior

### 15.8 Common Patterns

**Pattern 1: FK Column + Relationship (Recommended)**:
```java
// JPA Entity
@Column(name = "user_id")
private UUID userId;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", insertable = false, updatable = false)
private UserJpaEntity user;

// Mapper
@Mapping(target = "userId", source = "userId", qualifiedByName = "uuidToUserId")  // From FK
@Mapping(target = "userId", source = "userId.value")  // To FK
```

**Pattern 2: Relationship Only (Current Limitation)**:
```java
// JPA Entity - only relationship, no FK column field
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private UserJpaEntity user;

// Mapper - can extract from relationship, but cannot set FK when mapping domain → entity
@Mapping(target = "userId", source = "user.userId", qualifiedByName = "uuidToUserId")  // From relationship
// Cannot map domain → entity FK (no FK column field exists)
```

**Recommendation**: Always use Pattern 1 (FK column + relationship) for proper round-trip mapping support.

### 15.9 Testing Considerations

**Round-trip Mapping Tests**:
- When FK columns exist and are mapped correctly, round-trip tests should verify ID preservation
- When only relationships exist, round-trip tests may show null IDs (expected limitation)
- Tests should document which pattern is being used

**Example Test**:
```java
@Test
@DisplayName("Should perform round-trip mapping correctly")
void shouldPerformRoundTripMapping() {
    UserRoleAssignmentAggregate originalDomain = UserRoleAssignmentAggregate.builder()
        .id(UserRoleAssignmentId.generate())
        .userId(UserId.generate())
        .roleId(RoleId.generate())
        .scope(AssignmentScope.GLOBAL)
        .build();
    
    UserRoleAssignmentJpaEntity entity = mapper.toEntity(originalDomain);
    UserRoleAssignmentAggregate mappedDomain = mapper.toDomain(entity);
    
    // If FK columns exist and are mapped: IDs should be preserved
    assertEquals(originalDomain.getUserId().getValue(), mappedDomain.getUserId().getValue());
    assertEquals(originalDomain.getRoleId().getValue(), mappedDomain.getRoleId().getValue());
    
    // If only relationships exist: IDs may be null (documented limitation)
}
```

### 15.10 Real-World Implementation Examples

This section provides concrete examples from the Auth Service implementation showing Pattern 1 in action.

#### 15.10.1 UserRoleAssignmentAggregate Implementation

**Domain Aggregate** (ID-only references):
```java
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleAssignmentAggregate extends BaseAggregate<UserRoleAssignmentId> {
    private UserRoleAssignmentId id;
    private UserId userId;        // ID only, not UserAggregate object
    private RoleId roleId;        // ID only, not RoleAggregate object
    private AssignmentScope scope;
    private String scopeContext;
    private Instant effectiveFrom;
    private Instant effectiveUntil;
    private UserId assignedBy;    // ID only
    private Instant assignedAt;
    private AssignmentStatus status;
    private Long version;
    
    // Business logic methods - never modify foreign aggregate IDs
    public void updateScope(AssignmentScope newScope) {
        this.scope = newScope;
        markAsUpdated();
    }
}
```

**JPA Entity** (FK columns + relationships):
```java
@Entity
@Table(name = "user_role_assignments", schema = "authorization")
public class UserRoleAssignmentJpaEntity {
    @Id
    @Column(name = "assignment_id")
    private UUID assignmentId;
    
    // FK column - this is what gets persisted
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    // Lazy relationship - for queries only, never updated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserJpaEntity user;
    
    // FK column - this is what gets persisted
    @Column(name = "role_id", nullable = false)
    private UUID roleId;
    
    // Lazy relationship - for queries only, never updated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private RoleJpaEntity role;
    
    @Column(name = "scope", nullable = false)
    private String scope;
    
    @Column(name = "assigned_by", nullable = false)
    private UUID assignedBy;
    
    // Other fields...
}
```

**MapStruct Mapper**:
```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRoleAssignmentEntityMapper {
    
    // Entity → Domain: Extract IDs from FK columns (preferred)
    @Mapping(target = "id", source = "assignmentId", qualifiedByName = "uuidToUserRoleAssignmentId")
    @Mapping(target = "userId", source = "userId", qualifiedByName = "uuidToUserId")
    @Mapping(target = "roleId", source = "roleId", qualifiedByName = "uuidToRoleId")
    @Mapping(target = "assignedBy", source = "assignedBy", qualifiedByName = "uuidToUserId")
    @Mapping(target = "scope", source = "scope", qualifiedByName = "stringToAssignmentScope")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToAssignmentStatus")
    UserRoleAssignmentAggregate toDomain(UserRoleAssignmentJpaEntity entity);
    
    // Domain → Entity: Map IDs to FK columns, ignore relationships
    @Mapping(target = "assignmentId", source = "id.value")
    @Mapping(target = "userId", source = "userId.value")
    @Mapping(target = "roleId", source = "roleId.value")
    @Mapping(target = "assignedBy", source = "assignedBy.value")
    @Mapping(target = "user", ignore = true)   // Never set relationship
    @Mapping(target = "role", ignore = true)   // Never set relationship
    @Mapping(target = "scope", source = "scope", qualifiedByName = "assignmentScopeToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "assignmentStatusToString")
    UserRoleAssignmentJpaEntity toEntity(UserRoleAssignmentAggregate domain);
    
    // Helper methods for ID conversions
    @Named("uuidToUserId")
    default UserId uuidToUserId(UUID uuid) {
        return uuid != null ? UserId.of(uuid) : null;
    }
    
    @Named("uuidToRoleId")
    default RoleId uuidToRoleId(UUID uuid) {
        return uuid != null ? RoleId.of(uuid) : null;
    }
}
```

**Round-trip Test**:
```java
@Test
@DisplayName("Should perform round-trip mapping correctly")
void shouldPerformRoundTripMapping() {
    // Given
    UserRoleAssignmentId assignmentId = UserRoleAssignmentId.generate();
    UserId userId = UserId.generate();
    RoleId roleId = RoleId.generate();
    UserId assignedBy = UserId.generate();
    
    UserRoleAssignmentAggregate originalDomain = UserRoleAssignmentAggregate.builder()
        .id(assignmentId)
        .userId(userId)
        .roleId(roleId)
        .scope(AssignmentScope.GLOBAL)
        .assignedBy(assignedBy)
        .build();
    
    // When
    UserRoleAssignmentJpaEntity entity = mapper.toEntity(originalDomain);
    UserRoleAssignmentAggregate mappedDomain = mapper.toDomain(entity);
    
    // Then - Round-trip should preserve all ID references
    assertEquals(originalDomain.getId().getValue(), mappedDomain.getId().getValue());
    assertEquals(originalDomain.getUserId().getValue(), mappedDomain.getUserId().getValue());
    assertEquals(originalDomain.getRoleId().getValue(), mappedDomain.getRoleId().getValue());
    assertEquals(originalDomain.getAssignedBy().getValue(), mappedDomain.getAssignedBy().getValue());
}
```

#### 15.10.2 FederatedIdentityEntity Implementation

**Domain Entity** (ID-only reference):
```java
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FederatedIdentityEntity extends BaseEntity<FederatedIdentityId> {
    private FederatedIdentityId id;
    private UserId userId;           // ID only, not UserAggregate object
    private UUID providerId;         // References OIDCProviderConfigAggregate
    private String subjectId;
    private String issuer;
    private Instant linkedAt;
    private Instant lastSyncedAt;
    private Map<String, Object> metadata;
    
    public void linkToUser(UserId userId) {
        this.userId = userId;
        markAsUpdated();
    }
}
```

**JPA Entity** (FK column + relationship):
```java
@Entity
@Table(name = "federated_identities", schema = "identity")
public class FederatedIdentityJpaEntity {
    @Id
    @Column(name = "federated_identity_id")
    private UUID federatedIdentityId;
    
    // FK column - this is what gets persisted
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    // Lazy relationship - for queries only, never updated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserJpaEntity user;
    
    @Column(name = "provider_id", nullable = false)
    private UUID providerId;
    
    // Other fields...
}
```

**MapStruct Mapper**:
```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FederatedIdentityEntityMapper {
    
    // Entity → Domain: Extract userId from FK column
    @Mapping(target = "id", source = "federatedIdentityId", qualifiedByName = "uuidToFederatedIdentityId")
    @Mapping(target = "userId", source = "userId", qualifiedByName = "uuidToUserId")
    FederatedIdentityEntity toDomain(FederatedIdentityJpaEntity entity);
    
    // Domain → Entity: Map userId to FK column, ignore relationship
    @Mapping(target = "federatedIdentityId", source = "id.value")
    @Mapping(target = "userId", source = "userId.value")
    @Mapping(target = "user", ignore = true)
    FederatedIdentityJpaEntity toEntity(FederatedIdentityEntity domain);
}
```

#### 15.10.3 Key Implementation Guidelines

**When Creating New Aggregates That Reference Other Aggregates**:

1. **Domain Layer**: Use ID-only references
   ```java
   private UserId userId;  // ✅ Correct
   private UserAggregate user;  // ❌ Wrong - violates aggregate boundaries
   ```

2. **JPA Entity Layer**: Add both FK column AND relationship
   ```java
   @Column(name = "user_id")
   private UUID userId;  // ✅ FK column for persistence
   
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_id", insertable = false, updatable = false)
   private UserJpaEntity user;  // ✅ Relationship for queries
   ```

3. **Mapper Layer**: Map FK columns, not relationships
   ```java
   // ✅ Correct - uses FK column
   @Mapping(target = "userId", source = "userId", qualifiedByName = "uuidToUserId")
   
   // ❌ Wrong - uses relationship (fails if not loaded)
   @Mapping(target = "userId", source = "user.userId", qualifiedByName = "uuidToUserId")
   ```

4. **Test Layer**: Verify round-trip mapping preserves IDs
   ```java
   @Test
   void shouldPerformRoundTripMapping() {
       // Create domain with IDs
       // Map to entity
       // Map back to domain
       // Assert IDs are preserved
   }
   ```

**Benefits of This Approach**:
- ✅ Aggregate boundaries preserved in domain layer
- ✅ Round-trip mapping works correctly
- ✅ Can use JOIN FETCH for efficient queries when needed
- ✅ No accidental FK modifications (insertable=false, updatable=false)
- ✅ Clear separation: FK columns for persistence, relationships for queries

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


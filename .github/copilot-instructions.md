# Auth Service - AI Coding Agent Instructions

## Architecture Overview

This is a **Domain-Driven Design (DDD) multi-module Spring Boot 3.x** authentication/authorization service. The codebase strictly separates domain logic from infrastructure, using **bounded contexts** (Identity, Authorization, Configuration, Governance) to organize business capabilities.

**Critical**: Domain models (`*Aggregate`, `*Entity`) are completely separate from JPA entities (`*JpaEntity`). Never mix them.

## Module Structure & Dependencies

```
auth-service/
├── auth-service-common/         # Common/Shared Module
│   └── domain/                  # BaseId, BaseEntity, BaseAggregate
├── authservice-application/     # Application/API Layer
│   ├── controller/              # REST controllers
│   ├── dto/
│   │   ├── request/            # API request DTOs
│   │   └── response/           # API response DTOs
│   ├── config/                 # Spring configuration
│   ├── security/               # Security config
│   └── exception/              # Global exception handlers
├── authservice-domain/          # Domain Layer (Parent Module)
│   ├── domain-core/            # Core Domain Logic
│   │   └── {context}/          # e.g., identity, authorization
│   │       ├── model/
│   │       │   ├── aggregate/  # XAggregate classes
│   │       │   ├── entity/     # XEntity classes
│   │       │   ├── value/      # XValue classes
│   │       │   └── id/         # XId classes
│   │       ├── service/        # Domain services
│   │       ├── event/          # Domain events
│   │       └── exception/      # Domain exceptions
│   └── domain-application/     # Ports & Adapters
│       └── {context}/
│           ├── port/
│           │   ├── input/      # Use case interfaces
│           │   └── output/     # Repository ports
│           ├── service/        # Application services
│           ├── dto/
│           │   ├── request/    # Request DTOs
│           │   └── response/   # Response DTOs
│           └── mapper/         # Domain-DTO mappers
└── authservice-data/           # Data Layer (Single Module)
    └── data/{context}/         # Context-based packages
        ├── entity/             # XJpaEntity classes
        ├── repository/         # Spring Data repositories
        ├── projection/         # Spring Data projections
        ├── adapter/            # Repository port implementations
        ├── mapper/             # JPA-Domain mappers
        └── config/             # Context-specific config
```

**Project Coordinates**:
- **Group**: `me.namila.service.auth`
- **Version**: `0.0.1`
- **Java**: 25 (required) with `sourceCompatibility = JavaVersion.VERSION_25`

**Build & Test**:
- **Build**: `./gradlew build` (Windows: `./gradlew.bat build`)
- **Test**: `./gradlew test` (finalizes with `jacocoTestReport`)
- **Aggregate Coverage**: `./gradlew jacocoAggregatedReport`
- **Coverage Reports**: `build/reports/jacoco/`

**Module Registration** (`settings.gradle`):
```gradle
include("auth-service-common")
include("authservice-application")
include("authservice-domain:domain-application")
include("authservice-domain:domain-core")
include("authservice-data")
```

## Naming Conventions (MANDATORY)

| Type | Pattern | Example | Location | Base Class |
|------|---------|---------|----------|------------|
| **Aggregate Root** | `XAggregate` | `UserAggregate` | `domain-core/{context}/model/aggregate/` | `BaseAggregate<XId>` |
| **Entity** | `XEntity` | `ProfileEntity` | `domain-core/{context}/model/entity/` | `BaseEntity<XId>` |
| **Value Object** | `XValue` | `EmailValue` | `domain-core/{context}/model/value/` | Immutable class |
| **ID Class** | `XId` | `UserId` | `domain-core/{context}/model/id/` | `BaseId<UUID>` |
| **JPA Entity** | `XJpaEntity` | `UserJpaEntity` | `authservice-data/data/{context}/entity/` | JPA `@Entity` |
| **Projection** | `XSummaryProjection` | `UserSummaryProjection` | `authservice-data/data/{context}/projection/` | Interface |
| **Request DTO** | `CreateXRequest`, `UpdateXRequest` | `CreateUserRequest` | `domain-application/{context}/dto/request/` | Record/POJO |
| **Response DTO** | `XResponse`, `XDetailResponse` | `UserResponse` | `domain-application/{context}/dto/response/` | Record/POJO |

**Example ID Implementation** (see `UserId.java`):
```java
public class UserId implements BaseId<UUID> {
    private UUID value;
    private UserId(UUID value) { this.value = value; }
    public static UserId of(String id) { return new UserId(UUID.fromString(id)); }
    public static UserId of(UUID id) { return new UserId(id); }
    public static UserId generate() { return new UserId(UuidCreator.getTimeOrderedEpoch()); } // UUIDv7
}
```

## Bounded Contexts & Package Structure

### 1. Identity Context (`*.identity`)
**Purpose**: User management, credentials, external identity federation

**Domain Models**:
- **Aggregates**: `UserAggregate` (root)
- **Entities**: `ProfileEntity`, `FederatedIdentityEntity`, `LocalCredentialEntity`
- **Value Objects**: `EmailValue`, `UsernameValue`, `PasswordValue`, `UserStatus`
- **IDs**: `UserId`, `ProfileId`, `FederatedIdentityId`

**JPA Entities**: `UserJpaEntity`, `UserProfileJpaEntity`, `FederatedIdentityJpaEntity`

**Key Responsibilities**:
- User lifecycle management (create, update, activate, suspend)
- Profile management (personal information)
- Federated identity linking (OIDC/SAML providers)
- Local credential management (optional for local auth)

### 2. Authorization Context (`*.authorization`)
**Purpose**: Roles, permissions, policies, policy evaluation

**Domain Models**:
- **Aggregates**: `RoleAggregate` (root), `PolicyAggregate` (root), `UserRoleAssignmentAggregate` (root)
- **Entities**: `PermissionEntity`
- **Value Objects**: `RoleType`, `AssignmentScope`, `AssignmentStatus`, `PolicyType`, `Effect`
- **IDs**: `RoleId`, `PermissionId`, `PolicyId`, `UserRoleAssignmentId`

**JPA Entities**: `RoleJpaEntity`, `PermissionJpaEntity`, `PolicyJpaEntity`, `UserRoleAssignmentJpaEntity`

**Key Responsibilities**:
- Role-based access control (RBAC)
- Attribute-based access control (ABAC)
- Policy evaluation (PDP - Policy Decision Point)
- Permission aggregation and inheritance
- Scoped role assignments (GLOBAL, TENANT, RESOURCE)

### 3. Configuration Context (`*.configuration`)
**Purpose**: OIDC/SAML provider configuration, attribute mapping, system settings

**Domain Models**:
- **Aggregates**: `OIDCProviderConfigAggregate` (root)
- **Entities**: `AttributeMappingEntity`, `RoleMappingEntity`, `OIDCConfigurationEntity`
- **Value Objects**: `ProviderType`, `MappingStrategy`, `JITProvisioningConfig`
- **IDs**: `OIDCProviderConfigId`

**JPA Entities**: `OIDCProviderConfigJpaEntity`

**Key Responsibilities**:
- OIDC/SAML provider registration and configuration
- Attribute mapping (external claims → internal attributes)
- Role mapping (external groups → internal roles)
- JIT (Just-In-Time) provisioning configuration
- Provider enable/disable management

### 4. Governance Context (`*.governance`)
**Purpose**: Audit logging, access certification, compliance reporting

**Domain Models**:
- **Aggregates**: `AuditLogAggregate` (immutable event), `AccessCertificationAggregate`
- **Value Objects**: `AuditEventType`, `ActorType`, `Decision`
- **IDs**: `AuditLogId`, `AccessCertificationId`

**JPA Entities**: `AuditLogJpaEntity`, `AccessCertificationJpaEntity`

**Key Responsibilities**:
- Comprehensive audit trail (all authentication/authorization events)
- PDP/PEP correlation tracking
- Access certification workflows
- Compliance reporting (SOX, GDPR, HIPAA)

## Layer-Specific Rules

### domain-core (Pure Domain)
- **NO Spring** annotations (`@Service`, `@Component`)
- **NO JPA** annotations (`@Entity`, `@Table`)
- **NO infrastructure** dependencies
- Only business logic, domain services, and domain events
- All classes extend/implement base classes from `auth-service-common`

### domain-application (Use Cases)
- Port interfaces (`input/`, `output/`) define boundaries
- Application services implement use cases
- DTOs for all external communication
- MapStruct mappers for Domain ↔ DTO conversion
- `@Mapper(componentModel = "spring")` for dependency injection

### authservice-data (Infrastructure)
- **Context-based Java packages** (not separate Gradle modules): `data.identity`, `data.authorization`, etc.
- JPA entities MUST use `XJpaEntity` suffix
- MapStruct mappers for `XJpaEntity` ↔ `XAggregate`/`XEntity` (see `UserEntityMapper.java`)
- Spring Data Projections for parent-child relationships (minimal child data)
- Implement repository ports from `domain-application`
- Example mapper: `@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)`

### authservice-application (API)
- REST controllers delegate to application services
- Request/Response DTOs separate from domain DTOs
- ALL list endpoints support pagination (`PagedResponse<T>`)
- Global exception handling in `GlobalExceptionHandler`
- Component scanning: `me.namila.service.auth.application`, `me.namila.service.auth.domain.application`, `me.namila.service.auth.data`

## API Standards

**Base Path**: `/api/v1`

**Standard Endpoints** (all contexts follow this pattern):
```
/api/v1/{resource}              # List (paginated)
/api/v1/{resource}              # Create (POST)
/api/v1/{resource}/{id}         # Get by ID
/api/v1/{resource}/{id}         # Partial update (PATCH)
/api/v1/{resource}/{id}         # Full update (PUT)
/api/v1/{resource}/{id}         # Delete
```

**Key Endpoint Groups**:
- `/api/v1/auth/oidc/*` - OIDC authentication flow
- `/api/v1/users` - User management
- `/api/v1/roles` - Role management
- `/api/v1/permissions` - Permission management
- `/api/v1/policies` - Policy management
- `/api/v1/oidc-providers` - OIDC provider configuration
- `/api/v1/assignments` - Role assignments
- `/api/v1/access-control/check` - Authorization checks (PDP)
- `/api/v1/audit/logs` - Audit queries

**Pagination**: All list endpoints return `PagedResponse<T>`
- **Query Params**: `?page=0&size=20&sort=field,asc`
- **Response Fields**: `content`, `page`, `size`, `totalElements`, `totalPages`, `hasNext`, `hasPrevious`

**Response Formats**:

**Success Response**:
```json
{
  "status": "success",
  "data": { /* actual response data */ },
  "metadata": {
    "timestamp": "2025-01-15T10:30:00Z",
    "version": "v1"
  }
}
```

**Error Response**:
```json
{
  "status": "error",
  "error": {
    "code": "AUTH_001",
    "message": "Authentication failed",
    "details": "Invalid credentials provided",
    "field": "password",
    "timestamp": "2025-01-15T10:30:00Z",
    "path": "/api/v1/auth/login",
    "correlationId": "abc-123-def"
  }
}
```

**Pagination Response**:
```json
{
  "content": [ /* array of items */ ],
  "pageable": {
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

## Data Access Patterns

### 1. Spring Data Projections Strategy
**Purpose**: Optimize parent-child relationships and minimize data transfer

**Pattern**:
```java
// Parent response includes child projections (minimal data)
public interface UserSummaryProjection {
    UUID getId();
    String getUsername();
    String getEmail();
    LocalDateTime getCreatedAt();  // Note: Projections use LocalDateTime
    LocalDateTime getUpdatedAt();
}

public interface RoleSummaryProjection {
    UUID getId();
    String getRoleName();
    String getDisplayName();
    String getRoleType();
    LocalDateTime getCreatedAt();
    // No permissions or other heavy data
}

// For aggregates referencing other aggregates
public interface UserRoleAssignmentSummaryProjection {
    UUID getId();
    UUID getUserId();       // FK reference
    UUID getRoleId();       // FK reference
    String getRoleName();   // Joined from role table for convenience
    String getScope();
    String getScopeContext();
    Instant getEffectiveFrom();  // Note: Business timestamps use Instant
    Instant getEffectiveUntil();
    String getStatus();
}

// Full role details available via: GET /api/roles/{id}
```

**Benefits**:
- Parent entities include child projections (minimal data)
- Child details fetched separately when needed
- Avoids N+1 query problems
- Supports pagination for both parent and child collections
- Projections use `LocalDateTime` for audit timestamps, `Instant` for business timestamps

### 2. Three-Layer MapStruct Mapping

**Layer 1 - JPA ↔ Domain** (`authservice-data/data/{context}/mapper/`):

**Key Pattern**: When mapping aggregate references between JPA entities and domain aggregates:
- **Command Side (Write)**: JPA entities store ONLY foreign key UUID columns for referenced aggregates
- **Query Side (Read)**: JPA entities MAY have `@ManyToOne` relationships marked as non-insertable/non-updatable for read-only access
- **Mappers**: Always extract IDs from FK columns, NEVER from relationship objects

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRoleAssignmentEntityMapper {
    
    // JPA → Domain: Extract IDs from FK columns (preferred - always works)
    @Mapping(target = "id", source = "assignmentId", qualifiedByName = "uuidToAssignmentId")
    @Mapping(target = "userId", source = "userId", qualifiedByName = "uuidToUserId")
    @Mapping(target = "roleId", source = "roleId", qualifiedByName = "uuidToRoleId")
    @Mapping(target = "assignedBy", source = "assignedBy", qualifiedByName = "uuidToUserId")
    @Mapping(target = "scope", source = "scope", qualifiedByName = "stringToScope")
    @Mapping(target = "createdAt", ignore = true)  // BaseEntity handles this
    @Mapping(target = "updatedAt", ignore = true)  // BaseEntity handles this
    UserRoleAssignmentAggregate toDomain(UserRoleAssignmentJpaEntity entity);
    
    // Domain → JPA: Map IDs to FK columns, IGNORE relationship objects
    @Mapping(target = "assignmentId", source = "id.value")
    @Mapping(target = "userId", source = "userId.value")
    @Mapping(target = "roleId", source = "roleId.value")
    @Mapping(target = "assignedBy", source = "assignedBy.value")
    @Mapping(target = "scope", source = "scope", qualifiedByName = "scopeToString")
    @Mapping(target = "user", ignore = true)   // NEVER set - read-only relationship
    @Mapping(target = "role", ignore = true)   // NEVER set - read-only relationship
    UserRoleAssignmentJpaEntity toEntity(UserRoleAssignmentAggregate domain);
    
    @Named("uuidToAssignmentId")
    default UserRoleAssignmentId uuidToAssignmentId(UUID uuid) {
        return uuid != null ? UserRoleAssignmentId.of(uuid) : null;
    }
    
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

**Layer 2 - Domain ↔ DTO** (`domain-application/{context}/mapper/`):
```java
@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    // Domain → DTO: Aggregates use Instant for business timestamps
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "lastModifiedAt", source = "updatedAt")
    UserResponse toResponse(UserAggregate aggregate);
    
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "lastModifiedAt", source = "updatedAt")
    UserDetailResponse toDetailResponse(UserAggregate aggregate);
    
    // DTO → Domain
    UserAggregate fromRequest(CreateUserRequest request);
}
```

**Timestamp Handling**:
- **Domain Aggregates/Entities**: Use `LocalDateTime` (from `BaseEntity`) for audit timestamps (`createdAt`, `updatedAt`)
- **JPA Entities**: Use `Instant` for all timestamps (business + audit) to ensure UTC storage
- **DTOs (Request/Response)**: Use `Instant` for all timestamps (API returns UTC)
- **Projections**: Use `LocalDateTime` for audit fields, `Instant` for business timestamps

**Mapping Convention**:
```java
// Domain BaseEntity fields
private LocalDateTime createdAt;
private LocalDateTime updatedAt;

// JPA Entity fields (database columns)
@Column(name = "created_at", nullable = false)
private Instant createdAt;

@Column(name = "last_modified_at", nullable = false)
private Instant lastModifiedAt;

// DTO fields (API)
private Instant createdAt;
private Instant lastModifiedAt;
```

**Layer 3 - Controller** (never expose JPA entities):
```java
@RestController
public class UserController {
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID userId) {
        UserAggregate aggregate = userService.findById(userId);
        return ResponseEntity.ok(userDtoMapper.toResponse(aggregate));
    }
}
```

### 3. UUIDv7 ID Generation
**Implementation** (all ID classes follow this pattern):
```java
import com.github.f4b6a3.uuid.UuidCreator;

public class UserId implements BaseId<UUID> {
    private UUID value;
    
    public static UserId generate() {
        return new UserId(UuidCreator.getTimeOrderedEpoch());  // UUIDv7
    }
}
```

**Benefits**:
- Time-ordered for better database indexing
- Sortable by creation time
- Compatible with UUID columns
- Better performance than UUID.randomUUID() (UUIDv4)

**Dependency**: `com.github.f4b6a3:uuid-creator:5.2.0`

### 4. Aggregate Reference Patterns

**How Aggregates Reference Other Aggregates**:

**Domain Model** (stores ID references only):
```java
public class UserRoleAssignmentAggregate extends BaseAggregate<UserRoleAssignmentId> {
    private UserRoleAssignmentId id;
    private UserId userId;        // Reference to User aggregate by ID
    private RoleId roleId;        // Reference to Role aggregate by ID
    private UserId assignedBy;    // Reference to another User by ID
    
    private AssignmentScope scope;
    private Instant effectiveFrom;
    private Instant effectiveUntil;
    private AssignmentStatus status;
    
    // Business logic operates on IDs, not full aggregates
    public void revoke(UserId revokedBy) {
        this.status = AssignmentStatus.REVOKED;
        this.assignedBy = revokedBy;  // Store ID only
        markAsUpdated();
    }
}
```

**JPA Entity** (command side + optional query side):
```java
@Entity
@Table(name = "user_role_assignments", schema = "authorizations")
public class UserRoleAssignmentJpaEntity {
    
    @Id
    @Column(name = "assignment_id")
    private UUID assignmentId;
    
    // COMMAND SIDE: Foreign key columns for write operations
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "role_id", nullable = false)
    private UUID roleId;
    
    @Column(name = "assigned_by", nullable = false)
    private UUID assignedBy;
    
    // QUERY SIDE: Read-only relationships for fetching related data
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", 
                insertable = false, 
                updatable = false,
                foreignKey = @ForeignKey(name = "FK_user_role_assignment_user"))
    private UserJpaEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", 
                insertable = false, 
                updatable = false,
                foreignKey = @ForeignKey(name = "FK_user_role_assignment_role"))
    private RoleJpaEntity role;
    
    // Business timestamps
    @Column(name = "effective_from", nullable = false)
    private Instant effectiveFrom;
    
    @Column(name = "effective_until")
    private Instant effectiveUntil;
    
    // Status
    @Column(name = "status", nullable = false, length = 50)
    private String status;
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
```

**Key Principles**:
1. **Command Operations**: Use FK columns (`userId`, `roleId`) for INSERT/UPDATE
2. **Query Operations**: Use `@ManyToOne` relationships (marked `insertable=false, updatable=false`) for reading related data
3. **Mapper Pattern**: Always map from FK columns to domain IDs, IGNORE relationship objects in toEntity()
4. **Lazy Loading**: Always use `FetchType.LAZY` to avoid N+1 queries
5. **Projection Usage**: Use projections to fetch minimal related data (e.g., role name) without loading full aggregates

**When to Use Relationships vs Projections**:
- **Relationships**: When you need to navigate from assignment to full user/role details in complex queries
- **Projections**: When you need minimal info (ID, name) for list views - preferred for performance
- **Never**: Don't use relationships for write operations - always use FK columns

## Local Development Infrastructure

**Start all services**: `docker compose -f infra/docker-compose.yaml up -d`

| Service | Port | Purpose | Access |
|---------|------|---------|--------|
| PostgreSQL | 5432 | Main DB (authdb + keycloakdb) | `postgres:password@localhost:5432` |
| Redis | 6379 | Cache/sessions | `localhost:6379` |
| Keycloak | 8080 | OIDC provider | `http://localhost:8080` (admin/admin) |
| pgAdmin | 5050 | DB UI | `http://localhost:5050` |
| Redis Commander | 8081 | Redis UI | `http://localhost:8081` |

**DB Init**: `infra/init-scripts/01-init-databases.sql` creates schemas  
**Keycloak Realm**: `infra/keycloak/realms/auth-service-realm.json` pre-configured realm  
**Persistent Data**: `infra/data/postgres/pgdata`, `infra/data/redis/`, etc.

## Testing Strategy

- **Unit Tests**: Domain logic, mappers, services
- **Integration Tests**: Repository tests with real DB (consider Testcontainers)
- **Coverage**: JaCoCo reports in `build/reports/jacoco/`
- **Run**: `./gradlew test` (finalizes with `jacocoTestReport`)
- **Aggregate Report**: `./gradlew jacocoAggregatedReport` (multi-module coverage)

## Technology Stack & Dependencies

### Core Framework
- **Spring Boot**: 3.5.7
- **Java**: 25
- **Build Tool**: Gradle 8.x+ (Wrapper included)
- **Dependency Management**: Spring Boot BOM + Version Catalog

### Domain & Mapping
- **MapStruct**: Latest (for DTO/Entity/Domain mapping)
- **Lombok**: Latest (boilerplate reduction)
- **Jakarta Validation**: For validation rules
- **UUID Creator**: `com.github.f4b6a3:uuid-creator:5.2.0` (UUIDv7 generation)

### Data Layer
- **Spring Data JPA**: 3.x (database access)
- **PostgreSQL**: Primary database
- **Redis**: Caching and session storage
- **HikariCP**: Connection pooling (default in Spring Boot)
- **Flyway/Liquibase**: Database migrations

### Security
- **Spring Security**: 6.x (authentication and authorization)
- **Spring Security OAuth2**: OIDC integration
- **JWT**: Token handling (RS256 asymmetric signing)

### API & Documentation
- **Spring Web**: REST API
- **OpenAPI 3.0 / SpringDoc**: API documentation
- **Jackson**: JSON serialization

### Testing
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework
- **Testcontainers**: Integration testing with real databases
- **Spring Boot Test**: Integration test support
- **JaCoCo**: Code coverage (toolVersion = "0.8.13")

### Code Quality
- **Checkstyle**: Code style checking (optional)
- **SpotBugs**: Static analysis (optional)
- **JaCoCo**: Code coverage reports

## Common Patterns from Codebase

### Constructor Injection (No `@Autowired`)
```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserApplicationService userService;
    private final UserDtoMapper userDtoMapper;
    
    // Constructor injection - Spring auto-wires
    public UserController(UserApplicationService userService, 
                         UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
    }
}
```

### Value Objects (Immutable)
```java
public class EmailValue {
    private final String value;
    
    private EmailValue(String value) {
        if (value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.value = value;
    }
    
    public static EmailValue of(String email) {
        return new EmailValue(email);
    }
    
    public String getValue() {
        return value;
    }
    
    // No setter - immutable
}
```

### Aggregate Root with Business Logic
```java
public class UserAggregate extends BaseAggregate<UserId> {
    private UserId id;
    private UsernameValue username;
    private EmailValue email;
    private UserStatus status;
    
    // Business logic methods (NOT in entities or services)
    public void activate() {
        if (this.status == UserStatus.SUSPENDED) {
            throw new UserSuspendedException("Cannot activate suspended user");
        }
        this.status = UserStatus.ACTIVE;
        // Emit domain event if needed
    }
    
    public void suspend(String reason) {
        this.status = UserStatus.SUSPENDED;
        // Emit UserSuspendedEvent
    }
}
```

### Application Service (Use Case Implementation)
```java
@Service
@Transactional  // Class-level: default read-write transactions
public class UserApplicationService {
    private final UserRepositoryPort userRepository;
    private final UserDtoMapper mapper;
    
    public UserApplicationService(UserRepositoryPort userRepository,
                                 UserDtoMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }
    
    // Write operation (uses class-level @Transactional)
    @Transactional  // Explicit for clarity (optional, already covered by class-level)
    public UserResponse createUser(CreateUserRequest request) {
        // 1. Map DTO to domain
        UserAggregate user = mapper.fromRequest(request);
        
        // 2. Execute business logic
        user.setId(UserId.generate());
        
        // 3. Persist via port
        UserAggregate saved = userRepository.save(user);
        
        // 4. Map domain to response DTO
        return mapper.toResponse(saved);
    }
    
    // Read-only operation (optimizes transaction)
    @Transactional(readOnly = true)
    public UserDetailResponse getUserById(UUID userId) {
        UserAggregate user = userRepository.findById(UserId.of(userId))
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        return mapper.toDetailResponse(user);
    }
    
    // Paginated query (read-only)
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> findAllUsers(Pageable pageable) {
        Page<UserAggregate> page = userRepository.findAll(pageable);
        return PagedResponse.from(page, mapper::toResponse);
    }
}
```

**Transaction Guidelines**:
- **Class-level `@Transactional`**: Default for all methods (read-write)
- **Method-level `@Transactional`**: Override class-level behavior
- **`@Transactional(readOnly = true)`**: For queries, improves performance
- **Location**: Application services (`domain-application/{context}/service/`)
- **NOT in**: Domain core, controllers, or repository adapters

## Critical Anti-Patterns to Avoid

❌ **Mixing JPA entities with domain entities** - Keep `XJpaEntity` in data layer, `XAggregate`/`XEntity` in domain  
❌ **Using Spring annotations in `domain-core`** - No `@Service`, `@Component`, `@Autowired` in pure domain  
❌ **Forgetting `XJpaEntity` suffix on persistence entities** - Always use suffix to distinguish from domain entities  
❌ **Returning full child objects instead of projections** - Use `XSummaryProjection` for nested data  
❌ **Skipping pagination on list endpoints** - ALL list endpoints must return `PagedResponse<T>`  
❌ **Using plain `Entity` suffix** - Ambiguous! Use `XEntity` (domain) or `XJpaEntity` (persistence)  
❌ **Generating IDs with `UUID.randomUUID()`** - Always use UUIDv7: `UuidCreator.getTimeOrderedEpoch()`  
❌ **Using `@Autowired` field injection** - Always use constructor injection  
❌ **Putting business logic in JPA entities** - Business logic belongs in Aggregates and Domain Services  
❌ **Exposing JPA entities in API responses** - Always map to response DTOs  
❌ **Forgetting base class extension** - Aggregates extend `BaseAggregate`, Entities extend `BaseEntity`

## Quick Reference

### Creating New Bounded Context Features

**Step-by-step implementation order**:

1. **Domain Model** (`domain-core/{context}/model/`)
   - Create ID class: `XId implements BaseId<UUID>` with UUIDv7 generation
   - Create Aggregate: `XAggregate extends BaseAggregate<XId>` (aggregate root)
   - Create Entities: `XEntity extends BaseEntity<XId>` (child entities)
   - Create Value Objects: `XValue` (immutable classes)
   - Add domain services in `domain-core/{context}/service/` if needed

2. **Ports** (`domain-application/{context}/port/`)
   - Input ports: `X{UseCase}UseCase` interfaces
   - Output ports: `XRepositoryPort` interfaces

3. **DTOs** (`domain-application/{context}/dto/`)
   - Request DTOs: `CreateXRequest`, `UpdateXRequest`, `SearchXRequest`
   - Response DTOs: `XResponse`, `XDetailResponse`, `XSummaryResponse`
   - Include Jakarta Validation annotations on request DTOs

4. **Application Services** (`domain-application/{context}/service/`)
   - Implement use case interfaces
   - Orchestrate domain logic
   - Map between domain and DTOs

5. **Domain-DTO Mappers** (`domain-application/{context}/mapper/`)
   - MapStruct interfaces for Domain ↔ DTO
   - `@Mapper(componentModel = "spring")`

6. **JPA Entities** (`authservice-data/data/{context}/entity/`)
   - Create `XJpaEntity` with JPA annotations
   - MUST use `JpaEntity` suffix

7. **Projections** (`authservice-data/data/{context}/projection/`)
   - Create `XSummaryProjection` interfaces for minimal data
   - Used in parent-child relationships

8. **Repositories** (`authservice-data/data/{context}/repository/`)
   - Extend Spring Data JPA repositories
   - Support pagination on list operations

9. **Repository Adapters** (`authservice-data/data/{context}/adapter/`)
   - Implement repository ports from domain-application
   - Bridge between Spring Data repos and domain ports

10. **JPA-Domain Mappers** (`authservice-data/data/{context}/mapper/`)
    - MapStruct interfaces for `XJpaEntity` ↔ `XAggregate`/`XEntity`
    - `@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)`

11. **REST Controllers** (`authservice-application/controller/`)
    - Create `XController` with `@RestController`
    - Delegate to application services
    - Support pagination on list endpoints
    - Map to API response DTOs

### Key Files & Locations

- **Module Registration**: `auth-service/settings.gradle`
- **Main Application**: `authservice-application/src/main/java/.../AuthServiceApplication.java`
- **Base Classes**: `auth-service-common/src/main/java/.../common/domain/`
- **Component Scanning**: Application scans `me.namila.service.auth.{application,domain.application,data}`
- **Architecture Docs**: `DDD-SPRINGBOOT-SETUP.md` (detailed patterns)
- **Implementation Plan**: `auth-service-plan.md` (API designs, database schema)
- **Infrastructure**: `infra/docker-compose.yaml` (local development)

### Example Package Structure

```
me.namila.service.auth.domain.core.identity/
├── model/
│   ├── aggregate/UserAggregate.java
│   ├── entity/ProfileEntity.java
│   ├── value/EmailValue.java
│   └── id/UserId.java
├── service/UserProvisioningService.java
└── exception/UserNotFoundException.java

me.namila.service.auth.domain.application.identity/
├── port/
│   ├── input/UserManagementUseCase.java
│   └── output/UserRepositoryPort.java
├── service/UserApplicationService.java
├── dto/
│   ├── request/CreateUserRequest.java
│   └── response/UserResponse.java
└── mapper/UserDtoMapper.java

me.namila.service.auth.data.identity/
├── entity/UserJpaEntity.java
├── repository/UserJpaRepository.java
├── projection/UserSummaryProjection.java
├── adapter/UserRepositoryAdapter.java
└── mapper/UserEntityMapper.java

me.namila.service.auth.application.controller/
└── UserController.java
```

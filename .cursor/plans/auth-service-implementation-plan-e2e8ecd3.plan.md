<!-- e2e8ecd3-26a5-4e79-91fc-c6291eb487f5 c13e3d8f-c7c4-477f-b7ec-a207d03d5594 -->
# Auth Service Implementation Plan

## Overview

This plan implements the Authentication & Authorization Service using Domain-Driven Design (DDD) principles with a multi-module Spring Boot architecture. The implementation is divided into 8 phases over 17 weeks, with each phase containing specific, trackable tasks.

project group: me.namila.service.auth

project id: auth-service

version: 0.0.1

## Project Structure

```
auth-service/
├── auth-service-common/         # Common/Shared Module (Base classes)
│   └── domain/                  # BaseId, BaseEntity, BaseAggregate
├── authservice-application/     # Application/API Layer
├── authservice-domain/          # Domain Layer
│   ├── domain-core/             # Core Domain Logic (Aggregates, Entities, Values)
│   └── domain-application/      # Ports, Services, DTOs (request/response)
└── authservice-data/           # Data Layer (Single Module)
    └── data/                   # Java package structure
        ├── identity/            # Identity Context Package
        ├── authorization/       # Authorization Context Package
        ├── configuration/       # Configuration Context Package
        └── governance/          # Governance Context Package
```

## Architectural Patterns

### Base Classes (auth-service-common)

- **BaseId<T>**: Generic identifier interface with UUIDv7 generation
  - Static methods: `of(String)`, `of(T)`, `generate()`
  - Uses `com.github.f4b6a3:uuid-creator:5.2.0`
  - Generation: `UuidCreator.getTimeOrderedEpoch()`
- **BaseEntity<ID extends BaseId<?>>**: Base class for entities
- **BaseAggregate<ID extends BaseId<?>>**: Base class for aggregate roots

### Naming Conventions

- **Aggregates**: `XAggregate` (e.g., `UserAggregate`, `RoleAggregate`)
- **Entities**: `XEntity` (e.g., `ProfileEntity`, `AddressEntity`)
- **Value Objects**: `XValue` (e.g., `EmailValue`, `PasswordValue`)
- **ID Classes**: `XId` implementing `BaseId<UUID>`

### DTO Organization

- **Request DTOs**: Separate `dto/request/` package
- **Response DTOs**: Separate `dto/response/` package
- **Pagination**: All list endpoints support pagination with `PagedResponse<T>`

### Data Access Patterns

- **Spring Data Projections**: For parent-child relationships
- **Minimal Child Data**: In parent responses (use projections)
- **Full Child Details**: Via separate API endpoints
- **Pagination Support**: On all collection endpoints

## Phase 1: Foundation (Weeks 1-2)

**Goal**: Set up project structure, database schema, and core domain models

### Tasks:

1. **Project Setup**

   - Create root Gradle project structure
   - Set up multi-module Gradle build (settings.gradle.kts)
   - Configure root build.gradle.kts with common plugins
   - Create libs.versions.toml for dependency version management
   - Set up Gradle wrapper

2. **Module Creation**

   - Create auth-service-common module with build.gradle.kts
   - Create authservice-application module with build.gradle.kts
   - Create authservice-domain parent module
   - Create domain-core submodule with build.gradle.kts
   - Create domain-application submodule with build.gradle.kts
   - Create authservice-data module with build.gradle.kts
   - Create Java package structure: data/identity, data/authorization, data/configuration, data/governance
   - Create sub-packages for each context: entity, repository, projection, adapter, mapper, config
   - Create shared config package: data/config
   - Configure module dependencies (depends on auth-service-common and domain-application)

3. **Dependencies Configuration**

   - Add Spring Boot BOM to root build
   - Add UUID Creator (`com.github.f4b6a3:uuid-creator:5.2.0`) to common module
   - Configure MapStruct in application and data modules
   - Configure Lombok in all modules (required for domain models - no manual getters/setters)
   - Add PostgreSQL driver to data module
   - Add Redis client (Lettuce) to data module
   - Add Spring Data JPA to data module
   - Add Flyway/Liquibase for migrations

3a. **Common Module - Base Classes**

   - Create BaseId<T> interface in auth-service-common
     - Add getValue() and setValue(T value) methods
     - Add static factory methods: of(String), of(T), generate()
   - Create BaseEntity<ID extends BaseId<?>> abstract class
     - Add ID field, createdAt, updatedAt
     - Implement equals/hashCode based on ID
   - Create BaseAggregate<ID extends BaseId<?>> extending BaseEntity
     - Add domain events list
     - Add registerDomainEvent(), getDomainEvents(), clearDomainEvents() methods

4. **Database Schema**

   - Create Flyway migration scripts for identity schema
   - Create Flyway migration scripts for authorization schema
   - Create Flyway migration scripts for configuration schema
   - Create Flyway migration scripts for governance schema
   - Add database indexes as per schema design
   - Set up database connection configuration

5. **Domain Core Models - Identity Context**

   - Create UserId class implementing BaseId<UUID> with UUIDv7 generation
   - Create UserAggregate extending BaseAggregate<UserId> (using Lombok)
   - Create ProfileEntity extending BaseEntity<ProfileId> (using Lombok)
   - Create FederatedIdentityEntity extending BaseEntity<FederatedIdentityId> (using Lombok)
   - Create UserStatus value object/enum
   - Create EmailValue value object
   - Create UsernameValue value object
   - Create PasswordValue value object
   - Add domain validation rules
   - Note: All aggregates extend BaseAggregate, entities extend BaseEntity, values are immutable

6. **Domain Core Models - Authorization Context**

   - Create RoleId class implementing BaseId<UUID>
   - Create RoleAggregate extending BaseAggregate<RoleId> (using Lombok)
   - Create PermissionEntity extending BaseEntity<PermissionId> (using Lombok)
   - Create UserRoleAssignmentAggregate extending BaseAggregate<AssignmentId> (using Lombok)
   - Create PolicyAggregate extending BaseAggregate<PolicyId> (using Lombok)
   - Create value objects: RoleTypeValue, AssignmentScopeValue, etc.
   - Note: Use naming convention XAggregate, XEntity, XValue

7. **Domain Core Models - Configuration Context**

   - Create OIDCProviderConfigId implementing BaseId<UUID>
   - Create OIDCProviderConfigAggregate extending BaseAggregate<OIDCProviderConfigId>
   - Create AttributeMappingValue value object
   - Create RoleMappingValue value object
   - Create JITProvisioningValue value object
   - Note: Configuration aggregates follow same patterns

8. **Domain Core Models - Governance Context**

   - Create AuditLogId implementing BaseId<UUID>
   - Create AuditLogEntity extending BaseEntity<AuditLogId>
   - Create AccessCertificationEntity extending BaseEntity<CertificationId>
   - Create audit event types enum
   - Note: Governance models follow base class patterns

9. **Domain Services**

   - Create UserDomainService in domain-core
   - Create RoleHierarchyService in domain-core
   - Create PermissionAggregationService in domain-core

10. **Repository Ports (Domain Application)**

    - Create UserRepositoryPort interface
    - Create FederatedIdentityRepositoryPort interface
    - Create RoleRepositoryPort interface
    - Create PermissionRepositoryPort interface
    - Create UserRoleAssignmentRepositoryPort interface
    - Create PolicyRepositoryPort interface
    - Create OIDCProviderConfigRepositoryPort interface
    - Create AuditLogRepositoryPort interface

11. **JPA Entities (Data Module - Context-Based Java Packages)**

**data.identity package**:

    - Create UserJpaEntity (JPA entity for persistence)
    - Create UserProfileJpaEntity
    - Create FederatedIdentityJpaEntity
    - Note: JPA entities are separate from domain models

**data.authorization package**:

    - Create RoleJpaEntity
    - Create PermissionJpaEntity
    - Create UserRoleAssignmentJpaEntity
    - Create PolicyJpaEntity

**data.configuration package**:

    - Create OIDCProviderConfigJpaEntity
    - Create AttributeMappingJpaEntity

**data.governance package**:

    - Create AuditLogJpaEntity
    - Create AccessCertificationJpaEntity

11a. **Spring Data Projections (Data Module - Context-Based Java Packages)**

**data.identity package**:

    - Create UserSummaryProjection interface (minimal user data)
    - Create UserProfileSummaryProjection interface
    - Create FederatedIdentitySummaryProjection interface

**data.authorization package**:

    - Create RoleSummaryProjection interface (minimal role data)
    - Create PermissionSummaryProjection interface (minimal permission data)
    - Create UserRoleAssignmentSummaryProjection interface

**data.configuration package**:

    - Create OIDCProviderConfigSummaryProjection interface

**data.governance package**:

    - Create AuditLogSummaryProjection interface
    - Create AccessCertificationSummaryProjection interface

    - Note: Projections used for parent-child relationships to minimize data transfer

12. **Repository Adapters (Data Module - Context-Based Java Packages)**

**data.identity package**:

    - Implement UserRepositoryAdapter with JPA
    - Implement FederatedIdentityRepositoryAdapter
    - Add pagination support to all repository methods

**data.authorization package**:

    - Implement RoleRepositoryAdapter with projection support
    - Implement PermissionRepositoryAdapter
    - Implement UserRoleAssignmentRepositoryAdapter
    - Implement PolicyRepositoryAdapter
    - Add pagination support to all repository methods

**data.configuration package**:

    - Implement OIDCProviderConfigRepositoryAdapter
    - Add pagination support to all repository methods

**data.governance package**:

    - Implement AuditLogRepositoryAdapter
    - Implement AccessCertificationRepositoryAdapter
    - Add pagination support to all repository methods

13. **Entity-Domain Mappers (Data Module - Context-Based Java Packages)**

**data.identity package**:

    - Create UserEntityMapper (MapStruct) with `unmappedTargetPolicy = ReportingPolicy.IGNORE`
    - Create FederatedIdentityEntityMapper
    - Create UserProfileEntityMapper

**data.authorization package**:

    - Create RoleEntityMapper with lazy collection ignore mappings
    - Create PermissionEntityMapper
    - Create PolicyEntityMapper
    - Create UserRoleAssignmentEntityMapper

**data.configuration package**:

    - Create OIDCProviderConfigEntityMapper

**data.governance package**:

    - Create AuditLogEntityMapper
    - Create AccessCertificationEntityMapper

    - **Best Practice**: Always use `@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)`
    - **Best Practice**: Always ignore lazy collections: `@Mapping(target = "lazyCollection", ignore = true)`

14. **Infrastructure Configuration**

**Shared Configuration (data.config package)**:

    - Create DatabaseConfig class in data.config package
    - Create RedisConfig class in data.config package
    - Create JpaConfig class in data.config package
    - Create CacheConfig for Redis
    - Set up HikariCP connection pool
    - Configure JPA/Hibernate settings
    - Configure batch size: `hibernate.jdbc.batch_size=20`
    - Enable `hibernate.order_inserts=true` and `hibernate.order_updates=true`
    - Configure `@EnableJpaAuditing` for audit fields
    - Set up transaction management with `@Transactional(readOnly = true)` default

**Context-Specific Configuration (each context package)**:

    - Create IdentityDataConfig in data.identity.config package
    - Create AuthorizationDataConfig in data.authorization.config package
    - Create ConfigurationDataConfig in data.configuration.config package
    - Create GovernanceDataConfig in data.governance.config package
    - Configure context-specific JPA entity scanning (if needed)
    - Configure context-specific repository scanning (if needed)

14a. **JPA Best Practices Implementation**

    - Implement ID-First Pattern for complex searches in repositories
    - Create projection interfaces for list views
    - Use JOIN FETCH for preventing N+1 queries
    - Configure all repositories with pagination support
    - Add database indexes on filtered/sorted columns
    - Implement soft delete pattern if needed (using @SQLDelete and @Where)
    - Configure MapStruct to ignore lazy collections
    - Use `@Transactional(readOnly = true)` for all read operations

15. **Exception Framework**

    - Create base DomainException in domain-core
    - Create domain-specific exceptions
    - Create GlobalExceptionHandler in application module
    - Set up error response DTOs

## Phase 2: User Management & Basic Auth (Weeks 3-4)

**Goal**: Implement user management APIs and basic JWT authentication

### Tasks:

1. **Application Services - User Management**

   - Create UserApplicationService in domain-application
   - Implement create user use case
   - Implement get user by ID use case
   - Implement update user use case
   - Implement list users with pagination use case
   - Implement delete/deactivate user use case

2. **DTOs - User Management**

   - Create CreateUserRequest DTO in dto/request/ package
   - Create UpdateUserRequest DTO in dto/request/ package
   - Create UserResponse DTO in dto/response/ package
   - Create UserDetailResponse DTO in dto/response/ package
   - Create UserSummaryResponse DTO in dto/response/ package
   - Create PagedResponse<T> wrapper for pagination
   - Add validation annotations to request DTOs
   - Note: Separate request and response DTOs in distinct packages

3. **Mappers - User Management**

   - Create UserDtoMapper (MapStruct) in domain-application
   - Map CreateUserRequest to UserAggregate
   - Map UpdateUserRequest to UserAggregate
   - Map UserAggregate to UserResponse
   - Map UserAggregate to UserDetailResponse
   - Map UserAggregate to UserSummaryResponse

4. **REST Controllers - User Management**

   - Create UserController in application module
   - Implement POST /api/v1/users endpoint
   - Implement GET /api/v1/users/{userId} endpoint
   - Implement PATCH /api/v1/users/{userId} endpoint
   - Implement GET /api/v1/users endpoint with pagination
   - Implement DELETE /api/v1/users/{userId} endpoint
   - Add OpenAPI annotations

5. **JWT Token Provider**

   - Create JwtTokenProvider service
   - Implement token generation (RS256)
   - Implement token validation
   - Implement token claims extraction
   - Configure JWT signing keys

6. **JWT Authentication Filter**

   - Create JwtAuthenticationFilter
   - Implement token extraction from headers
   - Implement token validation
   - Set up Spring Security filter chain

7. **Spring Security Configuration**

   - Create SecurityConfig class
   - Configure JWT authentication
   - Set up public endpoints
   - Configure CORS
   - Set up security filter chain

8. **API Documentation**

   - Configure SpringDoc OpenAPI
   - Set up Swagger UI
   - Document all user management endpoints
   - Add request/response examples

9. **Input Validation**

   - Configure Jakarta Validation
   - Add validation to DTOs
   - Create custom validators if needed
   - Set up validation error handling

10. **Testing - User Management**

    - Write unit tests for UserApplicationService
    - Write unit tests for UserController
    - Write integration tests for user APIs
    - Test JWT token generation and validation

## Phase 3: OIDC Integration (Weeks 5-6)

**Goal**: Implement OIDC authentication flow with JIT provisioning

### Tasks:

1. **OIDC Provider Configuration - Application Service**

   - Create OIDCProviderApplicationService
   - Implement create provider use case
   - Implement update provider use case
   - Implement get provider use case
   - Implement list providers use case
   - Implement delete provider use case
   - Implement test provider configuration use case

2. **OIDC Provider DTOs**

   - Create OIDCProviderConfigRequest DTO
   - Create OIDCProviderConfigResponse DTO
   - Create OIDCProviderListResponse DTO
   - Add validation for OIDC configuration

3. **OIDC Provider Mappers**

   - Create OIDCProviderDtoMapper
   - Map configuration DTOs to domain
   - Map domain to response DTOs

4. **OIDC Provider Controller**

   - Create OIDCProviderController
   - Implement GET /api/v1/oidc-providers endpoint
   - Implement GET /api/v1/oidc-providers/{providerId} endpoint
   - Implement POST /api/v1/oidc-providers endpoint
   - Implement PUT /api/v1/oidc-providers/{providerId} endpoint
   - Implement DELETE /api/v1/oidc-providers/{providerId} endpoint
   - Implement POST /api/v1/oidc-providers/{providerId}/test endpoint

5. **OIDC Authentication Service**

   - Create OIDCAuthenticationService
   - Implement initiate OIDC flow
   - Implement handle OIDC callback
   - Implement token exchange with IdP
   - Implement user info retrieval
   - Implement PKCE flow support

6. **JIT Provisioning Service**

   - Create JITProvisioningService
   - Implement user creation logic
   - Implement user update logic
   - Implement federated identity linking
   - Implement role synchronization

7. **Attribute Transformation Service**

   - Create AttributeTransformationService
   - Implement attribute mapping logic
   - Implement transformation rules engine
   - Support custom transformation expressions

8. **Role Mapping Service**

   - Create RoleMappingService
   - Implement explicit mapping strategy
   - Implement pattern-based mapping
   - Implement script-based mapping (optional)
   - Map external groups to internal roles

9. **Domain Services - Identity Federation**

   - Create IdentityFederationService in domain-core
   - Create UserProvisioningService in domain-core
   - Implement domain rules for federation

10. **Authentication DTOs**

    - Create OIDCInitiateRequest DTO
    - Create OIDCCallbackRequest DTO
    - Create AuthenticationResponse DTO
    - Create TokenRefreshRequest DTO
    - Create LogoutRequest DTO

11. **Authentication Controller**

    - Create AuthenticationController
    - Implement POST /api/v1/auth/oidc/initiate endpoint
    - Implement POST /api/v1/auth/oidc/callback endpoint
    - Implement POST /api/v1/auth/token/refresh endpoint
    - Implement POST /api/v1/auth/logout endpoint

12. **Token Management**

    - Implement refresh token generation
    - Implement token blacklist (Redis)
    - Implement token revocation
    - Add token rotation logic

13. **Testing - OIDC Integration**

    - Write unit tests for OIDC services
    - Write integration tests for OIDC flow
    - Test JIT provisioning scenarios
    - Test attribute and role mapping

## Phase 4: Role & Permission Management (Weeks 7-8)

**Goal**: Implement role and permission management with assignment APIs

### Tasks:

1. **Role Application Service**

   - Create RoleApplicationService
   - Implement create role use case
   - Implement update role use case
   - Implement get role use case
   - Implement list roles use case
   - Implement delete role use case
   - Implement add permissions to role use case
   - Implement remove permission from role use case

2. **Permission Application Service**

   - Create PermissionApplicationService
   - Implement create permission use case
   - Implement get permission use case
   - Implement list permissions use case
   - Implement delete permission use case

3. **Role Assignment Application Service**

   - Create RoleAssignmentApplicationService
   - Implement assign role to user use case
   - Implement revoke role assignment use case
   - Implement get user assignments use case
   - Implement bulk assignment use case
   - Implement get user effective permissions use case

4. **Role DTOs**

   - Create CreateRoleRequest DTO
   - Create UpdateRoleRequest DTO
   - Create RoleResponse DTO
   - Create RoleListResponse DTO

5. **Permission DTOs**

   - Create CreatePermissionRequest DTO
   - Create PermissionResponse DTO
   - Create PermissionListResponse DTO

6. **Assignment DTOs**

   - Create AssignRoleRequest DTO
   - Create RevokeRoleRequest DTO
   - Create RoleAssignmentResponse DTO
   - Create BulkAssignmentRequest DTO
   - Create UserPermissionsResponse DTO

7. **Role Mappers**

   - Create RoleDtoMapper
   - Map role DTOs to domain
   - Map domain to role DTOs

8. **Role Controller**

   - Create RoleController
   - Implement GET /api/v1/roles endpoint
   - Implement GET /api/v1/roles/{roleId} endpoint
   - Implement POST /api/v1/roles endpoint
   - Implement PUT /api/v1/roles/{roleId} endpoint
   - Implement DELETE /api/v1/roles/{roleId} endpoint
   - Implement POST /api/v1/roles/{roleId}/permissions endpoint
   - Implement DELETE /api/v1/roles/{roleId}/permissions/{permissionId} endpoint

9. **Permission Controller**

   - Create PermissionController
   - Implement GET /api/v1/permissions endpoint
   - Implement POST /api/v1/permissions endpoint
   - Implement DELETE /api/v1/permissions/{permissionId} endpoint

10. **Role Assignment Controller**

    - Create RoleAssignmentController
    - Implement GET /api/v1/assignments endpoint
    - Implement POST /api/v1/assignments endpoint
    - Implement DELETE /api/v1/assignments/{assignmentId} endpoint
    - Implement POST /api/v1/assignments/bulk endpoint
    - Implement GET /api/v1/users/{userId}/permissions endpoint

11. **Role Hierarchy Implementation**

    - Implement role hierarchy resolution in RoleHierarchyService
    - Add parent role support
    - Implement inherited permissions calculation
    - Add hierarchy validation

12. **Permission Aggregation**

    - Implement effective permissions calculation
    - Aggregate permissions from multiple roles
    - Handle permission conflicts
    - Cache effective permissions

13. **Testing - Role & Permission Management**

    - Write unit tests for role services
    - Write unit tests for permission services
    - Write integration tests for role APIs
    - Test role hierarchy resolution
    - Test permission aggregation

## Phase 5: Authorization Engine (PDP) (Weeks 9-11)

**Goal**: Implement Policy Decision Point with RBAC, ABAC, and policy evaluation

### Tasks:

1. **Policy Decision Point Service**

   - Create PolicyDecisionPointService
   - Implement authorization check use case
   - Implement batch authorization check use case
   - Integrate with policy evaluation engine

2. **Policy Evaluation Service**

   - Create PolicyEvaluationService in domain-core
   - Implement RBAC evaluation logic
   - Implement ABAC evaluation logic
   - Implement policy condition evaluation
   - Implement policy priority handling
   - Implement default deny logic

3. **Policy Information Point Service**

   - Create PolicyInformationPointService
   - Implement user attribute retrieval
   - Implement resource metadata retrieval
   - Implement external context retrieval
   - Cache PIP data

4. **Policy Application Service**

   - Create PolicyApplicationService
   - Implement create policy use case
   - Implement update policy use case
   - Implement get policy use case
   - Implement list policies use case
   - Implement delete/disable policy use case
   - Implement policy versioning

5. **Authorization DTOs**

   - Create AuthorizationRequest DTO
   - Create AuthorizationDecision DTO
   - Create BatchAuthorizationRequest DTO
   - Create BatchAuthorizationResponse DTO

6. **Policy DTOs**

   - Create CreatePolicyRequest DTO
   - Create UpdatePolicyRequest DTO
   - Create PolicyResponse DTO
   - Create PolicyListResponse DTO

7. **Authorization Controller**

   - Create AuthorizationController
   - Implement POST /api/v1/access-control/check endpoint
   - Implement POST /api/v1/access-control/batch-check endpoint
   - Add OpenAPI documentation

8. **Policy Controller**

   - Create PolicyController
   - Implement GET /api/v1/policies endpoint
   - Implement GET /api/v1/policies/{policyId} endpoint
   - Implement POST /api/v1/policies endpoint
   - Implement PUT /api/v1/policies/{policyId} endpoint
   - Implement DELETE /api/v1/policies/{policyId} endpoint

9. **Condition Evaluator**

   - Create ConditionEvaluator in domain-core
   - Implement attribute-based condition evaluation
   - Support comparison operators
   - Support logical operators (AND, OR, NOT)
   - Support time-based conditions

10. **Permission Resolver**

    - Create PermissionResolver in domain-core
    - Implement permission matching logic
    - Support wildcard matching
    - Support scope-based matching

11. **Redis Caching Integration**

    - Implement user permissions cache
    - Implement role hierarchy cache
    - Implement policy metadata cache
    - Implement cache invalidation on updates
    - Set up cache TTLs

12. **Performance Optimization**

    - Optimize permission lookup queries
    - Implement batch permission checks
    - Add query result caching
    - Optimize policy evaluation order
    - Add performance metrics

13. **Testing - Authorization Engine**

    - Write unit tests for PDP service
    - Write unit tests for policy evaluation
    - Write integration tests for authorization APIs
    - Test RBAC scenarios
    - Test ABAC scenarios
    - Test policy priority
    - Performance testing

## Phase 6: Audit & Governance (Weeks 12-13)

**Goal**: Implement comprehensive audit logging and compliance features

### Tasks:

1. **Audit Service**

   - Create AuditService
   - Implement audit log creation
   - Implement async audit logging
   - Implement audit log query use case
   - Implement user activity audit trail

2. **Compliance Reporting Service**

   - Create ComplianceReportingService
   - Implement SOX compliance report generation
   - Implement GDPR compliance report generation
   - Implement HIPAA compliance report generation
   - Implement report export functionality

3. **Access Certification Service**

   - Create AccessCertificationService
   - Implement initiate certification use case
   - Implement certification review workflow
   - Implement certification approval/rejection
   - Track certification status

4. **Audit DTOs**

   - Create AuditLogQueryRequest DTO
   - Create AuditLogResponse DTO
   - Create AuditLogListResponse DTO
   - Create ComplianceReportRequest DTO
   - Create ComplianceReportResponse DTO
   - Create AccessCertificationRequest DTO
   - Create AccessCertificationResponse DTO

5. **Audit Controller**

   - Create AuditController
   - Implement GET /api/v1/audit/logs endpoint with filtering
   - Implement GET /api/v1/audit/user-activity/{userId} endpoint
   - Add pagination support
   - Add date range filtering

6. **Compliance Controller**

   - Create ComplianceController
   - Implement GET /api/v1/audit/compliance-report endpoint
   - Implement POST /api/v1/audit/access-certifications endpoint
   - Add report type parameter

7. **Audit Logging Infrastructure**

   - Set up async audit logging
   - Implement audit log persistence
   - Set up audit log partitioning (by month)
   - Configure audit log retention
   - Implement audit log archival

8. **PDP/PEP Log Correlation**

   - Add correlation IDs to audit logs
   - Implement log correlation logic
   - Create correlation query endpoints
   - Link PDP decisions with PEP enforcement

9. **Domain Service - Audit**

   - Create AuditLogService in domain-core
   - Implement domain rules for audit logging
   - Define audit event types

10. **Dashboard Metrics**

    - Implement authentication metrics
    - Implement authorization decision metrics
    - Implement user activity metrics
    - Implement policy evaluation metrics
    - Expose metrics via actuator endpoints

11. **Testing - Audit & Governance**

    - Write unit tests for audit service
    - Write integration tests for audit APIs
    - Test compliance report generation
    - Test access certification workflow
    - Test log correlation

## Phase 7: Integration & Testing (Weeks 14-15)

**Goal**: Comprehensive testing, security validation, and documentation

### Tasks:

1. **Integration Tests - Authentication**

   - Create OIDCAuthenticationIntegrationTest
   - Test full OIDC flow
   - Test JIT provisioning
   - Test token refresh
   - Test logout

2. **Integration Tests - User Management**

   - Create UserManagementIntegrationTest
   - Test user CRUD operations
   - Test user pagination
   - Test user validation

3. **Integration Tests - Authorization**

   - Create PolicyEvaluationIntegrationTest
   - Test RBAC authorization
   - Test ABAC authorization
   - Test policy evaluation
   - Test batch authorization

4. **Integration Tests - Audit**

   - Create AuditLoggingIntegrationTest
   - Test audit log creation
   - Test audit log querying
   - Test compliance reports

5. **API Contract Tests**

   - Set up contract testing framework
   - Create API contracts for all endpoints
   - Validate request/response schemas
   - Test backward compatibility

6. **Performance Tests**

   - Set up JMeter or Gatling
   - Create load test scenarios
   - Test authentication endpoint performance
   - Test authorization endpoint performance
   - Test database query performance
   - Identify bottlenecks

7. **Security Testing**

   - Run OWASP dependency check
   - Test JWT token security
   - Test SQL injection prevention
   - Test XSS prevention
   - Test CSRF protection
   - Test rate limiting
   - Security audit review

8. **Unit Test Coverage**

   - Achieve 80%+ code coverage
   - Test all domain services
   - Test all application services
   - Test all controllers
   - Test all mappers
   - Test exception handling

9. **Documentation**

   - Complete API documentation
   - Add code comments and JavaDoc
   - Create developer guide
   - Create deployment guide
   - Create troubleshooting guide
   - Update README

10. **Test Data Management**

    - Create test data fixtures
    - Set up test database seeding
    - Create test user accounts
    - Create test roles and permissions

## Phase 8: Deployment & Production Readiness (Weeks 16-17)

**Goal**: Prepare for production deployment with monitoring and DevOps

### Tasks:

1. **Docker Containerization**

   - Create Dockerfile for application
   - Create .dockerignore file
   - Create docker-compose for local development
   - Optimize Docker image size
   - Set up multi-stage builds

2. **Kubernetes Manifests**

   - Create deployment.yaml
   - Create service.yaml
   - Create configmap.yaml
   - Create secret.yaml
   - Create ingress.yaml
   - Set up resource limits

3. **Health Checks**

   - Implement Spring Boot Actuator health endpoint
   - Implement database health check
   - Implement Redis health check
   - Create readiness probe
   - Create liveness probe

4. **Monitoring Setup**

   - Configure Prometheus metrics
   - Create Grafana dashboards
   - Set up alerting rules
   - Monitor application metrics
   - Monitor infrastructure metrics

5. **Logging Aggregation**

   - Set up structured logging (JSON)
   - Configure log levels per environment
   - Set up ELK stack or Loki
   - Implement log correlation IDs
   - Set up log retention policies

6. **Production Configuration**

   - Create production application.yml
   - Externalize sensitive configuration
   - Set up environment-specific configs
   - Configure connection pools
   - Set up SSL/TLS

7. **Database Migrations**

   - Create migration scripts
   - Set up migration automation
   - Create rollback scripts
   - Test migrations in staging

8. **Backup and Recovery**

   - Create database backup scripts
   - Set up automated backups
   - Test recovery procedures
   - Document backup retention policy

9. **Runbook Documentation**

   - Document deployment procedures
   - Document rollback procedures
   - Document troubleshooting steps
   - Document common issues and solutions
   - Create incident response guide

10. **CI/CD Pipeline**

    - Set up GitHub Actions or Jenkins
    - Configure build pipeline
    - Configure test pipeline
    - Configure deployment pipeline
    - Set up automated testing

11. **Security Hardening**

    - Review security configurations
    - Set up secrets management
    - Configure network policies
    - Set up WAF rules
    - Review access controls

12. **Performance Tuning**

    - Optimize JVM settings
    - Tune database connection pool
    - Configure Redis connection pool
    - Set up connection timeouts
    - Optimize query performance

## Tracking and Progress

Each phase should be tracked independently with:

- GitHub issues for each phase
- Task completion tracking
- Dependency tracking between tasks
- Progress metrics

## Success Criteria

- All 8 phases completed
- 80%+ test coverage
- All APIs documented
- Production-ready deployment
- Performance benchmarks met
- Security audit passed

### To-dos

- [x] Create root Gradle project structure with multi-module setup, settings.gradle.kts, root build.gradle.kts, and libs.versions.toml
- [x] Create auth-service-common module with build.gradle.kts
- [x] Create all modules: authservice-application, authservice-domain (with domain-core and domain-application submodules), and authservice-data
- [x] Configure all dependencies: Spring Boot, UUID Creator (5.2.0), MapStruct, Lombok, PostgreSQL, Redis, Spring Data JPA, Flyway
- [x] Implement BaseId<T> interface in auth-service-common with of(String), of(T), and generate() methods
- [x] Implement BaseEntity<ID> abstract class with ID, createdAt, updatedAt, equals/hashCode
- [x] Implement BaseAggregate<ID> extending BaseEntity with domain events support
- [x] Create Flyway migration scripts for all schemas: identity, authorization, configuration, governance with UUID columns
- [x] Create ID classes: UserId, RoleId, PermissionId, PolicyId, UserRoleAssignmentId, OIDCProviderConfigId, AuditLogId, UserProfileId, FederatedIdentityId, AccessCertificationId
- [x] Create Identity domain models: UserAggregate, UserProfileEntity, FederatedIdentityEntity, value objects (EmailValue, UsernameValue, UserStatus)
- [x] Create Authorization domain models: RoleAggregate, PermissionEntity, UserRoleAssignmentAggregate, PolicyAggregate with proper naming
- [x] Create Configuration domain models: OIDCProviderConfigAggregate
- [x] Create Governance domain models: AuditLogEntity extending BaseEntity
- [x] Create repository port interfaces in domain-application for all aggregates with pagination support
- [x] Create authservice-data module with build.gradle.kts
- [x] Create Java package structure: data/identity, data/authorization, data/configuration, data/governance, data/config
- [x] Create sub-packages for each context: entity, repository, projection, adapter, mapper, config
- [x] Create JPA entities in data.identity package: UserEntity, UserProfileEntity, FederatedIdentityEntity
- [x] Create JPA entities in data.authorization package: RoleEntity, PermissionEntity, PolicyEntity, UserRoleAssignmentEntity
- [x] Create JPA entities in data.configuration package: OIDCProviderConfigEntity
- [x] Create JPA entities in data.governance package: AuditLogEntity
- [x] Create Spring Data Projections in data.identity package: UserSummaryProjection, UserProfileSummaryProjection, FederatedIdentitySummaryProjection
- [x] Create Spring Data Projections in data.authorization package: RoleSummaryProjection, PermissionSummaryProjection, UserRoleAssignmentSummaryProjection
- [x] Create Spring Data Projections in data.configuration package: OIDCProviderConfigSummaryProjection
- [x] Create Spring Data Projections in data.governance package: AuditLogSummaryProjection
- [x] Implement repository adapters in data.identity package using JPA with projection support and pagination
- [x] Implement repository adapters in data.authorization package using JPA with projection support and pagination
- [x] Implement repository adapters in data.configuration package using JPA with projection support and pagination
- [x] Implement repository adapters in data.governance package using JPA with projection support and pagination
- [x] Create MapStruct mappers in data.identity package for JPA Entity-Domain conversion
- [x] Create MapStruct mappers in data.authorization package for JPA Entity-Domain conversion
- [x] Create MapStruct mappers in data.configuration package for JPA Entity-Domain conversion
- [x] Create MapStruct mappers in data.governance package for JPA Entity-Domain conversion
- [x] Create shared configuration classes in data.config package: DatabaseConfig, RedisConfig, JpaConfig
- [x] Create IdentityDataConfig in data.identity.config package
- [x] Create AuthorizationDataConfig in data.authorization.config package
- [x] Create ConfigurationDataConfig in data.configuration.config package
- [x] Create GovernanceDataConfig in data.governance.config package
- [x] Create domain services: UserDomainService, RoleHierarchyService, PermissionAggregationService
- [x] Create exception framework: DomainException, domain-specific exceptions, GlobalExceptionHandler
- [x] Create UserApplicationService with all use cases: create, get, update, list with pagination, delete
- [x] Create user management Request DTOs in dto/request/: CreateUserRequest, UpdateUserRequest
- [x] Create user management Response DTOs in dto/response/: UserResponse, UserDetailResponse, UserSummaryResponse
- [x] Create PagedResponse<T> wrapper class for pagination support
- [x] Create UserDtoMapper (MapStruct) for Request DTO → Domain and Domain → Response DTO conversion
- [x] Create UserController with all REST endpoints, pagination support, and OpenAPI documentation
- [x] Create JwtTokenProvider service with RS256 token generation and validation
- [x] Create Spring Security configuration with JWT authentication filter and CORS setup
- [x] Configure SpringDoc OpenAPI and document all endpoints
- [x] Write unit tests for domain module (domain-core): UserDomainService, RoleHierarchyService, PermissionAggregationService
- [x] Write unit tests for domain-application module: UserApplicationService, UserDtoMapper (mock dependencies) - Mapper tests passing, service tests need mock fixes
- [ ] Create OIDCProviderApplicationService with CRUD operations and test configuration
- [ ] Create OIDC provider DTOs: OIDCProviderConfigRequest, OIDCProviderConfigResponse
- [ ] Create OIDCProviderController with all provider management endpoints
- [ ] Create OIDCAuthenticationService with initiate, callback, token exchange, and user info retrieval
- [ ] Create JITProvisioningService with user creation, update, identity linking, and role sync
- [ ] Create AttributeTransformationService with mapping and transformation rules engine
- [ ] Create RoleMappingService with explicit, pattern, and script-based mapping strategies
- [ ] Create AuthenticationController with OIDC initiate, callback, refresh, and logout endpoints
- [ ] Implement refresh token generation, token blacklist (Redis), and token revocation
- [ ] Create RoleApplicationService with CRUD operations and permission management
- [ ] Create PermissionApplicationService with CRUD operations
- [ ] Create RoleAssignmentApplicationService with assign, revoke, bulk, and effective permissions
- [ ] Create RoleController, PermissionController, and RoleAssignmentController with all endpoints
- [ ] Implement role hierarchy resolution and inherited permissions calculation
- [ ] Implement effective permissions calculation with caching
- [ ] Create PolicyDecisionPointService with authorization check and batch check
- [ ] Create PolicyEvaluationService with RBAC, ABAC, and condition evaluation logic
- [ ] Create PolicyInformationPointService for user attributes, resource metadata, and external context
- [ ] Create PolicyApplicationService with CRUD operations and versioning
- [ ] Create AuthorizationController and PolicyController with all endpoints
- [ ] Create ConditionEvaluator with attribute-based, comparison, logical, and time-based conditions
- [ ] Implement Redis caching for user permissions, role hierarchy, and policy metadata with invalidation
- [ ] Optimize permission lookups, batch checks, query caching, and add performance metrics
- [ ] Create AuditService with async logging, query, and user activity trail
- [ ] Create ComplianceReportingService with SOX, GDPR, and HIPAA report generation
- [ ] Create AccessCertificationService with certification workflow and status tracking
- [ ] Create AuditController and ComplianceController with all endpoints
- [ ] Set up async audit logging, persistence, partitioning, retention, and archival
- [ ] Implement PDP/PEP log correlation with correlation IDs and query endpoints
- [ ] Implement dashboard metrics for authentication, authorization, user activity, and policy evaluation
- [ ] Create integration tests for authentication, user management, authorization, and audit
- [ ] Set up API contract testing framework and validate all endpoint schemas
- [ ] Set up JMeter/Gatling and create load test scenarios for all critical endpoints
- [ ] Run OWASP checks, test JWT security, SQL injection, XSS, CSRF protection, and rate limiting
- [ ] Achieve 80%+ code coverage with unit tests for all services, controllers, and mappers
- [ ] Complete API documentation, JavaDoc, developer guide, deployment guide, and troubleshooting guide
- [ ] Create Dockerfile, docker-compose, and optimize Docker image with multi-stage builds
- [ ] Create Kubernetes manifests: deployment, service, configmap, secret, ingress with resource limits
- [ ] Implement Spring Boot Actuator health endpoints, readiness and liveness probes
- [ ] Configure Prometheus metrics, Grafana dashboards, and alerting rules
- [ ] Set up structured logging (JSON), ELK/Loki, log correlation IDs, and retention policies
- [ ] Create production configuration, externalize secrets, set up SSL/TLS, and connection pools
- [ ] Create migration automation scripts, rollback scripts, and test in staging
- [ ] Create database backup scripts, automated backups, recovery procedures, and retention policy
- [ ] Document deployment, rollback, troubleshooting procedures, and incident response guide
- [ ] Set up CI/CD pipeline (GitHub Actions/Jenkins) with build, test, and deployment automation
- [ ] Review security configs, set up secrets management, network policies, WAF rules, and access controls
- [ ] Optimize JVM settings, connection pools, timeouts, and query performance
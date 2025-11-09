# Authentication & Authorization Service - Implementation Plan
## Spring Boot 3.x + Java 25 | DDD Architecture

---

## Table of Contents
1. [System Architecture Overview](#1-system-architecture-overview)
2. [Domain Model Design (DDD)](#2-domain-model-design-ddd)
3. [Database Schema Design](#3-database-schema-design)
4. [REST API Design](#4-rest-api-design)
5. [OIDC Provider Configuration](#5-oidc-provider-configuration)
6. [Authorization Flow](#6-authorization-flow)
7. [Implementation Phases](#7-implementation-phases)

---

## 1. System Architecture Overview

### 1.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     API Gateway / BFF                        │
│                  (PEP - Coarse-grained)                      │
└──────────────────────┬──────────────────────────────────────┘
                       │
           ┌───────────┴───────────┐
           │                       │
┌──────────▼──────────┐  ┌────────▼─────────────────────────┐
│  Auth Service       │  │   Resource Services              │
│  (AuthN)            │  │   (Protected APIs)               │
│  - OIDC Integration │  │   - Service Mesh Sidecars (PEP)  │
│  - User Management  │  │   - Fine-grained enforcement     │
│  - Token Issuance   │  │                                  │
└─────────┬───────────┘  └────────┬─────────────────────────┘
          │                       │
          │    ┌──────────────────┘
          │    │
┌─────────▼────▼──────────┐
│  Authorization Service   │
│  (PDP - Policy Engine)   │
│  - Policy Evaluation     │
│  - Role/Permission Mgmt  │
│  - ABAC/RBAC/ReBAC      │
└──────────┬───────────────┘
           │
┌──────────▼───────────────┐
│  Policy Information Point │
│  (PIP)                    │
│  - User Attributes        │
│  - Resource Metadata      │
│  - External Context       │
└──────────────────────────┘
```

### 1.2 Technology Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 22
- **Security**: Spring Security 6.x
- **OIDC**: Spring Security OAuth2 Client
- **Database**: PostgreSQL (recommended for ACID compliance)
- **Cache**: Redis (for tokens and sessions)
- **API Documentation**: OpenAPI 3.0 (SpringDoc)
- **Validation**: Jakarta Validation API
- **Audit**: Spring Data Envers / Custom Audit Framework

---

## 2. Domain Model Design (DDD)

### 2.1 Bounded Contexts

```
1. Identity Context
   - User Management
   - Credential Management
   - External Identity Federation

2. Authorization Context
   - Role Management
   - Permission Management
   - Policy Evaluation

3. Configuration Context
   - OIDC Provider Configuration
   - Attribute Mapping
   - System Settings

4. Governance Context
   - Audit Logging
   - Access Certification
   - Compliance Reporting
```

### 2.2 Core Domain Entities & Aggregates

#### Identity Context

**User Aggregate Root**
```
User (Aggregate Root)
├── userId: UUID (immutable global identifier)
├── username: String (unique)
├── email: String
├── status: UserStatus (ACTIVE, SUSPENDED, LOCKED)
├── metadata: Map<String, Object>
├── createdAt: Instant
├── lastModifiedAt: Instant
└── Entities/Value Objects:
    ├── UserProfile (Entity)
    │   ├── profileId: UUID
    │   ├── firstName: String
    │   ├── lastName: String
    │   ├── displayName: String
    │   └── attributes: Map<String, Object>
    │
    ├── FederatedIdentity (Entity)
    │   ├── federatedIdentityId: UUID
    │   ├── providerId: String (reference to OIDCProvider)
    │   ├── subjectId: String (immutable external IdP subject)
    │   ├── issuer: String
    │   ├── linkedAt: Instant
    │   └── lastSyncedAt: Instant
    │
    └── LocalCredential (Entity - optional for local auth)
        ├── credentialId: UUID
        ├── passwordHash: String
        ├── lastPasswordChange: Instant
        └── requiresReset: boolean
```

**Value Objects**
- `UserStatus`: ACTIVE, INACTIVE, SUSPENDED, LOCKED, PENDING_VERIFICATION
- `Email`: Validated email address
- `Username`: Validated username with constraints

#### Authorization Context

**Role Aggregate Root**
```
Role (Aggregate Root)
├── roleId: UUID
├── roleName: String (unique, e.g., "platform:administrator")
├── displayName: String
├── description: String
├── roleType: RoleType (SYSTEM, CUSTOM, FEDERATED)
├── permissions: Set<Permission>
├── parentRoles: Set<Role> (for hierarchical RBAC)
├── metadata: Map<String, Object>
├── createdAt: Instant
└── version: Long (for optimistic locking)
```

**Permission Entity**
```
Permission (Entity)
├── permissionId: UUID
├── resource: String (e.g., "document", "invoice")
├── action: String (e.g., "read", "write", "delete")
├── scope: String (e.g., "own", "department", "global")
├── conditions: PolicyConditions (ABAC rules)
└── description: String
```

**UserRoleAssignment Aggregate**
```
UserRoleAssignment (Aggregate Root)
├── assignmentId: UUID
├── userId: UUID (reference)
├── roleId: UUID (reference)
├── scope: AssignmentScope (GLOBAL, TENANT, RESOURCE)
├── scopeContext: String (e.g., tenantId, resourceId)
├── effectiveFrom: Instant
├── effectiveUntil: Instant (nullable)
├── assignedBy: UUID (admin userId)
├── assignedAt: Instant
└── status: AssignmentStatus (ACTIVE, REVOKED, EXPIRED)
```

**Policy Aggregate (ABAC/PBAC)**
```
Policy (Aggregate Root)
├── policyId: UUID
├── policyName: String
├── policyType: PolicyType (RBAC, ABAC, REBAC)
├── effect: Effect (PERMIT, DENY)
├── subjects: SubjectMatcher (user, role, group conditions)
├── resources: ResourceMatcher (resource type, attributes)
├── actions: Set<String>
├── conditions: PolicyConditions (attribute expressions)
├── priority: Integer
├── enabled: boolean
└── version: Integer
```

#### Configuration Context

**OIDCProviderConfig Aggregate Root**
```
OIDCProviderConfig (Aggregate Root)
├── providerId: UUID
├── providerName: String (unique, e.g., "azure-ad", "okta-corp")
├── providerType: ProviderType (OIDC, SAML)
├── enabled: boolean
├── displayName: String
├── configuration: OIDCConfiguration
│   ├── issuerUri: String
│   ├── clientId: String
│   ├── clientSecret: String (encrypted)
│   ├── authorizationUri: String
│   ├── tokenUri: String
│   ├── userInfoUri: String
│   ├── jwksUri: String
│   ├── scopes: Set<String>
│   └── additionalParameters: Map<String, String>
├── attributeMapping: AttributeMappingConfig
│   ├── subjectAttribute: String (e.g., "sub")
│   ├── emailAttribute: String (e.g., "email")
│   ├── nameAttribute: String (e.g., "name")
│   ├── groupsAttribute: String (e.g., "groups")
│   └── customMappings: List<AttributeMap>
│       ├── externalAttribute: String
│       ├── internalAttribute: String
│       └── transformationRule: String (expression)
├── roleMapping: RoleMappingConfig
│   ├── mappingStrategy: MappingStrategy (EXPLICIT, PATTERN, SCRIPT)
│   └── mappingRules: List<RoleMappingRule>
│       ├── externalGroup: String (regex pattern)
│       ├── internalRole: String
│       └── conditions: Map<String, Object>
├── jitProvisioning: JITProvisioningConfig
│   ├── enabled: boolean
│   ├── createUsers: boolean
│   ├── updateUsers: boolean
│   ├── syncGroups: boolean
│   └── defaultRoles: Set<String>
└── metadata: Map<String, Object>
```

#### Governance Context

**AuditLog (Immutable Event)**
```
AuditLog
├── auditId: UUID
├── timestamp: Instant
├── eventType: AuditEventType
├── actorId: UUID (who performed the action)
├── actorType: ActorType (USER, SYSTEM, SERVICE)
├── subjectId: UUID (target user/resource)
├── resource: String
├── action: String
├── decision: Decision (PERMIT, DENY, ERROR)
├── policyVersion: String
├── context: Map<String, Object>
├── ipAddress: String
├── userAgent: String
└── correlationId: String (for PDP/PEP correlation)
```

### 2.3 Domain Services

```
Identity Domain Services:
- UserProvisioningService: Handles JIT provisioning logic
- IdentityFederationService: Links external identities to internal users
- CredentialManagementService: Password policies, credential validation

Authorization Domain Services:
- PolicyEvaluationService: Core PDP logic
- RoleHierarchyService: Resolves inherited permissions
- PermissionAggregationService: Computes effective permissions

Configuration Domain Services:
- AttributeTransformationService: Applies transformation rules
- RoleMappingService: Maps external groups to internal roles
```

### 2.4 Repository Interfaces (DDD)

```
// Identity Context
UserRepository
FederatedIdentityRepository
CredentialRepository

// Authorization Context
RoleRepository
PermissionRepository
UserRoleAssignmentRepository
PolicyRepository

// Configuration Context
OIDCProviderConfigRepository
AttributeMappingRepository
RoleMappingRepository

// Governance Context
AuditLogRepository
AccessCertificationRepository
```

---

## 3. Database Schema Design

### 3.1 Core Tables

```sql
-- ============================================
-- IDENTITY SCHEMA
-- ============================================

CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);

CREATE TABLE user_profiles (
    profile_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    display_name VARCHAR(255),
    attributes JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE(user_id)
);

CREATE TABLE federated_identities (
    federated_identity_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    provider_id UUID NOT NULL,
    subject_id VARCHAR(255) NOT NULL,
    issuer VARCHAR(500) NOT NULL,
    linked_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_synced_at TIMESTAMP WITH TIME ZONE,
    metadata JSONB,
    UNIQUE(provider_id, subject_id)
);

CREATE INDEX idx_federated_identities_user ON federated_identities(user_id);
CREATE INDEX idx_federated_identities_provider ON federated_identities(provider_id);

-- ============================================
-- AUTHORIZATION SCHEMA
-- ============================================

CREATE TABLE roles (
    role_id UUID PRIMARY KEY,
    role_name VARCHAR(255) UNIQUE NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    role_type VARCHAR(50) NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_roles_type ON roles(role_type);
CREATE INDEX idx_roles_name ON roles(role_name);

CREATE TABLE permissions (
    permission_id UUID PRIMARY KEY,
    resource VARCHAR(255) NOT NULL,
    action VARCHAR(100) NOT NULL,
    scope VARCHAR(100),
    conditions JSONB,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    UNIQUE(resource, action, scope)
);

CREATE INDEX idx_permissions_resource ON permissions(resource);

CREATE TABLE role_permissions (
    role_id UUID NOT NULL REFERENCES roles(role_id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(permission_id) ON DELETE CASCADE,
    granted_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE role_hierarchy (
    parent_role_id UUID NOT NULL REFERENCES roles(role_id) ON DELETE CASCADE,
    child_role_id UUID NOT NULL REFERENCES roles(role_id) ON DELETE CASCADE,
    PRIMARY KEY (parent_role_id, child_role_id)
);

CREATE TABLE user_role_assignments (
    assignment_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(role_id) ON DELETE CASCADE,
    scope VARCHAR(50) NOT NULL,
    scope_context VARCHAR(255),
    effective_from TIMESTAMP WITH TIME ZONE NOT NULL,
    effective_until TIMESTAMP WITH TIME ZONE,
    assigned_by UUID NOT NULL,
    assigned_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_user_role_assignments_user ON user_role_assignments(user_id);
CREATE INDEX idx_user_role_assignments_role ON user_role_assignments(role_id);
CREATE INDEX idx_user_role_assignments_status ON user_role_assignments(status);
CREATE INDEX idx_user_role_assignments_effective ON user_role_assignments(effective_from, effective_until);

CREATE TABLE policies (
    policy_id UUID PRIMARY KEY,
    policy_name VARCHAR(255) UNIQUE NOT NULL,
    policy_type VARCHAR(50) NOT NULL,
    effect VARCHAR(20) NOT NULL,
    subjects JSONB NOT NULL,
    resources JSONB NOT NULL,
    actions JSONB NOT NULL,
    conditions JSONB,
    priority INTEGER NOT NULL DEFAULT 0,
    enabled BOOLEAN NOT NULL DEFAULT true,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_policies_type ON policies(policy_type);
CREATE INDEX idx_policies_enabled ON policies(enabled);

-- ============================================
-- CONFIGURATION SCHEMA
-- ============================================

CREATE TABLE oidc_provider_configs (
    provider_id UUID PRIMARY KEY,
    provider_name VARCHAR(255) UNIQUE NOT NULL,
    provider_type VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    display_name VARCHAR(255) NOT NULL,
    configuration JSONB NOT NULL,
    attribute_mapping JSONB NOT NULL,
    role_mapping JSONB NOT NULL,
    jit_provisioning JSONB NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_oidc_providers_name ON oidc_provider_configs(provider_name);
CREATE INDEX idx_oidc_providers_enabled ON oidc_provider_configs(enabled);

-- ============================================
-- GOVERNANCE SCHEMA
-- ============================================

CREATE TABLE audit_logs (
    audit_id UUID PRIMARY KEY,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    actor_id UUID,
    actor_type VARCHAR(50) NOT NULL,
    subject_id UUID,
    resource VARCHAR(255),
    action VARCHAR(100),
    decision VARCHAR(50),
    policy_version VARCHAR(100),
    context JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    correlation_id VARCHAR(100)
);

CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp DESC);
CREATE INDEX idx_audit_logs_actor ON audit_logs(actor_id);
CREATE INDEX idx_audit_logs_subject ON audit_logs(subject_id);
CREATE INDEX idx_audit_logs_event_type ON audit_logs(event_type);
CREATE INDEX idx_audit_logs_correlation ON audit_logs(correlation_id);

-- Partitioning strategy for audit_logs (by month)
CREATE TABLE audit_logs_y2025m01 PARTITION OF audit_logs
    FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');

CREATE TABLE access_certifications (
    certification_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id),
    role_id UUID NOT NULL REFERENCES roles(role_id),
    certification_date TIMESTAMP WITH TIME ZONE NOT NULL,
    certified_by UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    next_review_date TIMESTAMP WITH TIME ZONE,
    notes TEXT
);

CREATE INDEX idx_access_certifications_user ON access_certifications(user_id);
CREATE INDEX idx_access_certifications_status ON access_certifications(status);
CREATE INDEX idx_access_certifications_next_review ON access_certifications(next_review_date);
```

---

## 4. REST API Design

### 4.1 API Structure

```
/api/v1/
├── /auth                    # Authentication endpoints
├── /users                   # User management
├── /roles                   # Role management
├── /permissions            # Permission management
├── /policies               # Policy management
├── /oidc-providers         # OIDC provider configuration
├── /access-control         # Authorization checks (PDP)
├── /assignments            # Role assignments
├── /audit                  # Audit and compliance
└── /admin                  # Administrative operations
```

### 4.2 Authentication APIs

#### POST /api/v1/auth/oidc/initiate
**Description**: Initiate OIDC authentication flow
**Request DTO**:
```json
{
  "providerId": "string (UUID)",
  "redirectUri": "string (URL)",
  "state": "string (optional)",
  "nonce": "string (optional)"
}
```
**Response DTO**:
```json
{
  "authorizationUrl": "string (URL)",
  "state": "string",
  "codeVerifier": "string (for PKCE)"
}
```

#### POST /api/v1/auth/oidc/callback
**Description**: Handle OIDC callback and perform JIT provisioning
**Request DTO**:
```json
{
  "providerId": "string (UUID)",
  "code": "string",
  "state": "string",
  "codeVerifier": "string (for PKCE)"
}
```
**Response DTO**:
```json
{
  "accessToken": "string (JWT)",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "expiresIn": "integer (seconds)",
  "user": {
    "userId": "string (UUID)",
    "username": "string",
    "email": "string",
    "profile": {
      "firstName": "string",
      "lastName": "string",
      "displayName": "string"
    },
    "roles": ["string"],
    "permissions": ["string"],
    "federatedIdentity": {
      "providerId": "string (UUID)",
      "providerName": "string",
      "subjectId": "string"
    }
  }
}
```

#### POST /api/v1/auth/token/refresh
**Description**: Refresh access token
**Request DTO**:
```json
{
  "refreshToken": "string"
}
```
**Response DTO**:
```json
{
  "accessToken": "string (JWT)",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "expiresIn": "integer (seconds)"
}
```

#### POST /api/v1/auth/logout
**Description**: Logout and invalidate tokens
**Request DTO**:
```json
{
  "refreshToken": "string"
}
```
**Response DTO**:
```json
{
  "success": "boolean",
  "message": "string"
}
```

### 4.3 User Management APIs

#### GET /api/v1/users
**Description**: List users with pagination and filtering
**Query Parameters**:
- `page`: integer (default: 0)
- `size`: integer (default: 20)
- `sort`: string (e.g., "username,asc")
- `status`: string (filter by status)
- `search`: string (search username/email)

**Response DTO**:
```json
{
  "content": [
    {
      "userId": "string (UUID)",
      "username": "string",
      "email": "string",
      "status": "string",
      "profile": {
        "firstName": "string",
        "lastName": "string",
        "displayName": "string",
        "attributes": {}
      },
      "createdAt": "string (ISO 8601)",
      "lastModifiedAt": "string (ISO 8601)"
    }
  ],
  "pageable": {
    "page": "integer",
    "size": "integer",
    "totalElements": "integer",
    "totalPages": "integer"
  }
}
```

#### GET /api/v1/users/{userId}
**Description**: Get user details
**Response DTO**:
```json
{
  "userId": "string (UUID)",
  "username": "string",
  "email": "string",
  "status": "string",
  "profile": {
    "profileId": "string (UUID)",
    "firstName": "string",
    "lastName": "string",
    "displayName": "string",
    "attributes": {}
  },
  "federatedIdentities": [
    {
      "federatedIdentityId": "string (UUID)",
      "providerId": "string (UUID)",
      "providerName": "string",
      "subjectId": "string",
      "issuer": "string",
      "linkedAt": "string (ISO 8601)"
    }
  ],
  "roles": [
    {
      "roleId": "string (UUID)",
      "roleName": "string",
      "displayName": "string",
      "assignedAt": "string (ISO 8601)"
    }
  ],
  "createdAt": "string (ISO 8601)",
  "lastModifiedAt": "string (ISO 8601)"
}
```

#### POST /api/v1/users
**Description**: Create a new user (admin only)
**Request DTO**:
```json
{
  "username": "string (required, unique)",
  "email": "string (required, valid email)",
  "profile": {
    "firstName": "string",
    "lastName": "string",
    "displayName": "string",
    "attributes": {}
  },
  "status": "string (default: ACTIVE)",
  "initialRoles": ["string (roleId or roleName)"]
}
```
**Response DTO**:
```json
{
  "userId": "string (UUID)",
  "username": "string",
  "email": "string",
  "status": "string",
  "createdAt": "string (ISO 8601)"
}
```

#### PATCH /api/v1/users/{userId}
**Description**: Update user details
**Request DTO**:
```json
{
  "email": "string (optional)",
  "status": "string (optional)",
  "profile": {
    "firstName": "string",
    "lastName": "string",
    "displayName": "string",
    "attributes": {}
  }
}
```
**Response DTO**: Same as GET /api/v1/users/{userId}

#### DELETE /api/v1/users/{userId}
**Description**: Soft delete or deactivate user
**Response DTO**:
```json
{
  "success": "boolean",
  "message": "string"
}
```

#### GET /api/v1/users/{userId}/permissions
**Description**: Get effective permissions for a user
**Query Parameters**:
- `resourceType`: string (optional filter)
- `includeInherited`: boolean (default: true)

**Response DTO**:
```json
{
  "userId": "string (UUID)",
  "effectivePermissions": [
    {
      "permissionId": "string (UUID)",
      "resource": "string",
      "action": "string",
      "scope": "string",
      "source": "string (DIRECT, INHERITED)",
      "grantedThrough": {
        "roleId": "string (UUID)",
        "roleName": "string"
      }
    }
  ]
}
```

### 4.4 Role Management APIs

#### GET /api/v1/roles
**Description**: List all roles
**Query Parameters**:
- `page`: integer
- `size`: integer
- `roleType`: string (SYSTEM, CUSTOM, FEDERATED)
- `search`: string

**Response DTO**:
```json
{
  "content": [
    {
      "roleId": "string (UUID)",
      "roleName": "string",
      "displayName": "string",
      "description": "string",
      "roleType": "string",
      "permissionCount": "integer",
      "userCount": "integer",
      "createdAt": "string (ISO 8601)"
    }
  ],
  "pageable": {}
}
```

#### GET /api/v1/roles/{roleId}
**Description**: Get role details with permissions
**Response DTO**:
```json
{
  "roleId": "string (UUID)",
  "roleName": "string",
  "displayName": "string",
  "description": "string",
  "roleType": "string",
  "permissions": [
    {
      "permissionId": "string (UUID)",
      "resource": "string",
      "action": "string",
      "scope": "string",
      "description": "string"
    }
  ],
  "parentRoles": [
    {
      "roleId": "string (UUID)",
      "roleName": "string"
    }
  ],
  "metadata": {},
  "createdAt": "string (ISO 8601)",
  "lastModifiedAt": "string (ISO 8601)"
}
```

#### POST /api/v1/roles
**Description**: Create a new role
**Request DTO**:
```json
{
  "roleName": "string (required, unique)",
  "displayName": "string (required)",
  "description": "string",
  "roleType": "string (default: CUSTOM)",
  "permissionIds": ["string (UUID)"],
  "parentRoleIds": ["string (UUID)"],
  "metadata": {}
}
```
**Response DTO**: Same as GET /api/v1/roles/{roleId}

#### PUT /api/v1/roles/{roleId}
**Description**: Update role
**Request DTO**: Same as POST /api/v1/roles
**Response DTO**: Same as GET /api/v1/roles/{roleId}

#### DELETE /api/v1/roles/{roleId}
**Description**: Delete role (if not system role and no active assignments)
**Response DTO**:
```json
{
  "success": "boolean",
  "message": "string"
}
```

#### POST /api/v1/roles/{roleId}/permissions
**Description**: Add permissions to role
**Request DTO**:
```json
{
  "permissionIds": ["string (UUID)"]
}
```
**Response DTO**: Updated role details

#### DELETE /api/v1/roles/{roleId}/permissions/{permissionId}
**Description**: Remove permission from role
**Response DTO**:
```json
{
  "success": "boolean",
  "message": "string"
}
```

### 4.5 Permission Management APIs

#### GET /api/v1/permissions
**Description**: List all permissions
**Query Parameters**:
- `resource`: string (filter by resource)
- `action`: string (filter by action)
- `page`: integer
- `size`: integer

**Response DTO**:
```json
{
  "content": [
    {
      "permissionId": "string (UUID)",
      "resource": "string",
      "action": "string",
      "scope": "string",
      "description": "string",
      "conditions": {},
      "createdAt": "string (ISO 8601)"
    }
  ],
  "pageable": {}
}
```

#### POST /api/v1/permissions
**Description**: Create a new permission
**Request DTO**:
```json
{
  "resource": "string (required)",
  "action": "string (required)",
  "scope": "string (default: global)",
  "description": "string",
  "conditions": {
    "attributes": {},
    "expressions": []
  }
}
```
**Response DTO**:
```json
{
  "permissionId": "string (UUID)",
  "resource": "string",
  "action": "string",
  "scope": "string",
  "description": "string",
  "createdAt": "string (ISO 8601)"
}
```

#### DELETE /api/v1/permissions/{permissionId}
**Description**: Delete permission
**Response DTO**:
```json
{
  "success": "boolean",
  "message": "string"
}
```

### 4.6 Role Assignment APIs

#### GET /api/v1/assignments
**Description**: List role assignments
**Query Parameters**:
- `userId`: string (UUID, filter by user)
- `roleId`: string (UUID, filter by role)
- `status`: string (filter by status)
- `page`: integer
- `size`: integer

**Response DTO**:
```json
{
  "content": [
    {
      "assignmentId": "string (UUID)",
      "userId": "string (UUID)",
      "username": "string",
      "roleId": "string (UUID)",
      "roleName": "string",
      "scope": "string",
      "scopeContext": "string",
      "effectiveFrom": "string (ISO 8601)",
      "effectiveUntil": "string (ISO 8601, nullable)",
      "status": "string",
      "assignedBy": "string (UUID)",
      "assignedAt": "string (ISO 8601)"
    }
  ],
  "pageable": {}
}
```

#### POST /api/v1/assignments
**Description**: Assign role to user
**Request DTO**:
```json
{
  "userId": "string (UUID, required)",
  "roleId": "string (UUID, required)",
  "scope": "string (default: GLOBAL)",
  "scopeContext": "string (optional, e.g., tenantId)",
  "effectiveFrom": "string (ISO 8601, default: now)",
  "effectiveUntil": "string (ISO 8601, nullable)",
  "reason": "string (audit trail)"
}
```
**Response DTO**:
```json
{
  "assignmentId": "string (UUID)",
  "userId": "string (UUID)",
  "roleId": "string (UUID)",
  "scope": "string",
  "scopeContext": "string",
  "effectiveFrom": "string (ISO 8601)",
  "effectiveUntil": "string (ISO 8601)",
  "status": "ACTIVE",
  "assignedBy": "string (UUID)",
  "assignedAt": "string (ISO 8601)"
}
```

#### DELETE /api/v1/assignments/{assignmentId}
**Description**: Revoke role assignment
**Request DTO**:
```json
{
  "reason": "string (audit trail, required)"
}
```
**Response DTO**:
```json
{
  "success": "boolean",
  "message": "string",
  "revokedAt": "string (ISO 8601)"
}
```

#### POST /api/v1/assignments/bulk
**Description**: Bulk assign roles
**Request DTO**:
```json
{
  "assignments": [
    {
      "userId": "string (UUID)",
      "roleId": "string (UUID)",
      "scope": "string",
      "scopeContext": "string"
    }
  ],
  "reason": "string"
}
```
**Response DTO**:
```json
{
  "successful": "integer",
  "failed": "integer",
  "results": [
    {
      "userId": "string (UUID)",
      "roleId": "string (UUID)",
      "success": "boolean",
      "assignmentId": "string (UUID, if successful)",
      "error": "string (if failed)"
    }
  ]
}
```

### 4.7 OIDC Provider Configuration APIs

#### GET /api/v1/oidc-providers
**Description**: List configured OIDC providers
**Query Parameters**:
- `enabled`: boolean (filter)
- `page`: integer
- `size`: integer

**Response DTO**:
```json
{
  "content": [
    {
      "providerId": "string (UUID)",
      "providerName": "string",
      "displayName": "string",
      "providerType": "string (OIDC, SAML)",
      "enabled": "boolean",
      "userCount": "integer",
      "createdAt": "string (ISO 8601)",
      "lastModifiedAt": "string (ISO 8601)"
    }
  ],
  "pageable": {}
}
```

#### GET /api/v1/oidc-providers/{providerId}
**Description**: Get provider configuration details
**Response DTO**:
```json
{
  "providerId": "string (UUID)",
  "providerName": "string",
  "displayName": "string",
  "providerType": "string",
  "enabled": "boolean",
  "configuration": {
    "issuerUri": "string",
    "clientId": "string",
    "authorizationUri": "string",
    "tokenUri": "string",
    "userInfoUri": "string",
    "jwksUri": "string",
    "scopes": ["openid", "profile", "email"],
    "additionalParameters": {}
  },
  "attributeMapping": {
    "subjectAttribute": "sub",
    "emailAttribute": "email",
    "nameAttribute": "name",
    "groupsAttribute": "groups",
    "customMappings": [
      {
        "externalAttribute": "string",
        "internalAttribute": "string",
        "transformationRule": "string (expression)"
      }
    ]
  },
  "roleMapping": {
    "mappingStrategy": "string (EXPLICIT, PATTERN, SCRIPT)",
    "mappingRules": [
      {
        "externalGroup": "string (regex pattern)",
        "internalRole": "string",
        "conditions": {}
      }
    ]
  },
  "jitProvisioning": {
    "enabled": "boolean",
    "createUsers": "boolean",
    "updateUsers": "boolean",
    "syncGroups": "boolean",
    "defaultRoles": ["string"]
  },
  "metadata": {},
  "createdAt": "string (ISO 8601)",
  "lastModifiedAt": "string (ISO 8601)"
}
```

#### POST /api/v1/oidc-providers
**Description**: Create new OIDC provider configuration
**Request DTO**:
```json
{
  "providerName": "string (required, unique)",
  "displayName": "string (required)",
  "providerType": "string (default: OIDC)",
  "enabled": "boolean (default: true)",
  "configuration": {
    "issuerUri": "string (required)",
    "clientId": "string (required)",
    "clientSecret": "string (required)",
    "authorizationUri": "string",
    "tokenUri": "string",
    "userInfoUri": "string",
    "jwksUri": "string",
    "scopes": ["openid", "profile", "email"],
    "additionalParameters": {}
  },
  "attributeMapping": {
    "subjectAttribute": "sub",
    "emailAttribute": "email",
    "nameAttribute": "name",
    "groupsAttribute": "groups",
    "customMappings": []
  },
  "roleMapping": {
    "mappingStrategy": "EXPLICIT",
    "mappingRules": []
  },
  "jitProvisioning": {
    "enabled": true,
    "createUsers": true,
    "updateUsers": true,
    "syncGroups": true,
    "defaultRoles": []
  }
}
```
**Response DTO**: Same as GET /api/v1/oidc-providers/{providerId}

#### PUT /api/v1/oidc-providers/{providerId}
**Description**: Update provider configuration
**Request DTO**: Same as POST /api/v1/oidc-providers
**Response DTO**: Same as GET /api/v1/oidc-providers/{providerId}

#### DELETE /api/v1/oidc-providers/{providerId}
**Description**: Delete provider configuration
**Response DTO**:
```json
{
  "success": "boolean",
  "message": "string",
  "affectedUsers": "integer"
}
```

#### POST /api/v1/oidc-providers/{providerId}/test
**Description**: Test OIDC provider configuration
**Response DTO**:
```json
{
  "success": "boolean",
  "discoveryEndpoint": "string (URL)",
  "jwksValid": "boolean",
  "metadata": {},
  "errors": ["string"]
}
```

### 4.8 Authorization Check APIs (PDP)

#### POST /api/v1/access-control/check
**Description**: Check if user has permission (PDP evaluation)
**Request DTO**:
```json
{
  "userId": "string (UUID, required)",
  "resource": "string (required)",
  "action": "string (required)",
  "context": {
    "tenantId": "string",
    "resourceId": "string",
    "ipAddress": "string",
    "timestamp": "string (ISO 8601)",
    "customAttributes": {}
  }
}
```
**Response DTO**:
```json
{
  "decision": "string (PERMIT, DENY)",
  "reason": "string",
  "matchedPolicies": [
    {
      "policyId": "string (UUID)",
      "policyName": "string",
      "effect": "string"
    }
  ],
  "obligations": [],
  "evaluationTime": "integer (milliseconds)"
}
```

#### POST /api/v1/access-control/batch-check
**Description**: Batch permission check
**Request DTO**:
```json
{
  "userId": "string (UUID, required)",
  "checks": [
    {
      "resource": "string",
      "action": "string",
      "context": {}
    }
  ]
}
```
**Response DTO**:
```json
{
  "results": [
    {
      "resource": "string",
      "action": "string",
      "decision": "string (PERMIT, DENY)",
      "reason": "string"
    }
  ]
}
```

### 4.9 Policy Management APIs

#### GET /api/v1/policies
**Description**: List policies
**Query Parameters**:
- `policyType`: string
- `enabled`: boolean
- `page`: integer
- `size`: integer

**Response DTO**:
```json
{
  "content": [
    {
      "policyId": "string (UUID)",
      "policyName": "string",
      "policyType": "string",
      "effect": "string (PERMIT, DENY)",
      "enabled": "boolean",
      "priority": "integer",
      "version": "integer",
      "createdAt": "string (ISO 8601)"
    }
  ],
  "pageable": {}
}
```

#### GET /api/v1/policies/{policyId}
**Description**: Get policy details
**Response DTO**:
```json
{
  "policyId": "string (UUID)",
  "policyName": "string",
  "policyType": "string",
  "effect": "string",
  "subjects": {
    "userIds": ["string (UUID)"],
    "roleNames": ["string"],
    "attributes": {}
  },
  "resources": {
    "resourceTypes": ["string"],
    "resourceIds": ["string"],
    "attributes": {}
  },
  "actions": ["string"],
  "conditions": {
    "expressions": [
      {
        "attribute": "string",
        "operator": "string",
        "value": "any"
      }
    ]
  },
  "priority": "integer",
  "enabled": "boolean",
  "version": "integer",
  "createdAt": "string (ISO 8601)",
  "lastModifiedAt": "string (ISO 8601)"
}
```

#### POST /api/v1/policies
**Description**: Create new policy
**Request DTO**: Same structure as GET response
**Response DTO**: Created policy details

#### PUT /api/v1/policies/{policyId}
**Description**: Update policy (creates new version)
**Request DTO**: Policy update payload
**Response DTO**: Updated policy details

#### DELETE /api/v1/policies/{policyId}
**Description**: Delete or disable policy
**Response DTO**:
```json
{
  "success": "boolean",
  "message": "string"
}
```

### 4.10 Audit and Compliance APIs

#### GET /api/v1/audit/logs
**Description**: Query audit logs
**Query Parameters**:
- `from`: string (ISO 8601)
- `to`: string (ISO 8601)
- `actorId`: string (UUID)
- `subjectId`: string (UUID)
- `eventType`: string
- `decision`: string
- `resource`: string
- `correlationId`: string
- `page`: integer
- `size`: integer

**Response DTO**:
```json
{
  "content": [
    {
      "auditId": "string (UUID)",
      "timestamp": "string (ISO 8601)",
      "eventType": "string",
      "actorId": "string (UUID)",
      "actorType": "string",
      "subjectId": "string (UUID)",
      "resource": "string",
      "action": "string",
      "decision": "string",
      "policyVersion": "string",
      "context": {},
      "ipAddress": "string",
      "userAgent": "string",
      "correlationId": "string"
    }
  ],
  "pageable": {}
}
```

#### GET /api/v1/audit/user-activity/{userId}
**Description**: Get user activity audit trail
**Query Parameters**:
- `from`: string (ISO 8601)
- `to`: string (ISO 8601)
- `page`: integer
- `size`: integer

**Response DTO**: Same as audit logs

#### GET /api/v1/audit/compliance-report
**Description**: Generate compliance report
**Query Parameters**:
- `reportType`: string (SOX, GDPR, HIPAA)
- `from`: string (ISO 8601)
- `to`: string (ISO 8601)

**Response DTO**:
```json
{
  "reportId": "string (UUID)",
  "reportType": "string",
  "period": {
    "from": "string (ISO 8601)",
    "to": "string (ISO 8601)"
  },
  "summary": {
    "totalUsers": "integer",
    "totalRoleChanges": "integer",
    "failedAccessAttempts": "integer",
    "policyViolations": "integer"
  },
  "findings": [
    {
      "severity": "string (HIGH, MEDIUM, LOW)",
      "category": "string",
      "description": "string",
      "affectedUsers": ["string (UUID)"],
      "recommendation": "string"
    }
  ],
  "generatedAt": "string (ISO 8601)"
}
```

#### POST /api/v1/audit/access-certifications
**Description**: Initiate access certification review
**Request DTO**:
```json
{
  "reviewType": "string (PERIODIC, AD_HOC)",
  "scope": {
    "userIds": ["string (UUID)"],
    "roleIds": ["string (UUID)"]
  },
  "dueDate": "string (ISO 8601)",
  "reviewers": ["string (UUID)"]
}
```
**Response DTO**:
```json
{
  "certificationId": "string (UUID)",
  "status": "string (PENDING)",
  "itemCount": "integer",
  "dueDate": "string (ISO 8601)",
  "createdAt": "string (ISO 8601)"
}
```

---

## 5. OIDC Provider Configuration

### 5.1 Configuration Flow

```
1. Admin creates OIDC Provider Config
   ↓
2. Configure OAuth2 parameters
   - Client ID/Secret
   - Endpoints (authorization, token, userinfo, jwks)
   - Scopes
   ↓
3. Define Attribute Mapping
   - Map external claims to internal attributes
   - Set transformation rules
   ↓
4. Configure Role Mapping
   - Map external groups to internal roles
   - Define mapping strategy (explicit, pattern, script)
   ↓
5. Enable JIT Provisioning
   - Auto-create users
   - Auto-update attributes
   - Sync group memberships
   ↓
6. Test Configuration
   ↓
7. Enable Provider
```

### 5.2 JIT Provisioning Logic

```java
// Pseudo-code for JIT Provisioning Service

@Service
public class JITProvisioningService {
    
    public User provisionUser(OIDCProviderConfig provider, 
                             OIDCUserInfo userInfo,
                             Set<String> externalGroups) {
        
        // 1. Extract immutable subject identifier
        String subjectId = extractSubjectId(userInfo, provider);
        
        // 2. Check if federated identity exists
        Optional<FederatedIdentity> existingIdentity = 
            federatedIdentityRepository
                .findByProviderIdAndSubjectId(provider.getId(), subjectId);
        
        User user;
        
        if (existingIdentity.isPresent()) {
            // 3a. Update existing user
            user = existingIdentity.get().getUser();
            
            if (provider.getJitProvisioning().isUpdateUsers()) {
                updateUserAttributes(user, userInfo, provider);
            }
        } else {
            // 3b. Create new user
            if (!provider.getJitProvisioning().isCreateUsers()) {
                throw new UserNotProvisionedException();
            }
            
            user = createNewUser(userInfo, provider);
            
            // Link federated identity
            FederatedIdentity federatedIdentity = new FederatedIdentity(
                provider.getId(),
                subjectId,
                userInfo.getIssuer()
            );
            federatedIdentity.linkToUser(user);
            federatedIdentityRepository.save(federatedIdentity);
        }
        
        // 4. Sync roles based on group mappings
        if (provider.getJitProvisioning().isSyncGroups()) {
            syncUserRoles(user, externalGroups, provider);
        }
        
        // 5. Apply default roles if configured
        applyDefaultRoles(user, provider);
        
        // 6. Audit the provisioning event
        auditService.logJITProvisioning(user, provider);
        
        return user;
    }
    
    private void syncUserRoles(User user, 
                              Set<String> externalGroups,
                              OIDCProviderConfig provider) {
        
        Set<Role> mappedRoles = new HashSet<>();
        
        for (String externalGroup : externalGroups) {
            // Apply mapping rules
            List<Role> roles = roleMappingService
                .mapExternalGroupToRoles(externalGroup, provider);
            mappedRoles.addAll(roles);
        }
        
        // Remove roles not in mapped set (federated roles only)
        // Add new roles
        userRoleService.synchronizeRoles(user, mappedRoles, 
                                        AssignmentSource.FEDERATED);
    }
}
```

### 5.3 Attribute Transformation Examples

**Mapping Configuration**:
```json
{
  "customMappings": [
    {
      "externalAttribute": "department",
      "internalAttribute": "profile.department",
      "transformationRule": "value.toUpperCase()"
    },
    {
      "externalAttribute": "employee_number",
      "internalAttribute": "profile.employeeId",
      "transformationRule": "value.replaceAll('-', '')"
    },
    {
      "externalAttribute": "roles",
      "internalAttribute": "profile.jobTitle",
      "transformationRule": "value.split(',')[0].trim()"
    }
  ]
}
```

**Role Mapping Rules**:
```json
{
  "mappingStrategy": "PATTERN",
  "mappingRules": [
    {
      "externalGroup": "^Federated-Admin-.*$",
      "internalRole": "platform:administrator",
      "conditions": {}
    },
    {
      "externalGroup": "^App-([A-Za-z]+)-Editor$",
      "internalRole": "app:$1:editor",
      "conditions": {
        "captureGroup": 1
      }
    },
    {
      "externalGroup": "^Department-Finance-.*$",
      "internalRole": "department:finance:member",
      "conditions": {}
    }
  ]
}
```

---

## 6. Authorization Flow

### 6.1 User Access Flow (End-to-End)

```
┌─────────────────────────────────────────────────────────────────┐
│  PHASE 1: Authentication (AuthN)                                 │
└─────────────────────────────────────────────────────────────────┘

1. User initiates login via OIDC provider
   ↓
2. Frontend calls: POST /api/v1/auth/oidc/initiate
   {providerId: "azure-ad-uuid"}
   ↓
3. Auth Service returns authorization URL
   ↓
4. User redirected to external IdP (Azure AD, Okta, etc.)
   ↓
5. User authenticates with IdP
   ↓
6. IdP redirects back with authorization code
   ↓
7. Frontend calls: POST /api/v1/auth/oidc/callback
   {providerId, code, state}
   ↓
8. Auth Service:
   a. Exchanges code for tokens with IdP
   b. Validates ID token (signature, claims, nonce)
   c. Fetches user info from IdP
   d. Executes JIT Provisioning logic
      - Create/Update User
      - Link FederatedIdentity
      - Sync Roles based on group mappings
   e. Generates internal JWT access token
   f. Generates refresh token
   ↓
9. Returns JWT + User Info to frontend
   {accessToken, refreshToken, user: {...}}

┌─────────────────────────────────────────────────────────────────┐
│  PHASE 2: Authorization (AuthZ)                                  │
└─────────────────────────────────────────────────────────────────┘

10. User attempts to access protected resource
    Request: GET /api/v1/documents/123
    Header: Authorization: Bearer {JWT}
    ↓
11. API Gateway (PEP - Coarse-grained)
    a. Validates JWT signature and expiration
    b. Extracts userId from token
    c. Checks coarse-grained permissions (e.g., authenticated user)
    ↓
12. Request forwarded to Resource Service
    ↓
13. Service Mesh Sidecar (PEP - Fine-grained)
    a. Intercepts request
    b. Calls Authorization Service (PDP)
       POST /api/v1/access-control/check
       {
         userId: "user-uuid",
         resource: "document",
         action: "read",
         context: {
           resourceId: "123",
           tenantId: "tenant-1"
         }
       }
    ↓
14. Authorization Service (PDP)
    a. Loads user's effective permissions from cache/DB
    b. Queries Policy Information Point (PIP) for additional context
       - Resource ownership
       - Department membership
       - Time-based constraints
    c. Evaluates policies (RBAC + ABAC + ReBAC)
       - Check role-based permissions
       - Evaluate attribute conditions
       - Check relationship graph (if ReBAC)
    d. Returns decision: {decision: "PERMIT", reason: "..."}
    e. Logs decision to audit log (PDP log)
    ↓
15. Service Mesh Sidecar (PEP)
    a. Receives decision from PDP
    b. If PERMIT: forwards request to service
    c. If DENY: returns 403 Forbidden
    d. Logs enforcement action (PEP log)
    ↓
16. Resource Service processes request
    ↓
17. Response returned to user
```

### 6.2 Token Structure

**Internal JWT Access Token**:
```json
{
  "header": {
    "alg": "RS256",
    "typ": "JWT",
    "kid": "key-id-2025"
  },
  "payload": {
    "iss": "https://auth.yourcompany.com",
    "sub": "user-uuid",
    "aud": ["api://yourcompany"],
    "exp": 1735689600,
    "iat": 1735686000,
    "jti": "token-uuid",
    "scope": "openid profile email",
    "userId": "user-uuid",
    "username": "john.doe",
    "email": "john.doe@company.com",
    "roles": ["platform:user", "app:crm:editor"],
    "tenantId": "tenant-uuid",
    "sessionId": "session-uuid",
    "federatedIdentity": {
      "provider": "azure-ad",
      "subjectId": "external-sub-id"
    }
  }
}
```

### 6.3 Permission Evaluation Algorithm

```java
// Pseudo-code for PDP evaluation

@Service
public class PolicyEvaluationService {
    
    public AuthorizationDecision evaluate(AuthorizationRequest request) {
        
        // 1. Load user context
        User user = userRepository.findById(request.getUserId());
        
        // 2. Get effective permissions (cached)
        Set<Permission> effectivePermissions = 
            getEffectivePermissions(user);
        
        // 3. Check RBAC permissions first (fast path)
        for (Permission permission : effectivePermissions) {
            if (matches(permission, request)) {
                if (evaluateConditions(permission, request)) {
                    return AuthorizationDecision.permit(permission);
                }
            }
        }
        
        // 4. Evaluate ABAC policies
        List<Policy> applicablePolicies = 
            policyRepository.findApplicablePolicies(
                request.getResource(), 
                request.getAction()
            );
        
        // Sort by priority
        applicablePolicies.sort(Comparator.comparing(Policy::getPriority));
        
        for (Policy policy : applicablePolicies) {
            if (matchesSubject(policy, user, request) &&
                matchesResource(policy, request) &&
                matchesAction(policy, request) &&
                evaluatePolicyConditions(policy, request)) {
                
                if (policy.getEffect() == Effect.DENY) {
                    // Explicit DENY always wins
                    return AuthorizationDecision.deny(policy);
                } else {
                    return AuthorizationDecision.permit(policy);
                }
            }
        }
        
        // 5. Check ReBAC relationships (if enabled)
        if (isReBAC(request)) {
            boolean hasRelationship = 
                relationshipService.checkRelationship(
                    user.getId(),
                    request.getResourceId(),
                    request.getAction()
                );
            
            if (hasRelationship) {
                return AuthorizationDecision.permitByRelationship();
            }
        }
        
        // 6. Default deny
        return AuthorizationDecision.denyByDefault();
    }
    
    private Set<Permission> getEffectivePermissions(User user) {
        // Check cache first
        String cacheKey = "user:permissions:" + user.getId();
        Set<Permission> cached = redisCache.get(cacheKey);
        
        if (cached != null) {
            return cached;
        }
        
        // Compute effective permissions
        Set<Permission> permissions = new HashSet<>();
        
        // Get direct role assignments
        List<UserRoleAssignment> assignments = 
            userRoleAssignmentRepository
                .findActiveAssignmentsByUserId(user.getId());
        
        for (UserRoleAssignment assignment : assignments) {
            Role role = assignment.getRole();
            permissions.addAll(role.getPermissions());
            
            // Include inherited permissions from parent roles
            permissions.addAll(
                roleHierarchyService.getInheritedPermissions(role)
            );
        }
        
        // Cache for 5 minutes
        redisCache.set(cacheKey, permissions, Duration.ofMinutes(5));
        
        return permissions;
    }
}
```

### 6.4 Caching Strategy

**Cache Hierarchy**:
```
L1: Application Cache (Caffeine)
├── User effective permissions (5 min TTL)
├── Role hierarchy (10 min TTL)
└── Policy metadata (10 min TTL)

L2: Distributed Cache (Redis)
├── User sessions (token TTL)
├── JWT blacklist (until expiry)
├── OIDC provider configs (30 min TTL)
└── Recent authorization decisions (1 min TTL)

L3: Database (PostgreSQL)
└── Source of truth for all data
```

**Cache Invalidation Events**:
- Role assignment/revocation → Invalidate user permissions cache
- Role permission change → Invalidate all users with that role
- Policy update → Invalidate policy cache
- User logout → Invalidate user session and tokens

---

## 7. Implementation Phases

### Phase 1: Foundation (Weeks 1-2)

**Deliverables**:
- [ ] Project setup (Spring Boot 3.x, Java 25, dependencies)
- [ ] Database schema creation
- [ ] Core domain models (DDD structure)
- [ ] Repository interfaces and implementations
- [ ] Basic error handling and validation framework

**Key Components**:
```
src/main/java/com/company/authservice/
├── domain/
│   ├── identity/
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── UserProfile.java
│   │   │   ├── FederatedIdentity.java
│   │   │   └── UserStatus.java (enum)
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── FederatedIdentityRepository.java
│   │   └── service/
│   │       └── UserDomainService.java
│   ├── authorization/
│   │   ├── model/
│   │   │   ├── Role.java
│   │   │   ├── Permission.java
│   │   │   ├── UserRoleAssignment.java
│   │   │   └── Policy.java
│   │   ├── repository/
│   │   │   ├── RoleRepository.java
│   │   │   ├── PermissionRepository.java
│   │   │   ├── UserRoleAssignmentRepository.java
│   │   │   └── PolicyRepository.java
│   │   └── service/
│   │       ├── RoleHierarchyService.java
│   │       └── PermissionAggregationService.java
│   ├── configuration/
│   │   ├── model/
│   │   │   ├── OIDCProviderConfig.java
│   │   │   ├── AttributeMappingConfig.java
│   │   │   └── RoleMappingConfig.java
│   │   └── repository/
│   │       └── OIDCProviderConfigRepository.java
│   └── governance/
│       ├── model/
│       │   └── AuditLog.java
│       └── repository/
│           └── AuditLogRepository.java
├── infrastructure/
│   ├── persistence/
│   │   └── jpa/
│   ├── security/
│   │   └── SecurityConfig.java
│   └── cache/
│       └── CacheConfig.java
└── shared/
    └── exception/
        └── DomainException.java
```

### Phase 2: User Management & Basic Auth (Weeks 3-4)

**Deliverables**:
- [x] User management REST APIs
- [x] Profile management
- [x] Basic authentication (JWT generation)
- [x] Token validation middleware
- [x] API documentation (OpenAPI/Swagger)

**API Endpoints**:
- POST /api/v1/users
- GET /api/v1/users/{userId}
- PATCH /api/v1/users/{userId}
- GET /api/v1/users (with pagination)

**Key Components**:
```
src/main/java/com/company/authservice/
├── application/
│   ├── user/
│   │   ├── UserApplicationService.java
│   │   ├── dto/
│   │   │   ├── CreateUserRequest.java
│   │   │   ├── UpdateUserRequest.java
│   │   │   ├── UserResponse.java
│   │   │   └── UserListResponse.java
│   │   └── mapper/
│   │       └── UserMapper.java
│   └── security/
│       ├── JwtTokenProvider.java
│       └── JwtAuthenticationFilter.java
└── interfaces/
    └── rest/
        ├── UserController.java
        └── advice/
            └── GlobalExceptionHandler.java
```

### Phase 3: OIDC Integration (Weeks 5-6)

**Deliverables**:
- [ ] OIDC provider configuration APIs
- [ ] OIDC authentication flow
- [ ] JIT provisioning logic
- [ ] Attribute mapping engine
- [ ] Role mapping engine
- [ ] Federated identity management

**API Endpoints**:
- POST /api/v1/auth/oidc/initiate
- POST /api/v1/auth/oidc/callback
- POST /api/v1/auth/token/refresh
- POST /api/v1/auth/logout
- GET/POST/PUT/DELETE /api/v1/oidc-providers/*

**Key Components**:
```
src/main/java/com/company/authservice/
├── application/
│   ├── auth/
│   │   ├── OIDCAuthenticationService.java
│   │   ├── JITProvisioningService.java
│   │   ├── AttributeTransformationService.java
│   │   ├── RoleMappingService.java
│   │   └── dto/
│   │       ├── OIDCInitiateRequest.java
│   │       ├── OIDCCallbackRequest.java
│   │       └── AuthenticationResponse.java
│   └── oidcprovider/
│       ├── OIDCProviderApplicationService.java
│       └── dto/
│           ├── OIDCProviderConfigRequest.java
│           └── OIDCProviderConfigResponse.java
├── domain/
│   └── identity/
│       └── service/
│           ├── IdentityFederationService.java
│           └── UserProvisioningService.java
└── interfaces/
    └── rest/
        ├── AuthenticationController.java
        └── OIDCProviderController.java
```

### Phase 4: Role & Permission Management (Weeks 7-8)

**Deliverables**:
- [ ] Role management APIs
- [ ] Permission management APIs
- [ ] Role assignment APIs
- [ ] Role hierarchy implementation
- [ ] Permission aggregation logic
- [ ] Bulk operations support

**API Endpoints**:
- GET/POST/PUT/DELETE /api/v1/roles/*
- GET/POST/DELETE /api/v1/permissions/*
- GET/POST/DELETE /api/v1/assignments/*
- POST /api/v1/assignments/bulk
- GET /api/v1/users/{userId}/permissions

**Key Components**:
```
src/main/java/com/company/authservice/
├── application/
│   ├── role/
│   │   ├── RoleApplicationService.java
│   │   └── dto/
│   ├── permission/
│   │   ├── PermissionApplicationService.java
│   │   └── dto/
│   └── assignment/
│       ├── RoleAssignmentApplicationService.java
│       └── dto/
└── interfaces/
    └── rest/
        ├── RoleController.java
        ├── PermissionController.java
        └── RoleAssignmentController.java
```

### Phase 5: Authorization Engine (PDP) (Weeks 9-11)

**Deliverables**:
- [ ] Policy Decision Point (PDP) implementation
- [ ] Policy evaluation engine (RBAC + ABAC)
- [ ] Policy management APIs
- [ ] Permission check APIs
- [ ] Caching layer (Redis integration)
- [ ] Performance optimization

**API Endpoints**:
- POST /api/v1/access-control/check
- POST /api/v1/access-control/batch-check
- GET/POST/PUT/DELETE /api/v1/policies/*

**Key Components**:
```
src/main/java/com/company/authservice/
├── application/
│   ├── authorization/
│   │   ├── PolicyDecisionPointService.java
│   │   ├── PolicyEvaluationService.java
│   │   ├── PolicyInformationPointService.java
│   │   └── dto/
│   │       ├── AuthorizationRequest.java
│   │       ├── AuthorizationDecision.java
│   │       └── BatchAuthorizationRequest.java
│   └── policy/
│       ├── PolicyApplicationService.java
│       └── dto/
├── domain/
│   └── authorization/
│       └── service/
│           ├── PolicyEvaluator.java
│           ├── ConditionEvaluator.java
│           └── PermissionResolver.java
└── interfaces/
    └── rest/
        ├── AuthorizationController.java
        └── PolicyController.java
```

### Phase 6: Audit & Governance (Weeks 12-13)

**Deliverables**:
- [ ] Comprehensive audit logging
- [ ] Audit query APIs
- [ ] Compliance reporting
- [ ] Access certification workflow
- [ ] PDP/PEP log correlation
- [ ] Dashboard metrics

**API Endpoints**:
- GET /api/v1/audit/logs
- GET /api/v1/audit/user-activity/{userId}
- GET /api/v1/audit/compliance-report
- POST /api/v1/audit/access-certifications

**Key Components**:
```
src/main/java/com/company/authservice/
├── application/
│   ├── audit/
│   │   ├── AuditService.java
│   │   ├── ComplianceReportingService.java
│   │   └── dto/
│   └── certification/
│       ├── AccessCertificationService.java
│       └── dto/
├── domain/
│   └── governance/
│       └── service/
│           └── AuditLogService.java
└── interfaces/
    └── rest/
        ├── AuditController.java
        └── ComplianceController.java
```

### Phase 7: Integration & Testing (Weeks 14-15)

**Deliverables**:
- [ ] Integration tests (Spring Boot Test)
- [ ] API contract tests
- [ ] Performance tests (JMeter/Gatling)
- [ ] Security testing (OWASP checks)
- [ ] Load testing
- [ ] Documentation completion

**Testing Structure**:
```
src/test/java/com/company/authservice/
├── integration/
│   ├── auth/
│   │   └── OIDCAuthenticationIntegrationTest.java
│   ├── user/
│   │   └── UserManagementIntegrationTest.java
│   ├── authorization/
│   │   └── PolicyEvaluationIntegrationTest.java
│   └── audit/
│       └── AuditLoggingIntegrationTest.java
├── unit/
│   ├── domain/
│   └── application/
└── performance/
    └── LoadTest.java
```

### Phase 8: Deployment & Production Readiness (Weeks 16-17)

**Deliverables**:
- [ ] Docker containerization
- [ ] Kubernetes manifests
- [ ] Monitoring setup (Prometheus/Grafana)
- [ ] Logging aggregation (ELK/Loki)
- [ ] Health checks and readiness probes
- [ ] Production configuration
- [ ] Backup and recovery procedures
- [ ] Runbook documentation

**DevOps Structure**:
```
deployment/
├── docker/
│   └── Dockerfile
├── kubernetes/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── configmap.yaml
│   ├── secret.yaml
│   └── ingress.yaml
├── monitoring/
│   ├── prometheus/
│   └── grafana/
└── scripts/
    ├── migrate-db.sh
    └── backup.sh
```

---

## 8. Additional Implementation Considerations

### 8.1 Security Best Practices

**Token Security**:
- Use RS256 (asymmetric) for JWT signing
- Implement token rotation
- Maintain JWT blacklist for revoked tokens
- Use short-lived access tokens (15-30 min)
- Use long-lived refresh tokens (7-30 days) with rotation

**Secret Management**:
- Store OIDC client secrets encrypted (AES-256)
- Use Spring Cloud Vault or AWS Secrets Manager
- Never log sensitive information
- Implement secret rotation policies

**API Security**:
- Rate limiting (Redis-based)
- Request validation (Jakarta Validation)
- CORS configuration
- CSRF protection for state-changing operations
- Input sanitization to prevent injection attacks

### 8.2 Performance Optimization

**Database Optimization**:
- Proper indexing strategy (see schema)
- Connection pooling (HikariCP)
- Query optimization and N+1 prevention
- Read replicas for audit queries
- Partition large tables (audit_logs)

**Caching Strategy**:
- Multi-level caching (L1: Caffeine, L2: Redis)
- Cache-aside pattern
- Intelligent cache invalidation
- Cache warming on startup
- Monitor cache hit rates

**Async Processing**:
- Async audit logging (don't block main flow)
- Event-driven architecture for provisioning
- Background jobs for certifications
- Queue-based processing (RabbitMQ/Kafka)

### 8.3 Observability

**Logging**:
- Structured logging (JSON format)
- Correlation IDs for request tracing
- Log levels per environment
- Sensitive data masking

**Metrics**:
- Authentication success/failure rates
- Authorization decision latency
- Token generation/validation times
- Cache hit/miss rates
- API endpoint metrics

**Tracing**:
- Distributed tracing (Spring Cloud Sleuth + Zipkin)
- Span annotations for key operations
- Service dependency mapping

### 8.4 Configuration Management

**application.yml Structure**:
```yaml
spring:
  application:
    name: auth-service
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${JWT_ISSUER_URI}

auth-service:
  jwt:
    private-key: ${JWT_PRIVATE_KEY}
    public-key: ${JWT_PUBLIC_KEY}
    access-token-expiry: 1800 # 30 minutes
    refresh-token-expiry: 2592000 # 30 days
  
  cache:
    enabled: true
    ttl:
      user-permissions: 300 # 5 minutes
      role-hierarchy: 600 # 10 minutes
  
  audit:
    enabled: true
    async: true
    retention-days: 365
  
  oidc:
    allowed-providers: ${ALLOWED_OIDC_PROVIDERS}
    callback-base-url: ${OIDC_CALLBACK_BASE_URL}
```

---

## 9. API Response Standards

### 9.1 Success Response Format

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

### 9.2 Error Response Format

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

### 9.3 Pagination Format

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

---

## 10. Summary Checklist

### Core Requirements Covered
- ✅ AuthN/AuthZ decoupling
- ✅ Centralized authorization service (PDP)
- ✅ Identity abstraction with immutable global identifier
- ✅ OIDC/SAML support
- ✅ JIT provisioning
- ✅ Attribute and role mapping
- ✅ PDP/PEP architecture
- ✅ Fine-grained authorization (RBAC + ABAC)
- ✅ Audit logging and compliance
- ✅ IGA framework foundation
- ✅ SOX/GDPR/HIPAA compliance support

### Technology Stack
- ✅ Spring Boot 3.x
- ✅ Java 25
- ✅ PostgreSQL
- ✅ Redis
- ✅ JWT (RS256)
- ✅ OpenAPI documentation

### Deliverables
- ✅ Comprehensive REST API design
- ✅ Request/Response DTOs
- ✅ Database schema
- ✅ DDD domain model
- ✅ Implementation phases
- ✅ Security considerations
- ✅ Performance optimization strategy

---

**Next Steps**: Begin Phase 1 implementation with project setup and core domain models.
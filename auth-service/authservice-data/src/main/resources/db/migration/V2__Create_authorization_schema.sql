-- ============================================
-- AUTHORIZATION SCHEMA
-- ============================================

CREATE SCHEMA IF NOT EXISTS authorization;

CREATE TABLE authorization.roles (
    role_id UUID PRIMARY KEY,
    role_name VARCHAR(255) UNIQUE NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    role_type VARCHAR(50) NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_roles_type ON authorization.roles(role_type);
CREATE INDEX idx_roles_name ON authorization.roles(role_name);

CREATE TABLE authorization.permissions (
    permission_id UUID PRIMARY KEY,
    resource VARCHAR(255) NOT NULL,
    action VARCHAR(100) NOT NULL,
    scope VARCHAR(100),
    conditions JSONB,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(resource, action, scope)
);

CREATE INDEX idx_permissions_resource ON authorization.permissions(resource);
CREATE INDEX idx_permissions_action ON authorization.permissions(action);

CREATE TABLE authorization.role_permissions (
    role_id UUID NOT NULL REFERENCES authorization.roles(role_id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES authorization.permissions(permission_id) ON DELETE CASCADE,
    granted_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_role_permissions_role ON authorization.role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission ON authorization.role_permissions(permission_id);

CREATE TABLE authorization.role_hierarchy (
    parent_role_id UUID NOT NULL REFERENCES authorization.roles(role_id) ON DELETE CASCADE,
    child_role_id UUID NOT NULL REFERENCES authorization.roles(role_id) ON DELETE CASCADE,
    PRIMARY KEY (parent_role_id, child_role_id),
    CHECK (parent_role_id != child_role_id)
);

CREATE INDEX idx_role_hierarchy_parent ON authorization.role_hierarchy(parent_role_id);
CREATE INDEX idx_role_hierarchy_child ON authorization.role_hierarchy(child_role_id);

CREATE TABLE authorization.user_role_assignments (
    assignment_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES identity.users(user_id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES authorization.roles(role_id) ON DELETE CASCADE,
    scope VARCHAR(50) NOT NULL,
    scope_context VARCHAR(255),
    effective_from TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    effective_until TIMESTAMP WITH TIME ZONE,
    assigned_by UUID NOT NULL,
    assigned_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_user_role_assignments_user ON authorization.user_role_assignments(user_id);
CREATE INDEX idx_user_role_assignments_role ON authorization.user_role_assignments(role_id);
CREATE INDEX idx_user_role_assignments_status ON authorization.user_role_assignments(status);
CREATE INDEX idx_user_role_assignments_effective ON authorization.user_role_assignments(effective_from, effective_until);

CREATE TABLE authorization.policies (
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
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_policies_type ON authorization.policies(policy_type);
CREATE INDEX idx_policies_enabled ON authorization.policies(enabled);
CREATE INDEX idx_policies_priority ON authorization.policies(priority);


-- ============================================
-- IDENTITY SCHEMA
-- ============================================

CREATE SCHEMA IF NOT EXISTS identity;

CREATE TABLE identity.users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_users_email ON identity.users(email);
CREATE INDEX idx_users_status ON identity.users(status);
CREATE INDEX idx_users_username ON identity.users(username);

CREATE TABLE identity.user_profiles (
    profile_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES identity.users(user_id) ON DELETE CASCADE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    display_name VARCHAR(255),
    attributes JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id)
);

CREATE INDEX idx_user_profiles_user ON identity.user_profiles(user_id);

CREATE TABLE identity.federated_identities (
    federated_identity_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES identity.users(user_id) ON DELETE CASCADE,
    provider_id UUID NOT NULL,
    subject_id VARCHAR(255) NOT NULL,
    issuer VARCHAR(500) NOT NULL,
    linked_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_synced_at TIMESTAMP WITH TIME ZONE,
    metadata JSONB,
    UNIQUE(provider_id, subject_id)
);

CREATE INDEX idx_federated_identities_user ON identity.federated_identities(user_id);
CREATE INDEX idx_federated_identities_provider ON identity.federated_identities(provider_id);
CREATE INDEX idx_federated_identities_subject ON identity.federated_identities(subject_id);


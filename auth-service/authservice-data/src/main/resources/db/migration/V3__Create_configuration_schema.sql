-- ============================================
-- CONFIGURATION SCHEMA
-- ============================================

CREATE SCHEMA IF NOT EXISTS configuration;

CREATE TABLE configuration.oidc_provider_configs (
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
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_oidc_providers_name ON configuration.oidc_provider_configs(provider_name);
CREATE INDEX idx_oidc_providers_enabled ON configuration.oidc_provider_configs(enabled);
CREATE INDEX idx_oidc_providers_type ON configuration.oidc_provider_configs(provider_type);


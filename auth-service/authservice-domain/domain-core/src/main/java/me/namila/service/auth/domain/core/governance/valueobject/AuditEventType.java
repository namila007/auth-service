package me.namila.service.auth.domain.core.governance.valueobject;

/**
 * Audit event type value object representing the type of audit event.
 */
public enum AuditEventType {
    AUTHENTICATION_SUCCESS,
    AUTHENTICATION_FAILURE,
    AUTHORIZATION_DECISION,
    USER_CREATED,
    USER_UPDATED,
    USER_DELETED,
    ROLE_ASSIGNED,
    ROLE_REVOKED,
    POLICY_CREATED,
    POLICY_UPDATED,
    POLICY_DELETED,
    OIDC_PROVIDER_CREATED,
    OIDC_PROVIDER_UPDATED,
    JIT_PROVISIONING,
    TOKEN_ISSUED,
    TOKEN_REVOKED,
    ACCESS_CERTIFICATION_INITIATED,
    ACCESS_CERTIFICATION_COMPLETED
}


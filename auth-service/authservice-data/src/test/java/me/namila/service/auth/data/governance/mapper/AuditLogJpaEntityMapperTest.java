package me.namila.service.auth.data.governance.mapper;

import me.namila.service.auth.data.governance.entity.AuditLogJpaEntity;
import me.namila.service.auth.domain.core.governance.model.id.AuditLogId;
import me.namila.service.auth.domain.core.governance.valueobject.ActorType;
import me.namila.service.auth.domain.core.governance.valueobject.AuditEventType;
import me.namila.service.auth.domain.core.governance.valueobject.Decision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AuditLogEntityMapper.
 */
@DisplayName("AuditLogEntityMapper Tests")
class AuditLogJpaEntityMapperTest
{

    private AuditLogEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(AuditLogEntityMapper.class);
    }

    @Test
    @DisplayName("Should map AuditLogJpaEntity to AuditLogJpaEntity domain model")
    void shouldMapEntityToDomain() {
        // Given
        UUID auditId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        Instant now = Instant.now();
        Map<String, Object> context = new HashMap<>();
        context.put("key", "value");

        AuditLogJpaEntity entity = AuditLogJpaEntity.builder()
                .auditId(auditId)
                .timestamp(now)
                .eventType("AUTHENTICATION_SUCCESS")
                .actorId(actorId)
                .actorType("USER")
                .subjectId(subjectId)
                .resource("user")
                .action("login")
                .decision("PERMIT")
                .policyVersion("1.0")
                .context(context)
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .correlationId("corr-123")
                .build();

        // When
        me.namila.service.auth.domain.core.governance.model.AuditLogEntity domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNotNull(domain.getId());
        assertEquals(auditId, domain.getId().getValue());
        assertEquals(now, domain.getTimestamp());
        assertEquals(AuditEventType.AUTHENTICATION_SUCCESS, domain.getEventType());
        assertEquals(actorId, domain.getActorId());
        assertEquals(ActorType.USER, domain.getActorType());
        assertEquals(subjectId, domain.getSubjectId());
        assertEquals("user", domain.getResource());
        assertEquals("login", domain.getAction());
        assertEquals(Decision.PERMIT, domain.getDecision());
        assertEquals("1.0", domain.getPolicyVersion());
        assertEquals(context, domain.getContext());
        assertEquals("192.168.1.1", domain.getIpAddress());
        assertEquals("Mozilla/5.0", domain.getUserAgent());
        assertEquals("corr-123", domain.getCorrelationId());
    }

    @Test
    @DisplayName("Should map AuditLogJpaEntity domain model to AuditLogJpaEntity")
    void shouldMapDomainToEntity() {
        // Given
        AuditLogId auditId = AuditLogId.generate();
        UUID actorId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        Instant now = Instant.now();
        Map<String, Object> context = new HashMap<>();
        context.put("key", "value");

        me.namila.service.auth.domain.core.governance.model.AuditLogEntity domain = me.namila.service.auth.domain.core.governance.model.AuditLogEntity.builder()
                .id(auditId)
                .timestamp(now)
                .eventType(AuditEventType.AUTHENTICATION_SUCCESS)
                .actorId(actorId)
                .actorType(ActorType.USER)
                .subjectId(subjectId)
                .resource("user")
                .action("login")
                .decision(Decision.PERMIT)
                .policyVersion("1.0")
                .context(context)
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .correlationId("corr-123")
                .build();

        // When
        AuditLogJpaEntity entity = mapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertEquals(auditId.getValue(), entity.getAuditId());
        assertEquals(now, entity.getTimestamp());
        assertEquals("AUTHENTICATION_SUCCESS", entity.getEventType());
        assertEquals(actorId, entity.getActorId());
        assertEquals("USER", entity.getActorType());
        assertEquals(subjectId, entity.getSubjectId());
        assertEquals("user", entity.getResource());
        assertEquals("login", entity.getAction());
        assertEquals("PERMIT", entity.getDecision());
        assertEquals("1.0", entity.getPolicyVersion());
        assertEquals(context, entity.getContext());
        assertEquals("192.168.1.1", entity.getIpAddress());
        assertEquals("Mozilla/5.0", entity.getUserAgent());
        assertEquals("corr-123", entity.getCorrelationId());
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        AuditLogJpaEntity entity = AuditLogJpaEntity.builder()
                .auditId(UUID.randomUUID())
                .timestamp(Instant.now())
                .eventType("AUTHENTICATION_SUCCESS")
                .actorId(UUID.randomUUID())
                .actorType("USER")
                .subjectId(null)
                .resource(null)
                .action(null)
                .decision(null)
                .policyVersion(null)
                .context(null)
                .ipAddress(null)
                .userAgent(null)
                .correlationId(null)
                .build();

        // When
        me.namila.service.auth.domain.core.governance.model.AuditLogEntity domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNull(domain.getSubjectId());
        assertNull(domain.getResource());
        assertNull(domain.getAction());
        assertNull(domain.getDecision());
        // MapStruct creates empty map instead of null for context
        assertTrue(domain.getContext() == null || domain.getContext().isEmpty());
    }

    @Test
    @DisplayName("Should map all AuditEventType enum values correctly")
    void shouldMapAllAuditEventTypeValues() {
        for (AuditEventType eventType : AuditEventType.values()) {
            // Given
            AuditLogJpaEntity entity = AuditLogJpaEntity.builder()
                    .auditId(UUID.randomUUID())
                    .timestamp(Instant.now())
                    .eventType(eventType.name())
                    .actorId(UUID.randomUUID())
                    .actorType("USER")
                    .build();

            // When
            me.namila.service.auth.domain.core.governance.model.AuditLogEntity domain = mapper.toDomain(entity);

            // Then
            assertEquals(eventType, domain.getEventType());

            // Reverse mapping
            me.namila.service.auth.domain.core.governance.model.AuditLogEntity domainForReverse = me.namila.service.auth.domain.core.governance.model.AuditLogEntity.builder()
                    .id(AuditLogId.generate())
                    .timestamp(Instant.now())
                    .eventType(eventType)
                    .actorId(UUID.randomUUID())
                    .actorType(ActorType.USER)
                    .build();
            AuditLogJpaEntity mappedEntity = mapper.toEntity(domainForReverse);
            assertEquals(eventType.name(), mappedEntity.getEventType());
        }
    }

    @Test
    @DisplayName("Should map all ActorType enum values correctly")
    void shouldMapAllActorTypeValues() {
        for (ActorType actorType : ActorType.values()) {
            // Given
            AuditLogJpaEntity entity = AuditLogJpaEntity.builder()
                    .auditId(UUID.randomUUID())
                    .timestamp(Instant.now())
                    .eventType("AUTHENTICATION_SUCCESS")
                    .actorId(UUID.randomUUID())
                    .actorType(actorType.name())
                    .build();

            // When
            me.namila.service.auth.domain.core.governance.model.AuditLogEntity domain = mapper.toDomain(entity);

            // Then
            assertEquals(actorType, domain.getActorType());

            // Reverse mapping
            me.namila.service.auth.domain.core.governance.model.AuditLogEntity domainForReverse = me.namila.service.auth.domain.core.governance.model.AuditLogEntity.builder()
                    .id(AuditLogId.generate())
                    .timestamp(Instant.now())
                    .eventType(AuditEventType.AUTHENTICATION_SUCCESS)
                    .actorId(UUID.randomUUID())
                    .actorType(actorType)
                    .build();
            AuditLogJpaEntity mappedEntity = mapper.toEntity(domainForReverse);
            assertEquals(actorType.name(), mappedEntity.getActorType());
        }
    }

    @Test
    @DisplayName("Should map all Decision enum values correctly")
    void shouldMapAllDecisionValues() {
        for (Decision decision : Decision.values()) {
            // Given
            AuditLogJpaEntity entity = AuditLogJpaEntity.builder()
                    .auditId(UUID.randomUUID())
                    .timestamp(Instant.now())
                    .eventType("AUTHENTICATION_SUCCESS")
                    .actorId(UUID.randomUUID())
                    .actorType("USER")
                    .decision(decision.name())
                    .build();

            // When
            me.namila.service.auth.domain.core.governance.model.AuditLogEntity domain = mapper.toDomain(entity);

            // Then
            assertEquals(decision, domain.getDecision());

            // Reverse mapping
            me.namila.service.auth.domain.core.governance.model.AuditLogEntity domainForReverse = me.namila.service.auth.domain.core.governance.model.AuditLogEntity.builder()
                    .id(AuditLogId.generate())
                    .timestamp(Instant.now())
                    .eventType(AuditEventType.AUTHENTICATION_SUCCESS)
                    .actorId(UUID.randomUUID())
                    .actorType(ActorType.USER)
                    .decision(decision)
                    .build();
            AuditLogJpaEntity mappedEntity = mapper.toEntity(domainForReverse);
            assertEquals(decision.name(), mappedEntity.getDecision());
        }
    }

    @Test
    @DisplayName("Should perform round-trip mapping correctly")
    void shouldPerformRoundTripMapping() {
        // Given
        AuditLogId auditId = AuditLogId.generate();
        UUID actorId = UUID.randomUUID();
        Map<String, Object> context = new HashMap<>();
        context.put("key", "value");

        me.namila.service.auth.domain.core.governance.model.AuditLogEntity originalDomain = me.namila.service.auth.domain.core.governance.model.AuditLogEntity.builder()
                .id(auditId)
                .timestamp(Instant.now())
                .eventType(AuditEventType.AUTHENTICATION_SUCCESS)
                .actorId(actorId)
                .actorType(ActorType.USER)
                .decision(Decision.PERMIT)
                .context(context)
                .build();

        // When
        AuditLogJpaEntity entity = mapper.toEntity(originalDomain);
        me.namila.service.auth.domain.core.governance.model.AuditLogEntity mappedDomain = mapper.toDomain(entity);

        // Then
        assertEquals(originalDomain.getId().getValue(), mappedDomain.getId().getValue());
        assertEquals(originalDomain.getEventType(), mappedDomain.getEventType());
        assertEquals(originalDomain.getActorType(), mappedDomain.getActorType());
        assertEquals(originalDomain.getDecision(), mappedDomain.getDecision());
        assertEquals(originalDomain.getContext(), mappedDomain.getContext());
    }
}


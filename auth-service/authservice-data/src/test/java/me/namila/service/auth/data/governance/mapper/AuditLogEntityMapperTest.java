package me.namila.service.auth.data.governance.mapper;

import me.namila.service.auth.data.governance.entity.AuditLogEntity;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AuditLogEntityMapper.
 */
@DisplayName("AuditLogEntityMapper Tests")
class AuditLogEntityMapperTest {

    private AuditLogEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(AuditLogEntityMapper.class);
    }

    @Test
    @DisplayName("Should map AuditLogEntity to AuditLogEntity domain model")
    void shouldMapEntityToDomain() {
        // Given
        UUID auditId = UUID.randomUUID();
        UUID actorId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        Instant now = Instant.now();
        Map<String, Object> context = new HashMap<>();
        context.put("key", "value");

        AuditLogEntity entity = AuditLogEntity.builder()
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
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isNotNull();
        assertThat(domain.getId().getValue()).isEqualTo(auditId);
        assertThat(domain.getTimestamp()).isEqualTo(now);
        assertThat(domain.getEventType()).isEqualTo(AuditEventType.AUTHENTICATION_SUCCESS);
        assertThat(domain.getActorId()).isEqualTo(actorId);
        assertThat(domain.getActorType()).isEqualTo(ActorType.USER);
        assertThat(domain.getSubjectId()).isEqualTo(subjectId);
        assertThat(domain.getResource()).isEqualTo("user");
        assertThat(domain.getAction()).isEqualTo("login");
        assertThat(domain.getDecision()).isEqualTo(Decision.PERMIT);
        assertThat(domain.getPolicyVersion()).isEqualTo("1.0");
        assertThat(domain.getContext()).isEqualTo(context);
        assertThat(domain.getIpAddress()).isEqualTo("192.168.1.1");
        assertThat(domain.getUserAgent()).isEqualTo("Mozilla/5.0");
        assertThat(domain.getCorrelationId()).isEqualTo("corr-123");
    }

    @Test
    @DisplayName("Should map AuditLogEntity domain model to AuditLogEntity")
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
        AuditLogEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getAuditId()).isEqualTo(auditId.getValue());
        assertThat(entity.getTimestamp()).isEqualTo(now);
        assertThat(entity.getEventType()).isEqualTo("AUTHENTICATION_SUCCESS");
        assertThat(entity.getActorId()).isEqualTo(actorId);
        assertThat(entity.getActorType()).isEqualTo("USER");
        assertThat(entity.getSubjectId()).isEqualTo(subjectId);
        assertThat(entity.getResource()).isEqualTo("user");
        assertThat(entity.getAction()).isEqualTo("login");
        assertThat(entity.getDecision()).isEqualTo("PERMIT");
        assertThat(entity.getPolicyVersion()).isEqualTo("1.0");
        assertThat(entity.getContext()).isEqualTo(context);
        assertThat(entity.getIpAddress()).isEqualTo("192.168.1.1");
        assertThat(entity.getUserAgent()).isEqualTo("Mozilla/5.0");
        assertThat(entity.getCorrelationId()).isEqualTo("corr-123");
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        AuditLogEntity entity = AuditLogEntity.builder()
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
        assertThat(domain).isNotNull();
        assertThat(domain.getSubjectId()).isNull();
        assertThat(domain.getResource()).isNull();
        assertThat(domain.getAction()).isNull();
        assertThat(domain.getDecision()).isNull();
        assertThat(domain.getContext()).isNull();
    }

    @Test
    @DisplayName("Should map all AuditEventType enum values correctly")
    void shouldMapAllAuditEventTypeValues() {
        for (AuditEventType eventType : AuditEventType.values()) {
            // Given
            AuditLogEntity entity = AuditLogEntity.builder()
                    .auditId(UUID.randomUUID())
                    .timestamp(Instant.now())
                    .eventType(eventType.name())
                    .actorId(UUID.randomUUID())
                    .actorType("USER")
                    .build();

            // When
            me.namila.service.auth.domain.core.governance.model.AuditLogEntity domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.getEventType()).isEqualTo(eventType);

            // Reverse mapping
            me.namila.service.auth.domain.core.governance.model.AuditLogEntity domainForReverse = me.namila.service.auth.domain.core.governance.model.AuditLogEntity.builder()
                    .id(AuditLogId.generate())
                    .timestamp(Instant.now())
                    .eventType(eventType)
                    .actorId(UUID.randomUUID())
                    .actorType(ActorType.USER)
                    .build();
            AuditLogEntity mappedEntity = mapper.toEntity(domainForReverse);
            assertThat(mappedEntity.getEventType()).isEqualTo(eventType.name());
        }
    }

    @Test
    @DisplayName("Should map all ActorType enum values correctly")
    void shouldMapAllActorTypeValues() {
        for (ActorType actorType : ActorType.values()) {
            // Given
            AuditLogEntity entity = AuditLogEntity.builder()
                    .auditId(UUID.randomUUID())
                    .timestamp(Instant.now())
                    .eventType("AUTHENTICATION_SUCCESS")
                    .actorId(UUID.randomUUID())
                    .actorType(actorType.name())
                    .build();

            // When
            me.namila.service.auth.domain.core.governance.model.AuditLogEntity domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.getActorType()).isEqualTo(actorType);

            // Reverse mapping
            me.namila.service.auth.domain.core.governance.model.AuditLogEntity domainForReverse = me.namila.service.auth.domain.core.governance.model.AuditLogEntity.builder()
                    .id(AuditLogId.generate())
                    .timestamp(Instant.now())
                    .eventType(AuditEventType.AUTHENTICATION_SUCCESS)
                    .actorId(UUID.randomUUID())
                    .actorType(actorType)
                    .build();
            AuditLogEntity mappedEntity = mapper.toEntity(domainForReverse);
            assertThat(mappedEntity.getActorType()).isEqualTo(actorType.name());
        }
    }

    @Test
    @DisplayName("Should map all Decision enum values correctly")
    void shouldMapAllDecisionValues() {
        for (Decision decision : Decision.values()) {
            // Given
            AuditLogEntity entity = AuditLogEntity.builder()
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
            assertThat(domain.getDecision()).isEqualTo(decision);

            // Reverse mapping
            me.namila.service.auth.domain.core.governance.model.AuditLogEntity domainForReverse = me.namila.service.auth.domain.core.governance.model.AuditLogEntity.builder()
                    .id(AuditLogId.generate())
                    .timestamp(Instant.now())
                    .eventType(AuditEventType.AUTHENTICATION_SUCCESS)
                    .actorId(UUID.randomUUID())
                    .actorType(ActorType.USER)
                    .decision(decision)
                    .build();
            AuditLogEntity mappedEntity = mapper.toEntity(domainForReverse);
            assertThat(mappedEntity.getDecision()).isEqualTo(decision.name());
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
        AuditLogEntity entity = mapper.toEntity(originalDomain);
        me.namila.service.auth.domain.core.governance.model.AuditLogEntity mappedDomain = mapper.toDomain(entity);

        // Then
        assertThat(mappedDomain.getId().getValue()).isEqualTo(originalDomain.getId().getValue());
        assertThat(mappedDomain.getEventType()).isEqualTo(originalDomain.getEventType());
        assertThat(mappedDomain.getActorType()).isEqualTo(originalDomain.getActorType());
        assertThat(mappedDomain.getDecision()).isEqualTo(originalDomain.getDecision());
        assertThat(mappedDomain.getContext()).isEqualTo(originalDomain.getContext());
    }
}


package me.namila.service.auth.data.identity.mapper;

import me.namila.service.auth.data.identity.entity.FederatedIdentityJpaEntity;
import me.namila.service.auth.data.identity.entity.UserJpaEntity;
import me.namila.service.auth.domain.core.identity.model.id.FederatedIdentityId;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
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
 * Unit tests for FederatedIdentityEntityMapper.
 */
@DisplayName("FederatedIdentityEntityMapper Tests")
class FederatedIdentityJpaEntityMapperTest
{

    private FederatedIdentityEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(FederatedIdentityEntityMapper.class);
    }

    @Test
    @DisplayName("Should map FederatedIdentityJpaEntity to domain model")
    void shouldMapEntityToDomain() {
        // Given
        UUID federatedIdentityId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        Instant now = Instant.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        UserJpaEntity userJpaEntity = UserJpaEntity.builder()
                .userId(userId)
                .username("testuser")
                .email("test@example.com")
                .status("ACTIVE")
                .createdAt(now)
                .lastModifiedAt(now)
                .version(0L)
                .build();

        FederatedIdentityJpaEntity entity = FederatedIdentityJpaEntity.builder()
                .federatedIdentityId(federatedIdentityId)
                .user(userJpaEntity)
                .providerId(providerId)
                .subjectId("subject123")
                .issuer("https://example.com")
                .linkedAt(now)
                .lastSyncedAt(now)
                .metadata(metadata)
                .build();

        // When
      me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNotNull(domain.getId());
        assertEquals(federatedIdentityId, domain.getId().getValue());
        assertNotNull(domain.getUserId());
        assertEquals(userId, domain.getUserId().getValue());
        assertEquals(providerId, domain.getProviderId());
        assertEquals("subject123", domain.getSubjectId());
        assertEquals("https://example.com", domain.getIssuer());
        assertEquals(now, domain.getLinkedAt());
        assertEquals(now, domain.getLastSyncedAt());
        assertEquals(metadata, domain.getMetadata());
    }

    @Test
    @DisplayName("Should map domain model to FederatedIdentityJpaEntity")
    void shouldMapDomainToEntity() {
        // Given
        FederatedIdentityId federatedIdentityId = FederatedIdentityId.generate();
        UserId userId = UserId.generate();
        UUID providerId = UUID.randomUUID();
        Instant instant = Instant.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity domain = me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity.builder()
                .id(federatedIdentityId)
                .userId(userId)
                .providerId(providerId)
                .subjectId("subject123")
                .issuer("https://example.com")
                .linkedAt(instant)
                .lastSyncedAt(instant)
                .metadata(metadata)
                .build();

        // When
        FederatedIdentityJpaEntity entity = mapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertEquals(federatedIdentityId.getValue(), entity.getFederatedIdentityId());
        assertEquals(providerId, entity.getProviderId());
        assertEquals("subject123", entity.getSubjectId());
        assertEquals("https://example.com", entity.getIssuer());
        assertEquals(instant, entity.getLinkedAt());
        assertEquals(instant, entity.getLastSyncedAt());
        assertEquals(metadata, entity.getMetadata());
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        FederatedIdentityJpaEntity entity = FederatedIdentityJpaEntity.builder()
                .federatedIdentityId(UUID.randomUUID())
                .user(null)
                .providerId(UUID.randomUUID())
                .subjectId("subject123")
                .issuer("https://example.com")
                .linkedAt(Instant.now())
                .lastSyncedAt(null)
                .metadata(null)
                .build();

        // When
        me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNull(domain.getUserId()); // user is null, so userId will be null
        assertNull(domain.getLastSyncedAt());
        // MapStruct creates empty map instead of null for metadata
        assertTrue(domain.getMetadata() == null || domain.getMetadata().isEmpty());
    }

    @Test
    @DisplayName("Should handle null values when mapping domain to entity")
    void shouldHandleNullValuesDomainToEntity() {
        // Given
        me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity domain = me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity.builder()
                .id(FederatedIdentityId.generate())
                .userId(null)
                .providerId(UUID.randomUUID())
                .subjectId("subject123")
                .issuer("https://example.com")
                .linkedAt(Instant.now())
                .lastSyncedAt(null)
                .metadata(null)
                .build();

        // When
        FederatedIdentityJpaEntity entity = mapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertNull(entity.getLastSyncedAt());
        // MapStruct creates empty map instead of null for metadata
        assertTrue(entity.getMetadata() == null || entity.getMetadata().isEmpty());
    }

    @Test
    @DisplayName("Should perform round-trip mapping correctly")
    void shouldPerformRoundTripMapping() {
        // Given
        FederatedIdentityId federatedIdentityId = FederatedIdentityId.generate();
        UserId userId = UserId.generate();
        UUID providerId = UUID.randomUUID();
        Instant now = Instant.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity originalDomain = me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity.builder()
                .id(federatedIdentityId)
                .userId(userId)
                .providerId(providerId)
                .subjectId("subject123")
                .issuer("https://example.com")
                .linkedAt(now)
                .lastSyncedAt(now)
                .metadata(metadata)
                .build();

        // When
        FederatedIdentityJpaEntity entity = mapper.toEntity(originalDomain);
        me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity mappedDomain = mapper.toDomain(entity);

        // Then
        assertEquals(originalDomain.getId().getValue(), mappedDomain.getId().getValue());
        // userId will be null because user entity is not set in JPA entity
        // and userId is extracted from user.userId in the mapper
        // This is expected behavior - user entity must be set separately
        assertNull(mappedDomain.getUserId());
        assertEquals(originalDomain.getProviderId(), mappedDomain.getProviderId());
        assertEquals(originalDomain.getSubjectId(), mappedDomain.getSubjectId());
        assertEquals(originalDomain.getIssuer(), mappedDomain.getIssuer());
        assertEquals(originalDomain.getLinkedAt(), mappedDomain.getLinkedAt());
        assertEquals(originalDomain.getLastSyncedAt(), mappedDomain.getLastSyncedAt());
        assertEquals(originalDomain.getMetadata(), mappedDomain.getMetadata());
    }
}



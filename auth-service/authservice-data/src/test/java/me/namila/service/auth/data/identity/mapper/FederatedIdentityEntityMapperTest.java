package me.namila.service.auth.data.identity.mapper;

import me.namila.service.auth.data.identity.entity.FederatedIdentityEntity;
import me.namila.service.auth.data.identity.entity.UserEntity;
import me.namila.service.auth.domain.core.identity.model.FederatedIdentity;
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
 * Unit tests for FederatedIdentityEntityMapper.
 */
@DisplayName("FederatedIdentityEntityMapper Tests")
class FederatedIdentityEntityMapperTest {

    private FederatedIdentityEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(FederatedIdentityEntityMapper.class);
    }

    @Test
    @DisplayName("Should map FederatedIdentityEntity to FederatedIdentity domain model")
    void shouldMapEntityToDomain() {
        // Given
        UUID federatedIdentityId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        Instant now = Instant.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        UserEntity userEntity = UserEntity.builder()
                .userId(userId)
                .username("testuser")
                .email("test@example.com")
                .status("ACTIVE")
                .createdAt(now)
                .lastModifiedAt(now)
                .version(0L)
                .build();

        FederatedIdentityEntity entity = FederatedIdentityEntity.builder()
                .federatedIdentityId(federatedIdentityId)
                .user(userEntity)
                .providerId(providerId)
                .subjectId("subject123")
                .issuer("https://example.com")
                .linkedAt(now)
                .lastSyncedAt(now)
                .metadata(metadata)
                .build();

        // When
        FederatedIdentity domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getFederatedIdentityId()).isEqualTo(federatedIdentityId);
        assertThat(domain.getUserId()).isEqualTo(userId);
        assertThat(domain.getProviderId()).isEqualTo(providerId);
        assertThat(domain.getSubjectId()).isEqualTo("subject123");
        assertThat(domain.getIssuer()).isEqualTo("https://example.com");
        assertThat(domain.getLinkedAt()).isEqualTo(now);
        assertThat(domain.getLastSyncedAt()).isEqualTo(now);
        assertThat(domain.getMetadata()).isEqualTo(metadata);
    }

    @Test
    @DisplayName("Should map FederatedIdentity domain model to FederatedIdentityEntity")
    void shouldMapDomainToEntity() {
        // Given
        UUID federatedIdentityId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        Instant now = Instant.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        FederatedIdentity domain = FederatedIdentity.builder()
                .federatedIdentityId(federatedIdentityId)
                .userId(userId)
                .providerId(providerId)
                .subjectId("subject123")
                .issuer("https://example.com")
                .linkedAt(now)
                .lastSyncedAt(now)
                .metadata(metadata)
                .build();

        // When
        FederatedIdentityEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getFederatedIdentityId()).isEqualTo(federatedIdentityId);
        assertThat(entity.getProviderId()).isEqualTo(providerId);
        assertThat(entity.getSubjectId()).isEqualTo("subject123");
        assertThat(entity.getIssuer()).isEqualTo("https://example.com");
        assertThat(entity.getLinkedAt()).isEqualTo(now);
        assertThat(entity.getLastSyncedAt()).isEqualTo(now);
        assertThat(entity.getMetadata()).isEqualTo(metadata);
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        FederatedIdentityEntity entity = FederatedIdentityEntity.builder()
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
        FederatedIdentity domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getLastSyncedAt()).isNull();
        assertThat(domain.getMetadata()).isNull();
    }

    @Test
    @DisplayName("Should handle null values when mapping domain to entity")
    void shouldHandleNullValuesDomainToEntity() {
        // Given
        FederatedIdentity domain = FederatedIdentity.builder()
                .federatedIdentityId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .providerId(UUID.randomUUID())
                .subjectId("subject123")
                .issuer("https://example.com")
                .linkedAt(Instant.now())
                .lastSyncedAt(null)
                .metadata(null)
                .build();

        // When
        FederatedIdentityEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getLastSyncedAt()).isNull();
        assertThat(entity.getMetadata()).isNull();
    }

    @Test
    @DisplayName("Should perform round-trip mapping correctly")
    void shouldPerformRoundTripMapping() {
        // Given
        UUID federatedIdentityId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        Instant now = Instant.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        FederatedIdentity originalDomain = FederatedIdentity.builder()
                .federatedIdentityId(federatedIdentityId)
                .userId(userId)
                .providerId(providerId)
                .subjectId("subject123")
                .issuer("https://example.com")
                .linkedAt(now)
                .lastSyncedAt(now)
                .metadata(metadata)
                .build();

        // When
        FederatedIdentityEntity entity = mapper.toEntity(originalDomain);
        FederatedIdentity mappedDomain = mapper.toDomain(entity);

        // Then
        assertThat(mappedDomain.getFederatedIdentityId()).isEqualTo(originalDomain.getFederatedIdentityId());
        assertThat(mappedDomain.getUserId()).isEqualTo(originalDomain.getUserId());
        assertThat(mappedDomain.getProviderId()).isEqualTo(originalDomain.getProviderId());
        assertThat(mappedDomain.getSubjectId()).isEqualTo(originalDomain.getSubjectId());
        assertThat(mappedDomain.getIssuer()).isEqualTo(originalDomain.getIssuer());
        assertThat(mappedDomain.getLinkedAt()).isEqualTo(originalDomain.getLinkedAt());
        assertThat(mappedDomain.getLastSyncedAt()).isEqualTo(originalDomain.getLastSyncedAt());
        assertThat(mappedDomain.getMetadata()).isEqualTo(originalDomain.getMetadata());
    }
}



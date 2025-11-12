package me.namila.service.auth.data.identity.mapper;

import me.namila.service.auth.data.identity.entity.UserJpaEntity;
import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import me.namila.service.auth.domain.core.identity.valueobject.EmailValue;
import me.namila.service.auth.domain.core.identity.valueobject.UserStatus;
import me.namila.service.auth.domain.core.identity.valueobject.UsernameValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserEntityMapper.
 */
@DisplayName("UserEntityMapper Tests")
class UserJpaEntityMapperTest
{

    private UserEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserEntityMapper.class);
    }

    @Test
    @DisplayName("Should map UserJpaEntity to UserAggregate domain model")
    void shouldMapEntityToDomain() {
        // Given
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(now, ZoneOffset.UTC);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        UserJpaEntity entity = UserJpaEntity.builder()
                .userId(userId)
                .username("testuser")
                .email("test@example.com")
                .status("ACTIVE")
                .metadata(metadata)
                .createdAt(now)
                .lastModifiedAt(now)
                .version(1L)
                .build();

        // When
        UserAggregate domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNotNull(domain.getId());
        assertEquals(userId, domain.getId().getValue());
        assertNotNull(domain.getUsername());
        assertEquals("testuser", domain.getUsername().getValue());
        assertNotNull(domain.getEmail());
        assertEquals("test@example.com", domain.getEmail().getValue());
        assertEquals(UserStatus.ACTIVE, domain.getStatus());
        assertEquals(metadata, domain.getMetadata());
        assertEquals(localDateTime, domain.getCreatedAt());
        assertEquals(localDateTime, domain.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map UserAggregate domain model to UserJpaEntity")
    void shouldMapDomainToEntity() {
        // Given
        UserId userId = UserId.generate();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        UserAggregate domain = UserAggregate.builder()
                .id(userId)
                .username(UsernameValue.of("testuser"))
                .email(EmailValue.of("test@example.com"))
                .status(UserStatus.ACTIVE)
                .metadata(metadata)
                .createdAt(now)
                .updatedAt(now)
                .version(1L)
                .build();

        // When
        UserJpaEntity entity = mapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertEquals(userId.getValue(), entity.getUserId());
        assertEquals("testuser", entity.getUsername());
        assertEquals("test@example.com", entity.getEmail());
        assertEquals("ACTIVE", entity.getStatus());
        assertEquals(metadata, entity.getMetadata());
        assertEquals(now.toInstant(ZoneOffset.UTC), entity.getCreatedAt());
        assertEquals(now.toInstant(ZoneOffset.UTC), entity.getLastModifiedAt());
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        UserJpaEntity entity = UserJpaEntity.builder()
                .userId(UUID.randomUUID())
                .username(null)
                .email(null)
                .status(null)
                .metadata(null)
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .version(0L)
                .build();

        // When
        UserAggregate domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNull(domain.getUsername());
        assertNull(domain.getEmail());
        assertNull(domain.getStatus());
        // MapStruct creates empty map instead of null for metadata
        assertTrue(domain.getMetadata() == null || domain.getMetadata().isEmpty());
    }

    @Test
    @DisplayName("Should handle null values when mapping domain to entity")
    void shouldHandleNullValuesDomainToEntity() {
        // Given
        UserAggregate domain = UserAggregate.builder()
                .id(UserId.generate())
                .username(null)
                .email(null)
                .status(null)
                .metadata(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .version(0L)
                .build();

        // When
        UserJpaEntity entity = mapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertNull(entity.getUsername());
        assertNull(entity.getEmail());
        assertNull(entity.getStatus());
        // MapStruct creates empty map instead of null for metadata
        assertTrue(entity.getMetadata() == null || entity.getMetadata().isEmpty());
    }

    @Test
    @DisplayName("Should map all UserStatus enum values correctly")
    void shouldMapAllUserStatusValues() {
        // Test all enum values
        for (UserStatus status : UserStatus.values()) {
            // Given
            UserJpaEntity entity = UserJpaEntity.builder()
                    .userId(UUID.randomUUID())
                    .username("testuser")
                    .email("test@example.com")
                    .status(status.name())
                    .createdAt(Instant.now())
                    .lastModifiedAt(Instant.now())
                    .version(0L)
                    .build();

            // When
            UserAggregate domain = mapper.toDomain(entity);

            // Then
            assertEquals(status, domain.getStatus());

            // Reverse mapping
            UserJpaEntity mappedEntity = mapper.toEntity(domain);
            assertEquals(status.name(), mappedEntity.getStatus());
        }
    }

    @Test
    @DisplayName("Should map UsernameValue value object correctly")
    void shouldMapUsernameValueObject() {
        // Given
        String usernameValue = "testuser";
        UserJpaEntity entity = UserJpaEntity.builder()
                .userId(UUID.randomUUID())
                .username(usernameValue)
                .email("test@example.com")
                .status("ACTIVE")
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .version(0L)
                .build();

        // When
        UserAggregate domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain.getUsername());
        assertEquals(usernameValue, domain.getUsername().getValue());

        // Reverse mapping
        UserJpaEntity mappedEntity = mapper.toEntity(domain);
        assertEquals(usernameValue, mappedEntity.getUsername());
    }

    @Test
    @DisplayName("Should map EmailValue value object correctly")
    void shouldMapEmailValueObject() {
        // Given
        String emailValue = "test@example.com";
        UserJpaEntity entity = UserJpaEntity.builder()
                .userId(UUID.randomUUID())
                .username("testuser")
                .email(emailValue)
                .status("ACTIVE")
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .version(0L)
                .build();

        // When
        UserAggregate domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain.getEmail());
        assertEquals(emailValue, domain.getEmail().getValue());

        // Reverse mapping
        UserJpaEntity mappedEntity = mapper.toEntity(domain);
        assertEquals(emailValue, mappedEntity.getEmail());
    }

    @Test
    @DisplayName("Should convert timestamps correctly between Instant and LocalDateTime")
    void shouldConvertTimestampsCorrectly() {
        // Given
        Instant instant = Instant.now();
        LocalDateTime expectedLocalDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        
        UserJpaEntity entity = UserJpaEntity.builder()
                .userId(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .status("ACTIVE")
                .createdAt(instant)
                .lastModifiedAt(instant)
                .version(0L)
                .build();

        // When
        UserAggregate domain = mapper.toDomain(entity);

        // Then
        assertEquals(expectedLocalDateTime, domain.getCreatedAt());
        assertEquals(expectedLocalDateTime, domain.getUpdatedAt());

        // Reverse mapping
        UserJpaEntity mappedEntity = mapper.toEntity(domain);
        assertEquals(instant, mappedEntity.getCreatedAt());
        assertEquals(instant, mappedEntity.getLastModifiedAt());
    }
}



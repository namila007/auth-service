package me.namila.service.auth.data.identity.mapper;

import me.namila.service.auth.data.identity.entity.UserEntity;
import me.namila.service.auth.domain.core.identity.model.User;
import me.namila.service.auth.domain.core.identity.valueobject.Email;
import me.namila.service.auth.domain.core.identity.valueobject.UserStatus;
import me.namila.service.auth.domain.core.identity.valueobject.Username;
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
 * Unit tests for UserEntityMapper.
 */
@DisplayName("UserEntityMapper Tests")
class UserEntityMapperTest {

    private UserEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserEntityMapper.class);
    }

    @Test
    @DisplayName("Should map UserEntity to User domain model")
    void shouldMapEntityToDomain() {
        // Given
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        UserEntity entity = UserEntity.builder()
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
        User domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getUserId()).isEqualTo(userId);
        assertThat(domain.getUsername()).isNotNull();
        assertThat(domain.getUsername().getValue()).isEqualTo("testuser");
        assertThat(domain.getEmail()).isNotNull();
        assertThat(domain.getEmail().getValue()).isEqualTo("test@example.com");
        assertThat(domain.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(domain.getMetadata()).isEqualTo(metadata);
        assertThat(domain.getCreatedAt()).isEqualTo(now);
        assertThat(domain.getLastModifiedAt()).isEqualTo(now);
        assertThat(domain.getVersion()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should map User domain model to UserEntity")
    void shouldMapDomainToEntity() {
        // Given
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        User domain = User.builder()
                .userId(userId)
                .username(Username.of("testuser"))
                .email(Email.of("test@example.com"))
                .status(UserStatus.ACTIVE)
                .metadata(metadata)
                .createdAt(now)
                .lastModifiedAt(now)
                .version(1L)
                .build();

        // When
        UserEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getUsername()).isEqualTo("testuser");
        assertThat(entity.getEmail()).isEqualTo("test@example.com");
        assertThat(entity.getStatus()).isEqualTo("ACTIVE");
        assertThat(entity.getMetadata()).isEqualTo(metadata);
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getLastModifiedAt()).isEqualTo(now);
        assertThat(entity.getVersion()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        UserEntity entity = UserEntity.builder()
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
        User domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getUsername()).isNull();
        assertThat(domain.getEmail()).isNull();
        assertThat(domain.getStatus()).isNull();
        assertThat(domain.getMetadata()).isNull();
    }

    @Test
    @DisplayName("Should handle null values when mapping domain to entity")
    void shouldHandleNullValuesDomainToEntity() {
        // Given
        User domain = User.builder()
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
        UserEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getUsername()).isNull();
        assertThat(entity.getEmail()).isNull();
        assertThat(entity.getStatus()).isNull();
        assertThat(entity.getMetadata()).isNull();
    }

    @Test
    @DisplayName("Should map all UserStatus enum values correctly")
    void shouldMapAllUserStatusValues() {
        // Test all enum values
        for (UserStatus status : UserStatus.values()) {
            // Given
            UserEntity entity = UserEntity.builder()
                    .userId(UUID.randomUUID())
                    .username("testuser")
                    .email("test@example.com")
                    .status(status.name())
                    .createdAt(Instant.now())
                    .lastModifiedAt(Instant.now())
                    .version(0L)
                    .build();

            // When
            User domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.getStatus()).isEqualTo(status);

            // Reverse mapping
            UserEntity mappedEntity = mapper.toEntity(domain);
            assertThat(mappedEntity.getStatus()).isEqualTo(status.name());
        }
    }

    @Test
    @DisplayName("Should map Username value object correctly")
    void shouldMapUsernameValueObject() {
        // Given
        String usernameValue = "testuser";
        UserEntity entity = UserEntity.builder()
                .userId(UUID.randomUUID())
                .username(usernameValue)
                .email("test@example.com")
                .status("ACTIVE")
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .version(0L)
                .build();

        // When
        User domain = mapper.toDomain(entity);

        // Then
        assertThat(domain.getUsername()).isNotNull();
        assertThat(domain.getUsername().getValue()).isEqualTo(usernameValue);

        // Reverse mapping
        UserEntity mappedEntity = mapper.toEntity(domain);
        assertThat(mappedEntity.getUsername()).isEqualTo(usernameValue);
    }

    @Test
    @DisplayName("Should map Email value object correctly")
    void shouldMapEmailValueObject() {
        // Given
        String emailValue = "test@example.com";
        UserEntity entity = UserEntity.builder()
                .userId(UUID.randomUUID())
                .username("testuser")
                .email(emailValue)
                .status("ACTIVE")
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .version(0L)
                .build();

        // When
        User domain = mapper.toDomain(entity);

        // Then
        assertThat(domain.getEmail()).isNotNull();
        assertThat(domain.getEmail().getValue()).isEqualTo(emailValue);

        // Reverse mapping
        UserEntity mappedEntity = mapper.toEntity(domain);
        assertThat(mappedEntity.getEmail()).isEqualTo(emailValue);
    }
}



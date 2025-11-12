package me.namila.service.auth.data.authorization.mapper;

import static org.junit.jupiter.api.Assertions.*;

import me.namila.service.auth.data.authorization.entity.PermissionJpaEntity;
import me.namila.service.auth.domain.core.authorization.model.id.PermissionId;
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


/**
 * Unit tests for PermissionEntityMapper.
 */
@DisplayName("PermissionEntityMapper Tests")
class PermissionJpaEntityMapperTest
{

    private PermissionEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PermissionEntityMapper.class);
    }

    @Test
    @DisplayName("Should map PermissionJpaEntity to domain model")
    void shouldMapEntityToDomain() {
        // Given
        UUID permissionId = UUID.randomUUID();
        Instant now = Instant.now();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(now, ZoneOffset.UTC);
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("key", "value");

        PermissionJpaEntity entity = PermissionJpaEntity.builder()
                .permissionId(permissionId)
                .resource("user")
                .action("read")
                .scope("tenant")
                .conditions(conditions)
                .description("Read user permission")
                .createdAt(now)
                .build();

        // When
        me.namila.service.auth.domain.core.authorization.model.PermissionEntity domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNotNull(domain.getId());
        assertEquals(permissionId, domain.getId().getValue());
        assertEquals("user", domain.getResource());
        assertEquals("read", domain.getAction());
        assertEquals("tenant", domain.getScope());
        assertEquals(conditions, domain.getConditions());
        assertEquals("Read user permission", domain.getDescription());
        assertEquals(localDateTime, domain.getCreatedAt());
    }

    @Test
    @DisplayName("Should map domain model to PermissionJpaEntity")
    void shouldMapDomainToEntity() {
        // Given
        PermissionId permissionId = PermissionId.generate();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("key", "value");

        me.namila.service.auth.domain.core.authorization.model.PermissionEntity domain = me.namila.service.auth.domain.core.authorization.model.PermissionEntity.builder()
                .id(permissionId)
                .resource("user")
                .action("read")
                .scope("tenant")
                .conditions(conditions)
                .description("Read user permission")
                .createdAt(now)
                .build();

        // When
        PermissionJpaEntity entity = mapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertEquals(permissionId.getValue(), entity.getPermissionId());
        assertEquals("user", entity.getResource());
        assertEquals("read", entity.getAction());
        assertEquals("tenant", entity.getScope());
        assertEquals(conditions, entity.getConditions());
        assertEquals("Read user permission", entity.getDescription());
        assertEquals(now.toInstant(ZoneOffset.UTC), entity.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        PermissionJpaEntity entity = PermissionJpaEntity.builder()
                .permissionId(UUID.randomUUID())
                .resource("user")
                .action("read")
                .scope(null)
                .conditions(null)
                .description(null)
                .createdAt(Instant.now())
                .build();

        // When
        me.namila.service.auth.domain.core.authorization.model.PermissionEntity domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNull(domain.getScope());
        // MapStruct creates empty map instead of null for conditions
        assertTrue(domain.getConditions() == null || domain.getConditions().isEmpty());
        assertNull(domain.getDescription());
    }

    @Test
    @DisplayName("Should convert timestamps correctly between Instant and LocalDateTime")
    void shouldConvertTimestampsCorrectly() {
        // Given
        Instant instant = Instant.now();
        LocalDateTime expectedLocalDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        
        PermissionJpaEntity entity = PermissionJpaEntity.builder()
                .permissionId(UUID.randomUUID())
                .resource("user")
                .action("read")
                .createdAt(instant)
                .build();

        // When
        me.namila.service.auth.domain.core.authorization.model.PermissionEntity domain = mapper.toDomain(entity);

        // Then
        assertEquals(expectedLocalDateTime, domain.getCreatedAt());

        // Reverse mapping
        PermissionJpaEntity mappedEntity = mapper.toEntity(domain);
        assertEquals(instant, mappedEntity.getCreatedAt());
    }

    @Test
    @DisplayName("Should perform round-trip mapping correctly")
    void shouldPerformRoundTripMapping() {
        // Given
        PermissionId permissionId = PermissionId.generate();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("key", "value");

        me.namila.service.auth.domain.core.authorization.model.PermissionEntity originalDomain = me.namila.service.auth.domain.core.authorization.model.PermissionEntity.builder()
                .id(permissionId)
                .resource("user")
                .action("read")
                .scope("tenant")
                .conditions(conditions)
                .description("Read user permission")
                .createdAt(now)
                .build();

        // When
        PermissionJpaEntity entity = mapper.toEntity(originalDomain);
        me.namila.service.auth.domain.core.authorization.model.PermissionEntity mappedDomain = mapper.toDomain(entity);

        // Then
        assertEquals(originalDomain.getId().getValue(), mappedDomain.getId().getValue());
        assertEquals(originalDomain.getResource(), mappedDomain.getResource());
        assertEquals(originalDomain.getAction(), mappedDomain.getAction());
        assertEquals(originalDomain.getScope(), mappedDomain.getScope());
        assertEquals(originalDomain.getConditions(), mappedDomain.getConditions());
        assertEquals(originalDomain.getDescription(), mappedDomain.getDescription());
    }
}

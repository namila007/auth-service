package me.namila.service.auth.data.authorization.mapper;

import me.namila.service.auth.data.authorization.entity.RoleJpaEntity;
import me.namila.service.auth.domain.core.authorization.model.RoleAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.RoleId;
import me.namila.service.auth.domain.core.authorization.valueobject.RoleType;
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
 * Unit tests for RoleEntityMapper.
 */
@DisplayName("RoleEntityMapper Tests")
class RoleJpaEntityMapperTest
{

    private RoleEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(RoleEntityMapper.class);
    }

    @Test
    @DisplayName("Should map RoleJpaEntity to RoleAggregate domain model")
    void shouldMapEntityToDomain() {
        // Given
        UUID roleId = UUID.randomUUID();
        Instant now = Instant.now();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(now, ZoneOffset.UTC);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        RoleJpaEntity entity = RoleJpaEntity.builder()
                .roleId(roleId)
                .roleName("admin")
                .displayName("Administrator")
                .description("Administrator role")
                .roleType("SYSTEM")
                .metadata(metadata)
                .createdAt(now)
                .lastModifiedAt(now)
                .version(1L)
                .build();

        // When
        RoleAggregate domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNotNull(domain.getId());
        assertEquals(roleId, domain.getId().getValue());
        assertEquals("admin", domain.getRoleName());
        assertEquals("Administrator", domain.getDisplayName());
        assertEquals("Administrator role", domain.getDescription());
        assertEquals(RoleType.SYSTEM, domain.getRoleType());
        assertEquals(metadata, domain.getMetadata());
        assertEquals(localDateTime, domain.getCreatedAt());
        assertEquals(localDateTime, domain.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map RoleAggregate domain model to RoleJpaEntity")
    void shouldMapDomainToEntity() {
        // Given
        RoleId roleId = RoleId.generate();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        RoleAggregate domain = RoleAggregate.builder()
                .id(roleId)
                .roleName("admin")
                .displayName("Administrator")
                .description("Administrator role")
                .roleType(RoleType.SYSTEM)
                .metadata(metadata)
                .createdAt(now)
                .updatedAt(now)
                .version(1L)
                .build();

        // When
        RoleJpaEntity entity = mapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertEquals(roleId.getValue(), entity.getRoleId());
        assertEquals("admin", entity.getRoleName());
        assertEquals("Administrator", entity.getDisplayName());
        assertEquals("Administrator role", entity.getDescription());
        assertEquals("SYSTEM", entity.getRoleType());
        assertEquals(metadata, entity.getMetadata());
        assertEquals(now.toInstant(ZoneOffset.UTC), entity.getCreatedAt());
        assertEquals(now.toInstant(ZoneOffset.UTC), entity.getLastModifiedAt());
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        RoleJpaEntity entity = RoleJpaEntity.builder()
                .roleId(UUID.randomUUID())
                .roleName("admin")
                .displayName(null)
                .description(null)
                .roleType("SYSTEM")
                .metadata(null)
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .version(0L)
                .build();

        // When
        RoleAggregate domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNull(domain.getDisplayName());
        assertNull(domain.getDescription());
        // MapStruct creates empty map instead of null for metadata
        assertTrue(domain.getMetadata() == null || domain.getMetadata().isEmpty());
    }

    @Test
    @DisplayName("Should map all RoleType enum values correctly")
    void shouldMapAllRoleTypeValues() {
        // Test all enum values
        for (RoleType roleType : RoleType.values()) {
            // Given
            RoleJpaEntity entity = RoleJpaEntity.builder()
                    .roleId(UUID.randomUUID())
                    .roleName("testrole")
                    .displayName("Test Role")
                    .roleType(roleType.name())
                    .createdAt(Instant.now())
                    .lastModifiedAt(Instant.now())
                    .version(0L)
                    .build();

            // When
            RoleAggregate domain = mapper.toDomain(entity);

            // Then
            assertEquals(roleType, domain.getRoleType());

            // Reverse mapping
            RoleJpaEntity mappedEntity = mapper.toEntity(domain);
            assertEquals(roleType.name(), mappedEntity.getRoleType());
        }
    }

    @Test
    @DisplayName("Should convert timestamps correctly between Instant and LocalDateTime")
    void shouldConvertTimestampsCorrectly() {
        // Given
        Instant instant = Instant.now();
        LocalDateTime expectedLocalDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        
        RoleJpaEntity entity = RoleJpaEntity.builder()
                .roleId(UUID.randomUUID())
                .roleName("admin")
                .displayName("Administrator")
                .roleType("SYSTEM")
                .createdAt(instant)
                .lastModifiedAt(instant)
                .version(0L)
                .build();

        // When
        RoleAggregate domain = mapper.toDomain(entity);

        // Then
        assertEquals(expectedLocalDateTime, domain.getCreatedAt());
        assertEquals(expectedLocalDateTime, domain.getUpdatedAt());

        // Reverse mapping
        RoleJpaEntity mappedEntity = mapper.toEntity(domain);
        assertEquals(instant, mappedEntity.getCreatedAt());
        assertEquals(instant, mappedEntity.getLastModifiedAt());
    }

    @Test
    @DisplayName("Should perform round-trip mapping correctly")
    void shouldPerformRoundTripMapping() {
        // Given
        RoleId roleId = RoleId.generate();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        RoleAggregate originalDomain = RoleAggregate.builder()
                .id(roleId)
                .roleName("admin")
                .displayName("Administrator")
                .description("Administrator role")
                .roleType(RoleType.SYSTEM)
                .metadata(metadata)
                .createdAt(now)
                .updatedAt(now)
                .version(1L)
                .build();

        // When
        RoleJpaEntity entity = mapper.toEntity(originalDomain);
        RoleAggregate mappedDomain = mapper.toDomain(entity);

        // Then
        assertEquals(originalDomain.getId().getValue(), mappedDomain.getId().getValue());
        assertEquals(originalDomain.getRoleName(), mappedDomain.getRoleName());
        assertEquals(originalDomain.getDisplayName(), mappedDomain.getDisplayName());
        assertEquals(originalDomain.getDescription(), mappedDomain.getDescription());
        assertEquals(originalDomain.getRoleType(), mappedDomain.getRoleType());
        assertEquals(originalDomain.getMetadata(), mappedDomain.getMetadata());
    }
}

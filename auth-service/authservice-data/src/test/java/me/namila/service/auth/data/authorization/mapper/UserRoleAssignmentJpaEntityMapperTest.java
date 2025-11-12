package me.namila.service.auth.data.authorization.mapper;

import me.namila.service.auth.data.authorization.entity.RoleJpaEntity;
import me.namila.service.auth.data.authorization.entity.UserRoleAssignmentJpaEntity;
import me.namila.service.auth.data.identity.entity.UserJpaEntity;
import me.namila.service.auth.domain.core.authorization.model.UserRoleAssignmentAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.RoleId;
import me.namila.service.auth.domain.core.authorization.model.id.UserRoleAssignmentId;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentScope;
import me.namila.service.auth.domain.core.authorization.valueobject.AssignmentStatus;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserRoleAssignmentEntityMapper.
 */
@DisplayName("UserRoleAssignmentEntityMapper Tests")
class UserRoleAssignmentJpaEntityMapperTest
{

    private UserRoleAssignmentEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserRoleAssignmentEntityMapper.class);
    }

    @Test
    @DisplayName("Should map UserRoleAssignmentJpaEntity to UserRoleAssignmentAggregate domain model")
    void shouldMapEntityToDomain() {
        // Given
        UUID assignmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID assignedBy = UUID.randomUUID();
        Instant now = Instant.now();

        UserJpaEntity userJpaEntity = UserJpaEntity.builder()
                .userId(userId)
                .username("testuser")
                .email("test@example.com")
                .status("ACTIVE")
                .createdAt(now)
                .lastModifiedAt(now)
                .version(0L)
                .build();

        RoleJpaEntity roleJpaEntity = RoleJpaEntity.builder()
                .roleId(roleId)
                .roleName("admin")
                .displayName("Administrator")
                .roleType("SYSTEM")
                .createdAt(now)
                .lastModifiedAt(now)
                .version(0L)
                .build();

        UserRoleAssignmentJpaEntity entity = UserRoleAssignmentJpaEntity.builder()
                .assignmentId(assignmentId)
                .userId(userId)  // Set FK column
                .user(userJpaEntity)  // Relationship for queries
                .roleId(roleId)  // Set FK column
                .role(roleJpaEntity)  // Relationship for queries
                .scope("GLOBAL")
                .scopeContext("tenant123")
                .effectiveFrom(now)
                .effectiveUntil(now.plusSeconds(86400))
                .assignedBy(assignedBy)
                .assignedAt(now)
                .status("ACTIVE")
                .version(1L)
                .build();

        // When
        UserRoleAssignmentAggregate domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNotNull(domain.getId());
        assertEquals(assignmentId, domain.getId().getValue());
        assertNotNull(domain.getUserId());
        assertEquals(userId, domain.getUserId().getValue());
        assertNotNull(domain.getRoleId());
        assertEquals(roleId, domain.getRoleId().getValue());
        assertEquals(AssignmentScope.GLOBAL, domain.getScope());
        assertEquals("tenant123", domain.getScopeContext());
        assertEquals(now, domain.getEffectiveFrom());
        assertEquals(now.plusSeconds(86400), domain.getEffectiveUntil());
        assertNotNull(domain.getAssignedBy());
        assertEquals(assignedBy, domain.getAssignedBy().getValue());
        assertEquals(now, domain.getAssignedAt());
        assertEquals(AssignmentStatus.ACTIVE, domain.getStatus());
        assertEquals(1L, domain.getVersion());
    }

    @Test
    @DisplayName("Should map UserRoleAssignmentAggregate domain model to UserRoleAssignmentJpaEntity")
    void shouldMapDomainToEntity() {
        // Given
        UserRoleAssignmentId assignmentId = UserRoleAssignmentId.generate();
        UserId userId = UserId.generate();
        RoleId roleId = RoleId.generate();
        UserId assignedBy = UserId.generate();
        Instant now = Instant.now();

        UserRoleAssignmentAggregate domain = UserRoleAssignmentAggregate.builder()
                .id(assignmentId)
                .userId(userId)
                .roleId(roleId)
                .scope(AssignmentScope.GLOBAL)
                .scopeContext("tenant123")
                .effectiveFrom(now)
                .effectiveUntil(now.plusSeconds(86400))
                .assignedBy(assignedBy)
                .assignedAt(now)
                .status(AssignmentStatus.ACTIVE)
                .version(1L)
                .build();

        // When
        UserRoleAssignmentJpaEntity entity = mapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertEquals(assignmentId.getValue(), entity.getAssignmentId());
        assertEquals("GLOBAL", entity.getScope());
        assertEquals("tenant123", entity.getScopeContext());
        assertEquals(now, entity.getEffectiveFrom());
        assertEquals(now.plusSeconds(86400), entity.getEffectiveUntil());
        assertEquals(assignedBy.getValue(), entity.getAssignedBy());
        assertEquals(now, entity.getAssignedAt());
        assertEquals("ACTIVE", entity.getStatus());
        assertEquals(1L, entity.getVersion());
        // user and role are ignored in mapping
        assertNull(entity.getUser());
        assertNull(entity.getRole());
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        UserRoleAssignmentJpaEntity entity = UserRoleAssignmentJpaEntity.builder()
                .assignmentId(UUID.randomUUID())
                .user(null)
                .role(null)
                .scope("GLOBAL")
                .scopeContext(null)
                .effectiveFrom(Instant.now())
                .effectiveUntil(null)
                .assignedBy(UUID.randomUUID())
                .assignedAt(Instant.now())
                .status("ACTIVE")
                .version(0L)
                .build();

        // When
        UserRoleAssignmentAggregate domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNull(domain.getScopeContext());
        assertNull(domain.getEffectiveUntil());
    }

    @Test
    @DisplayName("Should map all AssignmentScope enum values correctly")
    void shouldMapAllAssignmentScopeValues() {
        for (AssignmentScope scope : AssignmentScope.values()) {
            // Given
            UserRoleAssignmentJpaEntity entity = UserRoleAssignmentJpaEntity.builder()
                    .assignmentId(UUID.randomUUID())
                    .user(null)
                    .role(null)
                    .scope(scope.name())
                    .effectiveFrom(Instant.now())
                    .assignedBy(UUID.randomUUID())
                    .assignedAt(Instant.now())
                    .status("ACTIVE")
                    .version(0L)
                    .build();

            // When
            UserRoleAssignmentAggregate domain = mapper.toDomain(entity);

            // Then
            assertEquals(scope, domain.getScope());

            // Reverse mapping
            UserRoleAssignmentJpaEntity mappedEntity = mapper.toEntity(domain);
            assertEquals(scope.name(), mappedEntity.getScope());
        }
    }

    @Test
    @DisplayName("Should map all AssignmentStatus enum values correctly")
    void shouldMapAllAssignmentStatusValues() {
        for (AssignmentStatus status : AssignmentStatus.values()) {
            // Given
            UserRoleAssignmentJpaEntity entity = UserRoleAssignmentJpaEntity.builder()
                    .assignmentId(UUID.randomUUID())
                    .user(null)
                    .role(null)
                    .scope("GLOBAL")
                    .effectiveFrom(Instant.now())
                    .assignedBy(UUID.randomUUID())
                    .assignedAt(Instant.now())
                    .status(status.name())
                    .version(0L)
                    .build();

            // When
            UserRoleAssignmentAggregate domain = mapper.toDomain(entity);

            // Then
            assertEquals(status, domain.getStatus());

            // Reverse mapping
            UserRoleAssignmentJpaEntity mappedEntity = mapper.toEntity(domain);
            assertEquals(status.name(), mappedEntity.getStatus());
        }
    }

    @Test
    @DisplayName("Should perform round-trip mapping correctly")
    void shouldPerformRoundTripMapping() {
        // Given
        UserRoleAssignmentId assignmentId = UserRoleAssignmentId.generate();
        UserId userId = UserId.generate();
        RoleId roleId = RoleId.generate();
        UserId assignedBy = UserId.generate();
        Instant now = Instant.now();

        UserRoleAssignmentAggregate originalDomain = UserRoleAssignmentAggregate.builder()
                .id(assignmentId)
                .userId(userId)
                .roleId(roleId)
                .scope(AssignmentScope.GLOBAL)
                .scopeContext("tenant123")
                .effectiveFrom(now)
                .effectiveUntil(now.plusSeconds(86400))
                .assignedBy(assignedBy)
                .assignedAt(now)
                .status(AssignmentStatus.ACTIVE)
                .version(1L)
                .build();

        // When
        UserRoleAssignmentJpaEntity entity = mapper.toEntity(originalDomain);
        UserRoleAssignmentAggregate mappedDomain = mapper.toDomain(entity);

        // Then - Round-trip should preserve all ID references
        assertEquals(originalDomain.getId().getValue(), mappedDomain.getId().getValue());
        // FK columns are now properly mapped, so IDs should be preserved
        assertNotNull(mappedDomain.getUserId());
        assertEquals(originalDomain.getUserId().getValue(), mappedDomain.getUserId().getValue());
        assertNotNull(mappedDomain.getRoleId());
        assertEquals(originalDomain.getRoleId().getValue(), mappedDomain.getRoleId().getValue());
        assertNotNull(mappedDomain.getAssignedBy());
        assertEquals(originalDomain.getAssignedBy().getValue(), mappedDomain.getAssignedBy().getValue());
        assertEquals(originalDomain.getScope(), mappedDomain.getScope());
        assertEquals(originalDomain.getScopeContext(), mappedDomain.getScopeContext());
        assertEquals(originalDomain.getStatus(), mappedDomain.getStatus());
    }
}

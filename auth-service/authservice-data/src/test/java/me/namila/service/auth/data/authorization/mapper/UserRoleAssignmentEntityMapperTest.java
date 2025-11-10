package me.namila.service.auth.data.authorization.mapper;

import me.namila.service.auth.data.authorization.entity.RoleEntity;
import me.namila.service.auth.data.authorization.entity.UserRoleAssignmentEntity;
import me.namila.service.auth.data.identity.entity.UserEntity;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UserRoleAssignmentEntityMapper.
 */
@DisplayName("UserRoleAssignmentEntityMapper Tests")
class UserRoleAssignmentEntityMapperTest {

    private UserRoleAssignmentEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserRoleAssignmentEntityMapper.class);
    }

    @Test
    @DisplayName("Should map UserRoleAssignmentEntity to UserRoleAssignmentAggregate domain model")
    void shouldMapEntityToDomain() {
        // Given
        UUID assignmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID assignedBy = UUID.randomUUID();
        Instant now = Instant.now();

        UserEntity userEntity = UserEntity.builder()
                .userId(userId)
                .username("testuser")
                .email("test@example.com")
                .status("ACTIVE")
                .createdAt(now)
                .lastModifiedAt(now)
                .version(0L)
                .build();

        RoleEntity roleEntity = RoleEntity.builder()
                .roleId(roleId)
                .roleName("admin")
                .displayName("Administrator")
                .roleType("SYSTEM")
                .createdAt(now)
                .lastModifiedAt(now)
                .version(0L)
                .build();

        UserRoleAssignmentEntity entity = UserRoleAssignmentEntity.builder()
                .assignmentId(assignmentId)
                .user(userEntity)
                .role(roleEntity)
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
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isNotNull();
        assertThat(domain.getId().getValue()).isEqualTo(assignmentId);
        assertThat(domain.getUserId()).isNotNull();
        assertThat(domain.getUserId().getValue()).isEqualTo(userId);
        assertThat(domain.getRoleId()).isNotNull();
        assertThat(domain.getRoleId().getValue()).isEqualTo(roleId);
        assertThat(domain.getScope()).isEqualTo(AssignmentScope.GLOBAL);
        assertThat(domain.getScopeContext()).isEqualTo("tenant123");
        assertThat(domain.getEffectiveFrom()).isEqualTo(now);
        assertThat(domain.getEffectiveUntil()).isEqualTo(now.plusSeconds(86400));
        assertThat(domain.getAssignedBy()).isNotNull();
        assertThat(domain.getAssignedBy().getValue()).isEqualTo(assignedBy);
        assertThat(domain.getAssignedAt()).isEqualTo(now);
        assertThat(domain.getStatus()).isEqualTo(AssignmentStatus.ACTIVE);
        assertThat(domain.getVersion()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should map UserRoleAssignmentAggregate domain model to UserRoleAssignmentEntity")
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
        UserRoleAssignmentEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getAssignmentId()).isEqualTo(assignmentId.getValue());
        assertThat(entity.getScope()).isEqualTo("GLOBAL");
        assertThat(entity.getScopeContext()).isEqualTo("tenant123");
        assertThat(entity.getEffectiveFrom()).isEqualTo(now);
        assertThat(entity.getEffectiveUntil()).isEqualTo(now.plusSeconds(86400));
        assertThat(entity.getAssignedBy()).isEqualTo(assignedBy.getValue());
        assertThat(entity.getAssignedAt()).isEqualTo(now);
        assertThat(entity.getStatus()).isEqualTo("ACTIVE");
        assertThat(entity.getVersion()).isEqualTo(1L);
        // user and role are ignored in mapping
        assertThat(entity.getUser()).isNull();
        assertThat(entity.getRole()).isNull();
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        UserRoleAssignmentEntity entity = UserRoleAssignmentEntity.builder()
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
        assertThat(domain).isNotNull();
        assertThat(domain.getScopeContext()).isNull();
        assertThat(domain.getEffectiveUntil()).isNull();
    }

    @Test
    @DisplayName("Should map all AssignmentScope enum values correctly")
    void shouldMapAllAssignmentScopeValues() {
        for (AssignmentScope scope : AssignmentScope.values()) {
            // Given
            UserRoleAssignmentEntity entity = UserRoleAssignmentEntity.builder()
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
            assertThat(domain.getScope()).isEqualTo(scope);

            // Reverse mapping
            UserRoleAssignmentEntity mappedEntity = mapper.toEntity(domain);
            assertThat(mappedEntity.getScope()).isEqualTo(scope.name());
        }
    }

    @Test
    @DisplayName("Should map all AssignmentStatus enum values correctly")
    void shouldMapAllAssignmentStatusValues() {
        for (AssignmentStatus status : AssignmentStatus.values()) {
            // Given
            UserRoleAssignmentEntity entity = UserRoleAssignmentEntity.builder()
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
            assertThat(domain.getStatus()).isEqualTo(status);

            // Reverse mapping
            UserRoleAssignmentEntity mappedEntity = mapper.toEntity(domain);
            assertThat(mappedEntity.getStatus()).isEqualTo(status.name());
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
        UserRoleAssignmentEntity entity = mapper.toEntity(originalDomain);
        UserRoleAssignmentAggregate mappedDomain = mapper.toDomain(entity);

        // Then
        assertThat(mappedDomain.getId().getValue()).isEqualTo(originalDomain.getId().getValue());
        assertThat(mappedDomain.getUserId().getValue()).isEqualTo(originalDomain.getUserId().getValue());
        assertThat(mappedDomain.getRoleId().getValue()).isEqualTo(originalDomain.getRoleId().getValue());
        assertThat(mappedDomain.getScope()).isEqualTo(originalDomain.getScope());
        assertThat(mappedDomain.getScopeContext()).isEqualTo(originalDomain.getScopeContext());
        assertThat(mappedDomain.getStatus()).isEqualTo(originalDomain.getStatus());
    }
}


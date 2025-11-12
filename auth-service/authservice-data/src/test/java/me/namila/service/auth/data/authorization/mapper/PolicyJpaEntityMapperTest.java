package me.namila.service.auth.data.authorization.mapper;

import me.namila.service.auth.data.authorization.entity.PolicyJpaEntity;
import me.namila.service.auth.domain.core.authorization.model.PolicyAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.PolicyId;
import me.namila.service.auth.domain.core.authorization.valueobject.Effect;
import me.namila.service.auth.domain.core.authorization.valueobject.PolicyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PolicyEntityMapper.
 */
@DisplayName("PolicyEntityMapper Tests")
class PolicyJpaEntityMapperTest
{

    private PolicyEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PolicyEntityMapper.class);
    }

    @Test
    @DisplayName("Should map PolicyJpaEntity to PolicyAggregate domain model")
    void shouldMapEntityToDomain() {
        // Given
        UUID policyId = UUID.randomUUID();
        Instant now = Instant.now();
        Map<String, Object> subjects = new HashMap<>();
        subjects.put("users", List.of("user1", "user2"));
        Map<String, Object> resources = new HashMap<>();
        resources.put("type", "document");
        List<String> actions = Arrays.asList("read", "write");
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("time", "9-17");

        PolicyJpaEntity entity = PolicyJpaEntity.builder()
                .policyId(policyId)
                .policyName("test-policy")
                .policyType("ABAC")
                .effect("PERMIT")
                .subjects(subjects)
                .resources(resources)
                .actions(actions)
                .conditions(conditions)
                .priority(10)
                .enabled(true)
                .version(1)
                .createdAt(now)
                .lastModifiedAt(now)
                .build();

        // When
        PolicyAggregate domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNotNull(domain.getId());
        assertEquals(policyId, domain.getId().getValue());
        assertEquals("test-policy", domain.getPolicyName());
        assertEquals(PolicyType.ABAC, domain.getPolicyType());
        assertEquals(Effect.PERMIT, domain.getEffect());
        assertEquals(subjects, domain.getSubjects());
        assertEquals(resources, domain.getResources());
        assertTrue(domain.getActions().containsAll(actions) && actions.containsAll(domain.getActions()));
        assertEquals(conditions, domain.getConditions());
        assertEquals(10, domain.getPriority());
        assertTrue(domain.getEnabled());
        assertEquals(1, domain.getVersion());
        assertEquals(LocalDateTime.ofInstant(now, ZoneOffset.UTC), domain.getCreatedAt());
        assertEquals(LocalDateTime.ofInstant(now, ZoneOffset.UTC), domain.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map PolicyAggregate domain model to PolicyJpaEntity")
    void shouldMapDomainToEntity() {
        // Given
        PolicyId policyId = PolicyId.generate();
        Map<String, Object> subjects = new HashMap<>();
        subjects.put("users", List.of("user1", "user2"));
        Map<String, Object> resources = new HashMap<>();
        resources.put("type", "document");
        Set<String> actions = Set.of("read", "write");
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("time", "9-17");

        LocalDateTime now = LocalDateTime.now();
        PolicyAggregate domain = PolicyAggregate.builder()
                .id(policyId)
                .policyName("test-policy")
                .policyType(PolicyType.ABAC)
                .effect(Effect.PERMIT)
                .subjects(subjects)
                .resources(resources)
                .actions(actions)
                .conditions(conditions)
                .priority(10)
                .enabled(true)
                .version(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        PolicyJpaEntity entity = mapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertEquals(policyId.getValue(), entity.getPolicyId());
        assertEquals("test-policy", entity.getPolicyName());
        assertEquals("ABAC", entity.getPolicyType());
        assertEquals("PERMIT", entity.getEffect());
        assertEquals(subjects, entity.getSubjects());
        assertEquals(resources, entity.getResources());
        assertTrue(entity.getActions().containsAll(actions) && actions.containsAll(entity.getActions()));
        assertEquals(conditions, entity.getConditions());
        assertEquals(10, entity.getPriority());
        assertTrue(entity.getEnabled());
        assertEquals(1, entity.getVersion());
        // Note: createdAt and lastModifiedAt are set from domain's createdAt/updatedAt (LocalDateTime)
        // which are converted to Instant
    }
    
    @Test
    @DisplayName("Should convert timestamps correctly between Instant and LocalDateTime")
    void shouldConvertTimestampsCorrectly() {
        // Given
        Instant instant = Instant.now();
        LocalDateTime expectedLocalDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        
        PolicyJpaEntity entity = PolicyJpaEntity.builder()
                .policyId(UUID.randomUUID())
                .policyName("test-policy")
                .policyType("ABAC")
                .effect("PERMIT")
                .priority(0)
                .enabled(true)
                .version(1)
                .createdAt(instant)
                .lastModifiedAt(instant)
                .build();

        // When
        PolicyAggregate domain = mapper.toDomain(entity);

        // Then
        assertEquals(expectedLocalDateTime, domain.getCreatedAt());
        assertEquals(expectedLocalDateTime, domain.getUpdatedAt());

        // Reverse mapping
        PolicyJpaEntity mappedEntity = mapper.toEntity(domain);
        assertEquals(instant, mappedEntity.getCreatedAt());
        assertEquals(instant, mappedEntity.getLastModifiedAt());
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        PolicyJpaEntity entity = PolicyJpaEntity.builder()
                .policyId(UUID.randomUUID())
                .policyName("test-policy")
                .policyType("ABAC")
                .effect("PERMIT")
                .subjects(null)
                .resources(null)
                .actions(null)
                .conditions(null)
                .priority(0)
                .enabled(true)
                .version(1)
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .build();

        // When
        PolicyAggregate domain = mapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNotNull(domain.getSubjects());
        assertNotNull(domain.getResources());
        assertNotNull(domain.getActions());
        assertNotNull(domain.getConditions());
    }

    @Test
    @DisplayName("Should convert List to Set for actions")
    void shouldConvertListToSetForActions() {
        // Given
        List<String> actionsList = Arrays.asList("read", "write", "read"); // duplicate
        PolicyJpaEntity entity = PolicyJpaEntity.builder()
                .policyId(UUID.randomUUID())
                .policyName("test-policy")
                .policyType("ABAC")
                .effect("PERMIT")
                .actions(actionsList)
                .priority(0)
                .enabled(true)
                .version(1)
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .build();

        // When
        PolicyAggregate domain = mapper.toDomain(entity);

        // Then
        assertInstanceOf(Set.class, domain.getActions());
        assertEquals(2, domain.getActions().size()); // duplicates removed
        assertTrue(domain.getActions().containsAll(Set.of("read", "write")));
    }

    @Test
    @DisplayName("Should convert Set to List for actions")
    void shouldConvertSetToListForActions() {
        // Given
        Set<String> actionsSet = Set.of("read", "write");
        PolicyAggregate domain = PolicyAggregate.builder()
                .id(PolicyId.generate())
                .policyName("test-policy")
                .policyType(PolicyType.ABAC)
                .effect(Effect.PERMIT)
                .actions(actionsSet)
                .priority(0)
                .enabled(true)
                .version(1)
                .build();

        // When
        PolicyJpaEntity entity = mapper.toEntity(domain);

        // Then
        assertInstanceOf(List.class, entity.getActions());
        assertTrue(entity.getActions().containsAll(actionsSet) && actionsSet.containsAll(entity.getActions()));
    }

    @Test
    @DisplayName("Should map all PolicyType enum values correctly")
    void shouldMapAllPolicyTypeValues() {
        for (PolicyType policyType : PolicyType.values()) {
            // Given
            PolicyJpaEntity entity = PolicyJpaEntity.builder()
                    .policyId(UUID.randomUUID())
                    .policyName("test-policy")
                    .policyType(policyType.name())
                    .effect("PERMIT")
                    .priority(0)
                    .enabled(true)
                    .version(1)
                    .createdAt(Instant.now())
                    .lastModifiedAt(Instant.now())
                    .build();

            // When
            PolicyAggregate domain = mapper.toDomain(entity);

            // Then
            assertEquals(policyType, domain.getPolicyType());

            // Reverse mapping
            PolicyJpaEntity mappedEntity = mapper.toEntity(domain);
            assertEquals(policyType.name(), mappedEntity.getPolicyType());
        }
    }

    @Test
    @DisplayName("Should map all Effect enum values correctly")
    void shouldMapAllEffectValues() {
        for (Effect effect : Effect.values()) {
            // Given
            PolicyJpaEntity entity = PolicyJpaEntity.builder()
                    .policyId(UUID.randomUUID())
                    .policyName("test-policy")
                    .policyType("ABAC")
                    .effect(effect.name())
                    .priority(0)
                    .enabled(true)
                    .version(1)
                    .createdAt(Instant.now())
                    .lastModifiedAt(Instant.now())
                    .build();

            // When
            PolicyAggregate domain = mapper.toDomain(entity);

            // Then
            assertEquals(effect, domain.getEffect());

            // Reverse mapping
            PolicyJpaEntity mappedEntity = mapper.toEntity(domain);
            assertEquals(effect.name(), mappedEntity.getEffect());
        }
    }

    @Test
    @DisplayName("Should perform round-trip mapping correctly")
    void shouldPerformRoundTripMapping() {
        // Given
        PolicyId policyId = PolicyId.generate();
        Map<String, Object> subjects = new HashMap<>();
        subjects.put("users", List.of("user1"));
        Set<String> actions = Set.of("read");

        PolicyAggregate originalDomain = PolicyAggregate.builder()
                .id(policyId)
                .policyName("test-policy")
                .policyType(PolicyType.ABAC)
                .effect(Effect.PERMIT)
                .subjects(subjects)
                .actions(actions)
                .priority(10)
                .enabled(true)
                .version(1)
                .build();

        // When
        PolicyJpaEntity entity = mapper.toEntity(originalDomain);
        PolicyAggregate mappedDomain = mapper.toDomain(entity);

        // Then
        assertEquals(originalDomain.getId().getValue(), mappedDomain.getId().getValue());
        assertEquals(originalDomain.getPolicyName(), mappedDomain.getPolicyName());
        assertEquals(originalDomain.getPolicyType(), mappedDomain.getPolicyType());
        assertEquals(originalDomain.getEffect(), mappedDomain.getEffect());
        assertEquals(originalDomain.getActions(), mappedDomain.getActions());
    }
}


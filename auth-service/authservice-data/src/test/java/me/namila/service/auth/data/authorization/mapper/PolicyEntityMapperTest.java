package me.namila.service.auth.data.authorization.mapper;

import me.namila.service.auth.data.authorization.entity.PolicyEntity;
import me.namila.service.auth.domain.core.authorization.model.PolicyAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.PolicyId;
import me.namila.service.auth.domain.core.authorization.valueobject.Effect;
import me.namila.service.auth.domain.core.authorization.valueobject.PolicyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PolicyEntityMapper.
 */
@DisplayName("PolicyEntityMapper Tests")
class PolicyEntityMapperTest {

    private PolicyEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PolicyEntityMapper.class);
    }

    @Test
    @DisplayName("Should map PolicyEntity to PolicyAggregate domain model")
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

        PolicyEntity entity = PolicyEntity.builder()
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
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isNotNull();
        assertThat(domain.getId().getValue()).isEqualTo(policyId);
        assertThat(domain.getPolicyName()).isEqualTo("test-policy");
        assertThat(domain.getPolicyType()).isEqualTo(PolicyType.ABAC);
        assertThat(domain.getEffect()).isEqualTo(Effect.PERMIT);
        assertThat(domain.getSubjects()).isEqualTo(subjects);
        assertThat(domain.getResources()).isEqualTo(resources);
        assertThat(domain.getActions()).containsExactlyInAnyOrderElementsOf(actions);
        assertThat(domain.getConditions()).isEqualTo(conditions);
        assertThat(domain.getPriority()).isEqualTo(10);
        assertThat(domain.getEnabled()).isTrue();
        assertThat(domain.getVersion()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should map PolicyAggregate domain model to PolicyEntity")
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
                .build();

        // When
        PolicyEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getPolicyId()).isEqualTo(policyId.getValue());
        assertThat(entity.getPolicyName()).isEqualTo("test-policy");
        assertThat(entity.getPolicyType()).isEqualTo("ABAC");
        assertThat(entity.getEffect()).isEqualTo("PERMIT");
        assertThat(entity.getSubjects()).isEqualTo(subjects);
        assertThat(entity.getResources()).isEqualTo(resources);
        assertThat(entity.getActions()).containsExactlyInAnyOrderElementsOf(actions);
        assertThat(entity.getConditions()).isEqualTo(conditions);
        assertThat(entity.getPriority()).isEqualTo(10);
        assertThat(entity.getEnabled()).isTrue();
        assertThat(entity.getVersion()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        PolicyEntity entity = PolicyEntity.builder()
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
        assertThat(domain).isNotNull();
        assertThat(domain.getSubjects()).isNotNull();
        assertThat(domain.getResources()).isNotNull();
        assertThat(domain.getActions()).isNotNull();
        assertThat(domain.getConditions()).isNotNull();
    }

    @Test
    @DisplayName("Should convert List to Set for actions")
    void shouldConvertListToSetForActions() {
        // Given
        List<String> actionsList = Arrays.asList("read", "write", "read"); // duplicate
        PolicyEntity entity = PolicyEntity.builder()
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
        assertThat(domain.getActions()).isInstanceOf(Set.class);
        assertThat(domain.getActions()).hasSize(2); // duplicates removed
        assertThat(domain.getActions()).containsExactlyInAnyOrder("read", "write");
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
        PolicyEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity.getActions()).isInstanceOf(List.class);
        assertThat(entity.getActions()).containsExactlyInAnyOrderElementsOf(actionsSet);
    }

    @Test
    @DisplayName("Should map all PolicyType enum values correctly")
    void shouldMapAllPolicyTypeValues() {
        for (PolicyType policyType : PolicyType.values()) {
            // Given
            PolicyEntity entity = PolicyEntity.builder()
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
            assertThat(domain.getPolicyType()).isEqualTo(policyType);

            // Reverse mapping
            PolicyEntity mappedEntity = mapper.toEntity(domain);
            assertThat(mappedEntity.getPolicyType()).isEqualTo(policyType.name());
        }
    }

    @Test
    @DisplayName("Should map all Effect enum values correctly")
    void shouldMapAllEffectValues() {
        for (Effect effect : Effect.values()) {
            // Given
            PolicyEntity entity = PolicyEntity.builder()
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
            assertThat(domain.getEffect()).isEqualTo(effect);

            // Reverse mapping
            PolicyEntity mappedEntity = mapper.toEntity(domain);
            assertThat(mappedEntity.getEffect()).isEqualTo(effect.name());
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
        PolicyEntity entity = mapper.toEntity(originalDomain);
        PolicyAggregate mappedDomain = mapper.toDomain(entity);

        // Then
        assertThat(mappedDomain.getId().getValue()).isEqualTo(originalDomain.getId().getValue());
        assertThat(mappedDomain.getPolicyName()).isEqualTo(originalDomain.getPolicyName());
        assertThat(mappedDomain.getPolicyType()).isEqualTo(originalDomain.getPolicyType());
        assertThat(mappedDomain.getEffect()).isEqualTo(originalDomain.getEffect());
        assertThat(mappedDomain.getActions()).isEqualTo(originalDomain.getActions());
    }
}


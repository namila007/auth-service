package me.namila.service.auth.data.configuration.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.namila.service.auth.data.configuration.entity.OIDCProviderConfigEntity;
import me.namila.service.auth.domain.core.configuration.model.*;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.configuration.valueobject.ProviderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for OIDCProviderConfigEntityMapper.
 */
@DisplayName("OIDCProviderConfigEntityMapper Tests")
@SpringBootTest(classes = {OIDCProviderConfigEntityMapper.class})
class OIDCProviderConfigEntityMapperTest {

    @Autowired
    private OIDCProviderConfigEntityMapper mapper;

    @BeforeEach
    void setUp() {
        // Inject ObjectMapper if not already injected
        if (mapper != null) {
            ReflectionTestUtils.setField(mapper, "objectMapper", new ObjectMapper());
        }
    }

    @Test
    @DisplayName("Should map OIDCProviderConfigEntity to OIDCProviderConfigAggregate domain model")
    void shouldMapEntityToDomain() {
        // Given
        UUID providerId = UUID.randomUUID();
        Instant now = Instant.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        // Create JSON maps for complex objects
        Map<String, Object> oidcConfigMap = new HashMap<>();
        oidcConfigMap.put("issuerUri", "https://example.com");
        oidcConfigMap.put("clientId", "client123");
        oidcConfigMap.put("clientSecret", "secret123");

        Map<String, Object> attributeMappingMap = new HashMap<>();
        attributeMappingMap.put("subjectAttribute", "sub");
        attributeMappingMap.put("emailAttribute", "email");

        Map<String, Object> roleMappingMap = new HashMap<>();
        roleMappingMap.put("mappingStrategy", "EXPLICIT");

        Map<String, Object> jitProvisioningMap = new HashMap<>();
        jitProvisioningMap.put("enabled", true);
        jitProvisioningMap.put("createUsers", true);

        OIDCProviderConfigEntity entity = OIDCProviderConfigEntity.builder()
                .providerId(providerId)
                .providerName("test-provider")
                .providerType("OIDC")
                .enabled(true)
                .displayName("Test Provider")
                .configuration(oidcConfigMap)
                .attributeMapping(attributeMappingMap)
                .roleMapping(roleMappingMap)
                .jitProvisioning(jitProvisioningMap)
                .metadata(metadata)
                .createdAt(now)
                .lastModifiedAt(now)
                .version(1L)
                .build();

        // When
        OIDCProviderConfigAggregate domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isNotNull();
        assertThat(domain.getId().getValue()).isEqualTo(providerId);
        assertThat(domain.getProviderName()).isEqualTo("test-provider");
        assertThat(domain.getProviderType()).isEqualTo(ProviderType.OIDC);
        assertThat(domain.getEnabled()).isTrue();
        assertThat(domain.getDisplayName()).isEqualTo("Test Provider");
        assertThat(domain.getConfiguration()).isNotNull();
        assertThat(domain.getAttributeMapping()).isNotNull();
        assertThat(domain.getRoleMapping()).isNotNull();
        assertThat(domain.getJitProvisioning()).isNotNull();
        assertThat(domain.getMetadata()).isEqualTo(metadata);
        assertThat(domain.getVersion()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should map OIDCProviderConfigAggregate domain model to OIDCProviderConfigEntity")
    void shouldMapDomainToEntity() {
        // Given
        OIDCProviderConfigId providerId = OIDCProviderConfigId.generate();
        Instant now = Instant.now();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        OIDCConfiguration oidcConfig = OIDCConfiguration.builder()
                .issuerUri("https://example.com")
                .clientId("client123")
                .clientSecret("secret123")
                .build();

        AttributeMappingConfig attributeMapping = AttributeMappingConfig.builder()
                .subjectAttribute("sub")
                .emailAttribute("email")
                .build();

        RoleMappingConfig roleMapping = RoleMappingConfig.builder()
                .build();

        JITProvisioningConfig jitProvisioning = JITProvisioningConfig.builder()
                .enabled(true)
                .createUsers(true)
                .build();

        OIDCProviderConfigAggregate domain = OIDCProviderConfigAggregate.builder()
                .id(providerId)
                .providerName("test-provider")
                .providerType(ProviderType.OIDC)
                .enabled(true)
                .displayName("Test Provider")
                .configuration(oidcConfig)
                .attributeMapping(attributeMapping)
                .roleMapping(roleMapping)
                .jitProvisioning(jitProvisioning)
                .metadata(metadata)
                .version(1L)
                .build();

        // When
        OIDCProviderConfigEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getProviderId()).isEqualTo(providerId.getValue());
        assertThat(entity.getProviderName()).isEqualTo("test-provider");
        assertThat(entity.getProviderType()).isEqualTo("OIDC");
        assertThat(entity.getEnabled()).isTrue();
        assertThat(entity.getDisplayName()).isEqualTo("Test Provider");
        assertThat(entity.getConfiguration()).isNotNull();
        assertThat(entity.getAttributeMapping()).isNotNull();
        assertThat(entity.getRoleMapping()).isNotNull();
        assertThat(entity.getJitProvisioning()).isNotNull();
        assertThat(entity.getMetadata()).isEqualTo(metadata);
        assertThat(entity.getVersion()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        OIDCProviderConfigEntity entity = OIDCProviderConfigEntity.builder()
                .providerId(UUID.randomUUID())
                .providerName("test-provider")
                .providerType("OIDC")
                .enabled(true)
                .displayName("Test Provider")
                .configuration(null)
                .attributeMapping(null)
                .roleMapping(null)
                .jitProvisioning(null)
                .metadata(null)
                .createdAt(Instant.now())
                .lastModifiedAt(Instant.now())
                .version(0L)
                .build();

        // When
        OIDCProviderConfigAggregate domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getConfiguration()).isNull();
        assertThat(domain.getAttributeMapping()).isNull();
        assertThat(domain.getRoleMapping()).isNull();
        assertThat(domain.getJitProvisioning()).isNull();
        assertThat(domain.getMetadata()).isNull();
    }

    @Test
    @DisplayName("Should handle null values when mapping domain to entity")
    void shouldHandleNullValuesDomainToEntity() {
        // Given
        OIDCProviderConfigAggregate domain = OIDCProviderConfigAggregate.builder()
                .id(OIDCProviderConfigId.generate())
                .providerName("test-provider")
                .providerType(ProviderType.OIDC)
                .enabled(true)
                .displayName("Test Provider")
                .configuration(null)
                .attributeMapping(null)
                .roleMapping(null)
                .jitProvisioning(null)
                .metadata(null)
                .version(0L)
                .build();

        // When
        OIDCProviderConfigEntity entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getConfiguration()).isNull();
        assertThat(entity.getAttributeMapping()).isNull();
        assertThat(entity.getRoleMapping()).isNull();
        assertThat(entity.getJitProvisioning()).isNull();
        assertThat(entity.getMetadata()).isNull();
    }

    @Test
    @DisplayName("Should map all ProviderType enum values correctly")
    void shouldMapAllProviderTypeValues() {
        for (ProviderType providerType : ProviderType.values()) {
            // Given
            OIDCProviderConfigEntity entity = OIDCProviderConfigEntity.builder()
                    .providerId(UUID.randomUUID())
                    .providerName("test-provider")
                    .providerType(providerType.name())
                    .enabled(true)
                    .displayName("Test Provider")
                    .createdAt(Instant.now())
                    .lastModifiedAt(Instant.now())
                    .version(0L)
                    .build();

            // When
            OIDCProviderConfigAggregate domain = mapper.toDomain(entity);

            // Then
            assertThat(domain.getProviderType()).isEqualTo(providerType);

            // Reverse mapping
            OIDCProviderConfigEntity mappedEntity = mapper.toEntity(domain);
            assertThat(mappedEntity.getProviderType()).isEqualTo(providerType.name());
        }
    }

    @Test
    @DisplayName("Should perform round-trip mapping correctly")
    void shouldPerformRoundTripMapping() {
        // Given
        OIDCProviderConfigId providerId = OIDCProviderConfigId.generate();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("key", "value");

        OIDCConfiguration oidcConfig = OIDCConfiguration.builder()
                .issuerUri("https://example.com")
                .clientId("client123")
                .build();

        OIDCProviderConfigAggregate originalDomain = OIDCProviderConfigAggregate.builder()
                .id(providerId)
                .providerName("test-provider")
                .providerType(ProviderType.OIDC)
                .enabled(true)
                .displayName("Test Provider")
                .configuration(oidcConfig)
                .metadata(metadata)
                .version(1L)
                .build();

        // When
        OIDCProviderConfigEntity entity = mapper.toEntity(originalDomain);
        OIDCProviderConfigAggregate mappedDomain = mapper.toDomain(entity);

        // Then
        assertThat(mappedDomain.getId().getValue()).isEqualTo(originalDomain.getId().getValue());
        assertThat(mappedDomain.getProviderName()).isEqualTo(originalDomain.getProviderName());
        assertThat(mappedDomain.getProviderType()).isEqualTo(originalDomain.getProviderType());
        assertThat(mappedDomain.getEnabled()).isEqualTo(originalDomain.getEnabled());
        assertThat(mappedDomain.getDisplayName()).isEqualTo(originalDomain.getDisplayName());
        assertThat(mappedDomain.getMetadata()).isEqualTo(originalDomain.getMetadata());
    }
}


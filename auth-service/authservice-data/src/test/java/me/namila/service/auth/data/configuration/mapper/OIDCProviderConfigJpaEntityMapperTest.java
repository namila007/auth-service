package me.namila.service.auth.data.configuration.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.namila.service.auth.data.configuration.entity.OIDCProviderConfigJpaEntity;
import me.namila.service.auth.domain.core.configuration.model.*;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.configuration.valueobject.ProviderType;
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
 * Unit tests for OIDCProviderConfigEntityMapper.
 */
@DisplayName("OIDCProviderConfigEntityMapper Tests")
class OIDCProviderConfigJpaEntityMapperTest
{

    private OIDCProviderConfigEntityMapper mapper;

    @BeforeEach
    void setUp() {
        // Create mapper instance and inject ObjectMapper using reflection
        mapper = Mappers.getMapper(OIDCProviderConfigEntityMapper.class);
        try {
            java.lang.reflect.Field field = OIDCProviderConfigEntityMapper.class.getDeclaredField("objectMapper");
            field.setAccessible(true);
            field.set(mapper, new ObjectMapper());
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject ObjectMapper", e);
        }
    }

    @Test
    @DisplayName("Should map OIDCProviderConfigJpaEntity to OIDCProviderConfigAggregate domain model")
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

        OIDCProviderConfigJpaEntity entity = OIDCProviderConfigJpaEntity.builder()
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
        assertNotNull(domain);
        assertNotNull(domain.getId());
        assertEquals(providerId, domain.getId().getValue());
        assertEquals("test-provider", domain.getProviderName());
        assertEquals(ProviderType.OIDC, domain.getProviderType());
        assertTrue(domain.getEnabled());
        assertEquals("Test Provider", domain.getDisplayName());
        assertNotNull(domain.getConfiguration());
        assertNotNull(domain.getAttributeMapping());
        assertNotNull(domain.getRoleMapping());
        assertNotNull(domain.getJitProvisioning());
        assertEquals(metadata, domain.getMetadata());
        // Version is ignored in mapper (managed by JPA), so it will have default value
        assertNotNull(domain.getVersion());
        assertEquals(LocalDateTime.ofInstant(now, ZoneOffset.UTC), domain.getCreatedAt());
        assertEquals(LocalDateTime.ofInstant(now, ZoneOffset.UTC), domain.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map OIDCProviderConfigAggregate domain model to OIDCProviderConfigJpaEntity")
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

        LocalDateTime localNow = LocalDateTime.now();
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
                .createdAt(localNow)
                .updatedAt(localNow)
                .build();

        // When
        OIDCProviderConfigJpaEntity entity = mapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertEquals(providerId.getValue(), entity.getProviderId());
        assertEquals("test-provider", entity.getProviderName());
        assertEquals("OIDC", entity.getProviderType());
        assertTrue(entity.getEnabled());
        assertEquals("Test Provider", entity.getDisplayName());
        assertNotNull(entity.getConfiguration());
        assertNotNull(entity.getAttributeMapping());
        assertNotNull(entity.getRoleMapping());
        assertNotNull(entity.getJitProvisioning());
        assertEquals(metadata, entity.getMetadata());
        // Version is ignored in mapper (managed by JPA), so we don't assert it
        // Version will be managed by JPA/Hibernate when entity is persisted
        assertEquals(localNow.toInstant(ZoneOffset.UTC), entity.getCreatedAt());
        assertEquals(localNow.toInstant(ZoneOffset.UTC), entity.getLastModifiedAt());
    }
    
    @Test
    @DisplayName("Should convert timestamps correctly between Instant and LocalDateTime")
    void shouldConvertTimestampsCorrectly() {
        // Given
        Instant instant = Instant.now();
        LocalDateTime expectedLocalDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        
        OIDCProviderConfigJpaEntity entity = OIDCProviderConfigJpaEntity.builder()
                .providerId(UUID.randomUUID())
                .providerName("test-provider")
                .providerType("OIDC")
                .enabled(true)
                .displayName("Test Provider")
                .configuration(new HashMap<>())
                .attributeMapping(new HashMap<>())
                .roleMapping(new HashMap<>())
                .jitProvisioning(new HashMap<>())
                .createdAt(instant)
                .lastModifiedAt(instant)
                .version(0L)
                .build();

        // When
        OIDCProviderConfigAggregate domain = mapper.toDomain(entity);

        // Then
        assertEquals(expectedLocalDateTime, domain.getCreatedAt());
        assertEquals(expectedLocalDateTime, domain.getUpdatedAt());

        // Reverse mapping
        OIDCProviderConfigJpaEntity mappedEntity = mapper.toEntity(domain);
        assertEquals(instant, mappedEntity.getCreatedAt());
        assertEquals(instant, mappedEntity.getLastModifiedAt());
    }

    @Test
    @DisplayName("Should handle null values when mapping entity to domain")
    void shouldHandleNullValuesEntityToDomain() {
        // Given
        OIDCProviderConfigJpaEntity entity = OIDCProviderConfigJpaEntity.builder()
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
        assertNotNull(domain);
        assertNull(domain.getConfiguration());
        assertNull(domain.getAttributeMapping());
        assertNull(domain.getRoleMapping());
        assertNull(domain.getJitProvisioning());
        // MapStruct creates empty map instead of null for metadata
        assertTrue(domain.getMetadata() == null || domain.getMetadata().isEmpty());
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
        OIDCProviderConfigJpaEntity entity = mapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertNull(entity.getConfiguration());
        assertNull(entity.getAttributeMapping());
        assertNull(entity.getRoleMapping());
        assertNull(entity.getJitProvisioning());
        // MapStruct creates empty map instead of null for metadata
        assertTrue(entity.getMetadata() == null || entity.getMetadata().isEmpty());
    }

    @Test
    @DisplayName("Should map all ProviderType enum values correctly")
    void shouldMapAllProviderTypeValues() {
        for (ProviderType providerType : ProviderType.values()) {
            // Given
            OIDCProviderConfigJpaEntity entity = OIDCProviderConfigJpaEntity.builder()
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
            assertEquals(providerType, domain.getProviderType());

            // Reverse mapping
            OIDCProviderConfigJpaEntity mappedEntity = mapper.toEntity(domain);
            assertEquals(providerType.name(), mappedEntity.getProviderType());
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
        OIDCProviderConfigJpaEntity entity = mapper.toEntity(originalDomain);
        OIDCProviderConfigAggregate mappedDomain = mapper.toDomain(entity);

        // Then
        assertEquals(originalDomain.getId().getValue(), mappedDomain.getId().getValue());
        assertEquals(originalDomain.getProviderName(), mappedDomain.getProviderName());
        assertEquals(originalDomain.getProviderType(), mappedDomain.getProviderType());
        assertEquals(originalDomain.getEnabled(), mappedDomain.getEnabled());
        assertEquals(originalDomain.getDisplayName(), mappedDomain.getDisplayName());
        assertEquals(originalDomain.getMetadata(), mappedDomain.getMetadata());
    }
}


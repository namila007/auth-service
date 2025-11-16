package me.namila.service.auth.domain.application.configuration.mapper;

import me.namila.service.auth.domain.application.configuration.dto.request.*;
import me.namila.service.auth.domain.application.configuration.dto.response.*;
import me.namila.service.auth.domain.core.configuration.model.*;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.configuration.valueobject.MappingStrategy;
import me.namila.service.auth.domain.core.configuration.valueobject.ProviderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OIDCProviderConfigDtoMapper.
 * Tests DTO to Domain and Domain to DTO mapping logic.
 */
@DisplayName("OIDCProviderConfigDtoMapper Unit Tests")
class OIDCProviderConfigDtoMapperTest {
    
    private OIDCProviderConfigDtoMapper mapper;
    
    @BeforeEach
    void setUp() {
        // Get MapStruct generated implementation
        mapper = Mappers.getMapper(OIDCProviderConfigDtoMapper.class);
    }
    
    @Test
    @DisplayName("Should map CreateOIDCProviderConfigRequest to domain aggregate")
    void shouldMapCreateRequestToDomain() {
        // Given
        CreateOIDCProviderConfigRequest request = CreateOIDCProviderConfigRequest.builder()
            .providerName("test-provider")
            .displayName("Test Provider")
            .providerType("OIDC")
            .enabled(true)
            .configuration(createOIDCConfigurationRequest())
            .attributeMapping(createAttributeMappingConfigRequest())
            .roleMapping(createRoleMappingConfigRequest())
            .jitProvisioning(createJITProvisioningConfigRequest())
            .metadata(Map.of("key1", "value1"))
            .build();
        
        // When
        OIDCProviderConfigAggregate aggregate = mapper.toDomain(request);
        
        // Then
        assertNotNull(aggregate);
        assertEquals("test-provider", aggregate.getProviderName());
        assertEquals("Test Provider", aggregate.getDisplayName());
        assertEquals(ProviderType.OIDC, aggregate.getProviderType());
        assertTrue(aggregate.getEnabled());
        
        // Verify OIDC configuration
        assertNotNull(aggregate.getConfiguration());
        assertEquals("https://example.com", aggregate.getConfiguration().getIssuerUri());
        assertEquals("client-id-123", aggregate.getConfiguration().getClientId());
        assertEquals("client-secret-456", aggregate.getConfiguration().getClientSecret());
        
        // Verify attribute mapping
        assertNotNull(aggregate.getAttributeMapping());
        assertEquals("sub", aggregate.getAttributeMapping().getSubjectAttribute());
        assertEquals(1, aggregate.getAttributeMapping().getCustomMappings().size());
        
        // Verify role mapping
        assertNotNull(aggregate.getRoleMapping());
        assertEquals(MappingStrategy.EXPLICIT, aggregate.getRoleMapping().getMappingStrategy());
        
        // Verify JIT provisioning
        assertNotNull(aggregate.getJitProvisioning());
        assertTrue(aggregate.getJitProvisioning().getEnabled());
        assertTrue(aggregate.getJitProvisioning().getCreateUsers());
    }
    
    @Test
    @DisplayName("Should map domain aggregate to response DTO")
    void shouldMapDomainToResponse() {
        // Given
        OIDCProviderConfigId providerId = OIDCProviderConfigId.generate();
        LocalDateTime now = LocalDateTime.now();
        
        OIDCProviderConfigAggregate aggregate = OIDCProviderConfigAggregate.builder()
            .id(providerId)
            .providerName("test-provider")
            .displayName("Test Provider")
            .providerType(ProviderType.OIDC)
            .enabled(true)
            .configuration(createOIDCConfiguration())
            .attributeMapping(createAttributeMappingConfig())
            .roleMapping(createRoleMappingConfig())
            .jitProvisioning(createJITProvisioningConfig())
            .metadata(Map.of("key1", "value1"))
            .version(1L)
            .createdAt(now)
            .updatedAt(now)
            .build();
        
        // When
        OIDCProviderConfigResponse response = mapper.toResponse(aggregate);
        
        // Then
        assertNotNull(response);
        assertEquals(providerId.getValue(), response.getProviderId());
        assertEquals("test-provider", response.getProviderName());
        assertEquals("Test Provider", response.getDisplayName());
        assertEquals("OIDC", response.getProviderType());
        assertTrue(response.getEnabled());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getLastModifiedAt());
        assertEquals(Map.of("key1", "value1"), response.getMetadata());
    }
    
    @Test
    @DisplayName("Should map domain aggregate to detail response with full configuration")
    void shouldMapDomainToDetailResponse() {
        // Given
        OIDCProviderConfigId providerId = OIDCProviderConfigId.generate();
        LocalDateTime now = LocalDateTime.now();
        
        OIDCProviderConfigAggregate aggregate = OIDCProviderConfigAggregate.builder()
            .id(providerId)
            .providerName("test-provider")
            .displayName("Test Provider")
            .providerType(ProviderType.OIDC)
            .enabled(true)
            .configuration(createOIDCConfiguration())
            .attributeMapping(createAttributeMappingConfig())
            .roleMapping(createRoleMappingConfig())
            .jitProvisioning(createJITProvisioningConfig())
            .metadata(Map.of("key1", "value1"))
            .version(1L)
            .createdAt(now)
            .updatedAt(now)
            .build();
        
        // When
        OIDCProviderConfigDetailResponse response = mapper.toDetailResponse(aggregate);
        
        // Then
        assertNotNull(response);
        assertEquals(providerId.getValue(), response.getProviderId());
        assertEquals("test-provider", response.getProviderName());
        assertEquals("Test Provider", response.getDisplayName());
        assertEquals("OIDC", response.getProviderType());
        assertTrue(response.getEnabled());
        assertEquals(1L, response.getVersion());
        
        // Verify OIDC configuration response
        assertNotNull(response.getConfiguration());
        assertEquals("https://example.com", response.getConfiguration().getIssuerUri());
        assertEquals("client-id-123", response.getConfiguration().getClientId());
        // Note: Client secret is not mapped to response for security
        
        // Verify attribute mapping response
        assertNotNull(response.getAttributeMapping());
        assertEquals("sub", response.getAttributeMapping().getSubjectAttribute());
        assertEquals("email", response.getAttributeMapping().getEmailAttribute());
        
        // Verify role mapping response
        assertNotNull(response.getRoleMapping());
        assertEquals("EXPLICIT", response.getRoleMapping().getMappingStrategy());
        
        // Verify JIT provisioning response
        assertNotNull(response.getJitProvisioning());
        assertTrue(response.getJitProvisioning().getEnabled());
    }
    
    @Test
    @DisplayName("Should update domain from UpdateOIDCProviderConfigRequest")
    void shouldUpdateDomainFromRequest() {
        // Given
        OIDCProviderConfigAggregate aggregate = OIDCProviderConfigAggregate.builder()
            .id(OIDCProviderConfigId.generate())
            .providerName("original-provider")
            .displayName("Original Provider")
            .providerType(ProviderType.OIDC)
            .enabled(false)
            .configuration(createOIDCConfiguration())
            .metadata(new java.util.HashMap<>())  // Initialize metadata map
            .build();
        
        // Verify metadata is not null before update
        assertNotNull(aggregate.getMetadata(), "Metadata should be initialized");
        
        UpdateOIDCProviderConfigRequest request = UpdateOIDCProviderConfigRequest.builder()
            .displayName("Updated Provider")
            .enabled(true)
            .configuration(createOIDCConfigurationRequest())
            .attributeMapping(createAttributeMappingConfigRequest())
            .roleMapping(createRoleMappingConfigRequest())
            .jitProvisioning(createJITProvisioningConfigRequest())
            .metadata(new java.util.HashMap<>(Map.of("updated", "true")))  // Use mutable HashMap
            .build();
        
        // When
        mapper.updateDomainFromRequest(request, aggregate);
        
        // Then
        assertEquals("Updated Provider", aggregate.getDisplayName());
        assertTrue(aggregate.getEnabled());
        // Provider name should not change
        assertEquals("original-provider", aggregate.getProviderName());
        assertNotNull(aggregate.getAttributeMapping());
        assertNotNull(aggregate.getRoleMapping());
        assertNotNull(aggregate.getJitProvisioning());
        assertEquals(Map.of("updated", "true"), aggregate.getMetadata());
    }
    
    @Test
    @DisplayName("Should handle null values gracefully in update")
    void shouldHandleNullValuesInUpdate() {
        // Given
        OIDCProviderConfigAggregate aggregate = OIDCProviderConfigAggregate.builder()
            .id(OIDCProviderConfigId.generate())
            .providerName("test-provider")
            .displayName("Test Provider")
            .providerType(ProviderType.OIDC)
            .enabled(true)
            .configuration(createOIDCConfiguration())
            .build();
        
        UpdateOIDCProviderConfigRequest request = UpdateOIDCProviderConfigRequest.builder()
            .displayName(null) // Null should be ignored
            .enabled(null)
            .build();
        
        // When
        mapper.updateDomainFromRequest(request, aggregate);
        
        // Then
        // Values should remain unchanged when request has null
        assertEquals("Test Provider", aggregate.getDisplayName());
        assertTrue(aggregate.getEnabled());
    }
    
    @Test
    @DisplayName("Should convert ProviderType string to enum")
    void shouldConvertProviderTypeStringToEnum() {
        // Given
        CreateOIDCProviderConfigRequest request = CreateOIDCProviderConfigRequest.builder()
            .providerName("test")
            .displayName("Test")
            .providerType("oidc") // lowercase
            .enabled(true)
            .configuration(createOIDCConfigurationRequest())
            .build();
        
        // When
        OIDCProviderConfigAggregate aggregate = mapper.toDomain(request);
        
        // Then
        assertEquals(ProviderType.OIDC, aggregate.getProviderType());
    }
    
    @Test
    @DisplayName("Should convert MappingStrategy string to enum")
    void shouldConvertMappingStrategyStringToEnum() {
        // Given
        RoleMappingConfigRequest request = RoleMappingConfigRequest.builder()
            .mappingStrategy("explicit") // lowercase
            .mappingRules(new ArrayList<>())
            .build();
        
        // When
        RoleMappingConfig config = mapper.toRoleMappingConfig(request);
        
        // Then
        assertEquals(MappingStrategy.EXPLICIT, config.getMappingStrategy());
    }
    
    @Test
    @DisplayName("Should map all nested objects correctly")
    void shouldMapAllNestedObjectsCorrectly() {
        // Given
        CreateOIDCProviderConfigRequest request = CreateOIDCProviderConfigRequest.builder()
            .providerName("comprehensive-test")
            .displayName("Comprehensive Test")
            .providerType("OIDC")
            .enabled(true)
            .configuration(OIDCConfigurationRequest.builder()
                .issuerUri("https://issuer.com")
                .clientId("client-123")
                .clientSecret("secret-456")
                .authorizationUri("https://issuer.com/authorize")
                .tokenUri("https://issuer.com/token")
                .userInfoUri("https://issuer.com/userinfo")
                .jwksUri("https://issuer.com/jwks")
                .scopes(Set.of("openid", "profile", "email"))
                .additionalParameters(Map.of("prompt", "login"))
                .build())
            .attributeMapping(AttributeMappingConfigRequest.builder()
                .subjectAttribute("sub")
                .emailAttribute("email")
                .nameAttribute("name")
                .groupsAttribute("groups")
                .customMappings(List.of(
                    AttributeMappingRequest.builder()
                        .externalAttribute("external_attr")
                        .internalAttribute("internal_attr")
                        .transformationRule("rule1")
                        .build()
                ))
                .build())
            .roleMapping(RoleMappingConfigRequest.builder()
                .mappingStrategy("PATTERN")
                .mappingRules(List.of(
                    RoleMappingRuleRequest.builder()
                        .externalGroup("admin-.*")
                        .internalRole("ROLE_ADMIN")
                        .conditions(Map.of("condition1", "value1"))
                        .build()
                ))
                .build())
            .jitProvisioning(JITProvisioningConfigRequest.builder()
                .enabled(true)
                .createUsers(true)
                .updateUsers(true)
                .syncGroups(true)
                .defaultRoles(Set.of("ROLE_USER"))
                .build())
            .metadata(Map.of("env", "production"))
            .build();
        
        // When
        OIDCProviderConfigAggregate aggregate = mapper.toDomain(request);
        
        // Then
        assertNotNull(aggregate);
        
        // Verify OIDC configuration
        OIDCConfiguration config = aggregate.getConfiguration();
        assertNotNull(config);
        assertEquals("https://issuer.com", config.getIssuerUri());
        assertEquals("client-123", config.getClientId());
        assertEquals("secret-456", config.getClientSecret());
        assertEquals(3, config.getScopes().size());
        assertTrue(config.getScopes().contains("openid"));
        
        // Verify attribute mapping
        AttributeMappingConfig attrMapping = aggregate.getAttributeMapping();
        assertNotNull(attrMapping);
        assertEquals("sub", attrMapping.getSubjectAttribute());
        assertEquals(1, attrMapping.getCustomMappings().size());
        assertEquals("external_attr", attrMapping.getCustomMappings().get(0).getExternalAttribute());
        
        // Verify role mapping
        RoleMappingConfig roleMapping = aggregate.getRoleMapping();
        assertNotNull(roleMapping);
        assertEquals(MappingStrategy.PATTERN, roleMapping.getMappingStrategy());
        assertEquals(1, roleMapping.getMappingRules().size());
        assertEquals("admin-.*", roleMapping.getMappingRules().get(0).getExternalGroup());
        
        // Verify JIT provisioning
        JITProvisioningConfig jit = aggregate.getJitProvisioning();
        assertNotNull(jit);
        assertTrue(jit.getEnabled());
        assertTrue(jit.getCreateUsers());
        assertEquals(1, jit.getDefaultRoles().size());
        assertTrue(jit.getDefaultRoles().contains("ROLE_USER"));
    }
    
    // Helper methods to create test data
    
    private OIDCConfigurationRequest createOIDCConfigurationRequest() {
        return OIDCConfigurationRequest.builder()
            .issuerUri("https://example.com")
            .clientId("client-id-123")
            .clientSecret("client-secret-456")
            .authorizationUri("https://example.com/authorize")
            .tokenUri("https://example.com/token")
            .userInfoUri("https://example.com/userinfo")
            .jwksUri("https://example.com/jwks")
            .scopes(Set.of("openid", "profile", "email"))
            .additionalParameters(Map.of("prompt", "consent"))
            .build();
    }
    
    private AttributeMappingConfigRequest createAttributeMappingConfigRequest() {
        return AttributeMappingConfigRequest.builder()
            .subjectAttribute("sub")
            .emailAttribute("email")
            .nameAttribute("name")
            .groupsAttribute("groups")
            .customMappings(List.of(
                AttributeMappingRequest.builder()
                    .externalAttribute("dept")
                    .internalAttribute("department")
                    .transformationRule("uppercase")
                    .build()
            ))
            .build();
    }
    
    private RoleMappingConfigRequest createRoleMappingConfigRequest() {
        return RoleMappingConfigRequest.builder()
            .mappingStrategy("EXPLICIT")
            .mappingRules(List.of(
                RoleMappingRuleRequest.builder()
                    .externalGroup("admins")
                    .internalRole("ROLE_ADMIN")
                    .conditions(new HashMap<>())
                    .build()
            ))
            .build();
    }
    
    private JITProvisioningConfigRequest createJITProvisioningConfigRequest() {
        return JITProvisioningConfigRequest.builder()
            .enabled(true)
            .createUsers(true)
            .updateUsers(true)
            .syncGroups(true)
            .defaultRoles(Set.of("ROLE_USER"))
            .build();
    }
    
    private OIDCConfiguration createOIDCConfiguration() {
        return OIDCConfiguration.builder()
            .issuerUri("https://example.com")
            .clientId("client-id-123")
            .clientSecret("client-secret-456")
            .authorizationUri("https://example.com/authorize")
            .tokenUri("https://example.com/token")
            .userInfoUri("https://example.com/userinfo")
            .jwksUri("https://example.com/jwks")
            .scopes(Set.of("openid", "profile", "email"))
            .additionalParameters(Map.of("prompt", "consent"))
            .build();
    }
    
    private AttributeMappingConfig createAttributeMappingConfig() {
        return AttributeMappingConfig.builder()
            .subjectAttribute("sub")
            .emailAttribute("email")
            .nameAttribute("name")
            .groupsAttribute("groups")
            .customMappings(List.of(
                AttributeMap.builder()
                    .externalAttribute("dept")
                    .internalAttribute("department")
                    .transformationRule("uppercase")
                    .build()
            ))
            .build();
    }
    
    private RoleMappingConfig createRoleMappingConfig() {
        return RoleMappingConfig.builder()
            .mappingStrategy(MappingStrategy.EXPLICIT)
            .mappingRules(List.of(
                RoleMappingRule.builder()
                    .externalGroup("admins")
                    .internalRole("ROLE_ADMIN")
                    .conditions(new HashMap<>())
                    .build()
            ))
            .build();
    }
    
    private JITProvisioningConfig createJITProvisioningConfig() {
        return JITProvisioningConfig.builder()
            .enabled(true)
            .createUsers(true)
            .updateUsers(true)
            .syncGroups(true)
            .defaultRoles(Set.of("ROLE_USER"))
            .build();
    }
}

package me.namila.service.auth.domain.application.configuration.mapper;

import me.namila.service.auth.domain.application.configuration.dto.request.*;
import me.namila.service.auth.domain.application.configuration.dto.response.*;
import me.namila.service.auth.domain.core.configuration.model.*;
import me.namila.service.auth.domain.core.configuration.valueobject.MappingStrategy;
import me.namila.service.auth.domain.core.configuration.valueobject.ProviderType;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * MapStruct mapper for OIDC Provider Configuration DTO-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OIDCProviderConfigDtoMapper {
    
    // ==================== Request to Domain ====================
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "providerName", source = "providerName")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "providerType", source = "providerType", qualifiedByName = "stringToProviderType")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "configuration", source = "configuration")
    @Mapping(target = "attributeMapping", source = "attributeMapping")
    @Mapping(target = "roleMapping", source = "roleMapping")
    @Mapping(target = "jitProvisioning", source = "jitProvisioning")
    @Mapping(target = "metadata", source = "metadata")
    @Mapping(target = "version", ignore = true)
    OIDCProviderConfigAggregate toDomain(CreateOIDCProviderConfigRequest request);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "providerName", ignore = true) // Cannot update provider name
    @Mapping(target = "providerType", ignore = true) // Cannot update provider type
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "configuration", source = "configuration")
    @Mapping(target = "attributeMapping", source = "attributeMapping")
    @Mapping(target = "roleMapping", source = "roleMapping")
    @Mapping(target = "jitProvisioning", source = "jitProvisioning")
    @Mapping(target = "metadata", ignore = true) // Handle manually due to defensive copying in getter
    @Mapping(target = "version", ignore = true)
    void updateDomainFromRequest(UpdateOIDCProviderConfigRequest request, @MappingTarget OIDCProviderConfigAggregate domain);
    
    // AfterMapping to handle metadata update (due to defensive copying in getMetadata())
    @AfterMapping
    default void updateMetadata(UpdateOIDCProviderConfigRequest request, @MappingTarget OIDCProviderConfigAggregate domain) {
        if (request.getMetadata() != null) {
            domain.setMetadata(request.getMetadata());
        }
    }
    
    // OIDC Configuration
    OIDCConfiguration toOIDCConfiguration(OIDCConfigurationRequest request);
    
    // Attribute Mapping
    AttributeMappingConfig toAttributeMappingConfig(AttributeMappingConfigRequest request);
    AttributeMap toAttributeMap(AttributeMappingRequest request);
    
    // Role Mapping
    @Mapping(target = "mappingStrategy", source = "mappingStrategy", qualifiedByName = "stringToMappingStrategy")
    RoleMappingConfig toRoleMappingConfig(RoleMappingConfigRequest request);
    RoleMappingRule toRoleMappingRule(RoleMappingRuleRequest request);
    
    // JIT Provisioning
    JITProvisioningConfig toJITProvisioningConfig(JITProvisioningConfigRequest request);
    
    // ==================== Domain to Response ====================
    
    @Mapping(target = "providerId", source = "id.value")
    @Mapping(target = "providerName", source = "providerName")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "providerType", source = "providerType", qualifiedByName = "providerTypeToString")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(target = "lastModifiedAt", source = "updatedAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(target = "metadata", source = "metadata")
    OIDCProviderConfigResponse toResponse(OIDCProviderConfigAggregate domain);
    
    @Mapping(target = "providerId", source = "id.value")
    @Mapping(target = "providerName", source = "providerName")
    @Mapping(target = "displayName", source = "displayName")
    @Mapping(target = "providerType", source = "providerType", qualifiedByName = "providerTypeToString")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "configuration", source = "configuration", qualifiedByName = "sanitizeConfiguration")
    @Mapping(target = "attributeMapping", source = "attributeMapping")
    @Mapping(target = "roleMapping", source = "roleMapping")
    @Mapping(target = "jitProvisioning", source = "jitProvisioning")
    @Mapping(target = "metadata", source = "metadata")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(target = "lastModifiedAt", source = "updatedAt", qualifiedByName = "localDateTimeToInstant")
    OIDCProviderConfigDetailResponse toDetailResponse(OIDCProviderConfigAggregate domain);
    
    // OIDC Configuration Response - Sanitize sensitive data
    @Named("sanitizeConfiguration")
    OIDCConfigurationResponse toOIDCConfigurationResponse(OIDCConfiguration domain);
    
    // Attribute Mapping Response
    AttributeMappingConfigResponse toAttributeMappingConfigResponse(AttributeMappingConfig domain);
    AttributeMappingResponse toAttributeMappingResponse(AttributeMap domain);
    
    // Role Mapping Response
    @Mapping(target = "mappingStrategy", source = "mappingStrategy", qualifiedByName = "mappingStrategyToString")
    RoleMappingConfigResponse toRoleMappingConfigResponse(RoleMappingConfig domain);
    RoleMappingRuleResponse toRoleMappingRuleResponse(RoleMappingRule domain);
    
    // JIT Provisioning Response
    JITProvisioningConfigResponse toJITProvisioningConfigResponse(JITProvisioningConfig domain);
    
    // ==================== Value Object Converters ====================
    
    @Named("stringToProviderType")
    default ProviderType stringToProviderType(String providerType) {
        return providerType != null ? ProviderType.valueOf(providerType.toUpperCase()) : null;
    }
    
    @Named("providerTypeToString")
    default String providerTypeToString(ProviderType providerType) {
        return providerType != null ? providerType.name() : null;
    }
    
    @Named("stringToMappingStrategy")
    default MappingStrategy stringToMappingStrategy(String strategy) {
        return strategy != null ? MappingStrategy.valueOf(strategy.toUpperCase()) : MappingStrategy.EXPLICIT;
    }
    
    @Named("mappingStrategyToString")
    default String mappingStrategyToString(MappingStrategy strategy) {
        return strategy != null ? strategy.name() : null;
    }
    
    @Named("localDateTimeToInstant")
    default Instant localDateTimeToInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
}

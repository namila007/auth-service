package me.namila.service.auth.data.configuration.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.namila.service.auth.data.configuration.entity.OIDCProviderConfigEntity;
import me.namila.service.auth.domain.core.configuration.model.*;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.configuration.valueobject.ProviderType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MapStruct mapper for OIDCProviderConfig Entity-Domain conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class OIDCProviderConfigEntityMapper {
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    @Mapping(target = "id", source = "providerId", qualifiedByName = "uuidToOIDCProviderConfigId")
    @Mapping(target = "providerType", source = "providerType", qualifiedByName = "stringToProviderType")
    @Mapping(target = "configuration", source = "configuration", qualifiedByName = "jsonToOIDCConfiguration")
    @Mapping(target = "attributeMapping", source = "attributeMapping", qualifiedByName = "jsonToAttributeMappingConfig")
    @Mapping(target = "roleMapping", source = "roleMapping", qualifiedByName = "jsonToRoleMappingConfig")
    @Mapping(target = "jitProvisioning", source = "jitProvisioning", qualifiedByName = "jsonToJITProvisioningConfig")
    public abstract     OIDCProviderConfigAggregate toDomain(OIDCProviderConfigEntity entity);
    
    @Mapping(target = "providerId", source = "id.value")
    @Mapping(target = "providerType", source = "providerType", qualifiedByName = "providerTypeToString")
    @Mapping(target = "configuration", source = "configuration", qualifiedByName = "oidcConfigurationToJson")
    @Mapping(target = "attributeMapping", source = "attributeMapping", qualifiedByName = "attributeMappingConfigToJson")
    @Mapping(target = "roleMapping", source = "roleMapping", qualifiedByName = "roleMappingConfigToJson")
    @Mapping(target = "jitProvisioning", source = "jitProvisioning", qualifiedByName = "jitProvisioningConfigToJson")
    public abstract OIDCProviderConfigEntity toEntity(OIDCProviderConfigAggregate domain);
    
    @Named("stringToProviderType")
    protected ProviderType stringToProviderType(String providerType) {
        return providerType != null ? ProviderType.valueOf(providerType) : null;
    }
    
    @Named("providerTypeToString")
    protected String providerTypeToString(ProviderType providerType) {
        return providerType != null ? providerType.name() : null;
    }
    
    @Named("jsonToOIDCConfiguration")
    protected OIDCConfiguration jsonToOIDCConfiguration(java.util.Map<String, Object> json) {
        if (json == null) return null;
        try {
            return objectMapper.convertValue(json, OIDCConfiguration.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize OIDCConfiguration", e);
        }
    }
    
    @Named("oidcConfigurationToJson")
    protected java.util.Map<String, Object> oidcConfigurationToJson(OIDCConfiguration config) {
        if (config == null) return null;
        try {
            return objectMapper.convertValue(config, new TypeReference<java.util.Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize OIDCConfiguration", e);
        }
    }
    
    @Named("jsonToAttributeMappingConfig")
    protected AttributeMappingConfig jsonToAttributeMappingConfig(java.util.Map<String, Object> json) {
        if (json == null) return null;
        try {
            return objectMapper.convertValue(json, AttributeMappingConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize AttributeMappingConfig", e);
        }
    }
    
    @Named("attributeMappingConfigToJson")
    protected java.util.Map<String, Object> attributeMappingConfigToJson(AttributeMappingConfig config) {
        if (config == null) return null;
        try {
            return objectMapper.convertValue(config, new TypeReference<java.util.Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize AttributeMappingConfig", e);
        }
    }
    
    @Named("jsonToRoleMappingConfig")
    protected RoleMappingConfig jsonToRoleMappingConfig(java.util.Map<String, Object> json) {
        if (json == null) return null;
        try {
            return objectMapper.convertValue(json, RoleMappingConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize RoleMappingConfig", e);
        }
    }
    
    @Named("roleMappingConfigToJson")
    protected java.util.Map<String, Object> roleMappingConfigToJson(RoleMappingConfig config) {
        if (config == null) return null;
        try {
            return objectMapper.convertValue(config, new TypeReference<java.util.Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize RoleMappingConfig", e);
        }
    }
    
    @Named("jsonToJITProvisioningConfig")
    protected JITProvisioningConfig jsonToJITProvisioningConfig(java.util.Map<String, Object> json) {
        if (json == null) return null;
        try {
            return objectMapper.convertValue(json, JITProvisioningConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JITProvisioningConfig", e);
        }
    }
    
    @Named("jitProvisioningConfigToJson")
    protected java.util.Map<String, Object> jitProvisioningConfigToJson(JITProvisioningConfig config) {
        if (config == null) return null;
        try {
            return objectMapper.convertValue(config, new TypeReference<java.util.Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize JITProvisioningConfig", e);
        }
    }
    
    @Named("uuidToOIDCProviderConfigId")
    protected OIDCProviderConfigId uuidToOIDCProviderConfigId(java.util.UUID uuid) {
        return uuid != null ? OIDCProviderConfigId.of(uuid) : null;
    }
}


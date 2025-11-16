# Phase 3 - Task 1: OIDC Provider Configuration APIs - Step-by-Step Implementation

## Overview

This document provides step-by-step instructions to implement Task 1 of Phase 3 using TDD approach.

## Prerequisites

1. Run the setup script to create directory structure:
```bash
cd /home/runner/work/auth-service/auth-service
chmod +x phase3-task1-setup.sh
./phase3-task1-setup.sh
```

## Implementation Steps (TDD Approach)

### Step 1: Create Request DTOs

#### File 1: `AttributeMapRequest.java`
**Location:** `authservice-domain/domain-application/src/main/java/me/namila/service/auth/domain/application/configuration/dto/request/`

```java
package me.namila.service.auth.domain.application.configuration.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeMapRequest {
    @NotBlank(message = "External attribute is required")
    private String externalAttribute;
    
    @NotBlank(message = "Internal attribute is required")
    private String internalAttribute;
    
    private String transformationRule;
}
```

#### File 2: `AttributeMappingConfigRequest.java`
```java
package me.namila.service.auth.domain.application.configuration.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeMappingConfigRequest {
    private String subjectAttribute;
    private String emailAttribute;
    private String nameAttribute;
    private String groupsAttribute;
    
    @Valid
    @Builder.Default
    private List<AttributeMapRequest> customMappings = new ArrayList<>();
}
```

#### File 3: `RoleMappingRuleRequest.java`
```java
package me.namila.service.auth.domain.application.configuration.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleMappingRuleRequest {
    private String externalGroup;
    private String internalRole;
    
    @Builder.Default
    private Map<String, Object> conditions = new HashMap<>();
}
```

#### File 4: `RoleMappingConfigRequest.java`
```java
package me.namila.service.auth.domain.application.configuration.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleMappingConfigRequest {
    @Builder.Default
    private String mappingStrategy = "EXPLICIT";
    
    @Valid
    @Builder.Default
    private List<RoleMappingRuleRequest> mappingRules = new ArrayList<>();
}
```

#### File 5: `JITProvisioningConfigRequest.java`
```java
package me.namila.service.auth.domain.application.configuration.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JITProvisioningConfigRequest {
    @Builder.Default
    private Boolean enabled = true;
    
    @Builder.Default
    private Boolean createUsers = true;
    
    @Builder.Default
    private Boolean updateUsers = true;
    
    @Builder.Default
    private Boolean syncGroups = true;
    
    @Builder.Default
    private Set<String> defaultRoles = new HashSet<>();
}
```

#### File 6: `OIDCConfigurationRequest.java`
```java
package me.namila.service.auth.domain.application.configuration.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OIDCConfigurationRequest {
    @NotBlank(message = "Issuer URI is required")
    private String issuerUri;
    
    @NotBlank(message = "Client ID is required")
    private String clientId;
    
    @NotBlank(message = "Client secret is required")
    private String clientSecret;
    
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String jwksUri;
    
    @NotNull(message = "Scopes are required")
    @Builder.Default
    private Set<String> scopes = new HashSet<>();
    
    @Builder.Default
    private Map<String, String> additionalParameters = new HashMap<>();
}
```

#### File 7: `CreateOIDCProviderConfigRequest.java`
```java
package me.namila.service.auth.domain.application.configuration.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOIDCProviderConfigRequest {
    @NotBlank(message = "Provider name is required")
    @Size(min = 2, max = 255, message = "Provider name must be between 2 and 255 characters")
    private String providerName;
    
    @NotBlank(message = "Display name is required")
    @Size(min = 2, max = 255, message = "Display name must be between 2 and 255 characters")
    private String displayName;
    
    @NotNull(message = "Provider type is required")
    private String providerType;
    
    @Builder.Default
    private Boolean enabled = true;
    
    @Valid
    @NotNull(message = "OIDC configuration is required")
    private OIDCConfigurationRequest configuration;
    
    @Valid
    @Builder.Default
    private AttributeMappingConfigRequest attributeMapping = new AttributeMappingConfigRequest();
    
    @Valid
    @Builder.Default
    private RoleMappingConfigRequest roleMapping = new RoleMappingConfigRequest();
    
    @Valid
    @Builder.Default
    private JITProvisioningConfigRequest jitProvisioning = new JITProvisioningConfigRequest();
    
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
}
```

#### File 8: `UpdateOIDCProviderConfigRequest.java`
```java
package me.namila.service.auth.domain.application.configuration.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOIDCProviderConfigRequest {
    @Size(min = 2, max = 255, message = "Display name must be between 2 and 255 characters")
    private String displayName;
    
    private String providerType;
    private Boolean enabled;
    
    @Valid
    private OIDCConfigurationRequest configuration;
    
    @Valid
    private AttributeMappingConfigRequest attributeMapping;
    
    @Valid
    private RoleMappingConfigRequest roleMapping;
    
    @Valid
    private JITProvisioningConfigRequest jitProvisioning;
    
    private Map<String, Object> metadata;
}
```

### Step 2: Create Response DTOs

#### File 9: `OIDCConfigurationResponse.java`
**Location:** `authservice-domain/domain-application/src/main/java/me/namila/service/auth/domain/application/configuration/dto/response/`

```java
package me.namila.service.auth.domain.application.configuration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OIDCConfigurationResponse {
    private String issuerUri;
    private String clientId;
    // Note: clientSecret is NEVER returned in responses for security
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String jwksUri;
    private Set<String> scopes;
    private Map<String, String> additionalParameters;
}
```

#### File 10: `OIDCProviderConfigResponse.java`
```java
package me.namila.service.auth.domain.application.configuration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OIDCProviderConfigResponse {
    private UUID providerId;
    private String providerName;
    private String displayName;
    private String providerType;
    private Boolean enabled;
    private Instant createdAt;
    private Instant lastModifiedAt;
    private Map<String, Object> metadata;
}
```

#### File 11: `OIDCProviderConfigDetailResponse.java`
```java
package me.namila.service.auth.domain.application.configuration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OIDCProviderConfigDetailResponse {
    private UUID providerId;
    private String providerName;
    private String displayName;
    private String providerType;
    private Boolean enabled;
    
    private OIDCConfigurationResponse configuration;
    private Map<String, Object> attributeMapping;
    private Map<String, Object> roleMapping;
    private Map<String, Object> jitProvisioning;
    
    private Map<String, Object> metadata;
    private Instant createdAt;
    private Instant lastModifiedAt;
    private Long version;
}
```

### Step 3: Create Mapper Interface

#### File 12: `OIDCProviderConfigDtoMapper.java`
**Location:** `authservice-domain/domain-application/src/main/java/me/namila/service/auth/domain/application/configuration/mapper/`

```java
package me.namila.service.auth.domain.application.configuration.mapper;

import me.namila.service.auth.domain.application.configuration.dto.request.*;
import me.namila.service.auth.domain.application.configuration.dto.response.*;
import me.namila.service.auth.domain.core.configuration.model.*;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.configuration.valueobject.MappingStrategy;
import me.namila.service.auth.domain.core.configuration.valueobject.ProviderType;
import org.mapstruct.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OIDCProviderConfigDtoMapper {
    
    // Request to Domain mappings
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "providerType", source = "providerType", qualifiedByName = "stringToProviderType")
    @Mapping(target = "configuration", source = "configuration")
    @Mapping(target = "attributeMapping", source = "attributeMapping")
    @Mapping(target = "roleMapping", source = "roleMapping")
    @Mapping(target = "jitProvisioning", source = "jitProvisioning")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", constant = "0L")
    OIDCProviderConfigAggregate toDomain(CreateOIDCProviderConfigRequest request);
    
    // Domain to Response mappings
    @Mapping(target = "providerId", source = "id.value")
    @Mapping(target = "providerType", source = "providerType", qualifiedByName = "providerTypeToString")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(target = "lastModifiedAt", source = "updatedAt", qualifiedByName = "localDateTimeToInstant")
    OIDCProviderConfigResponse toResponse(OIDCProviderConfigAggregate aggregate);
    
    @Mapping(target = "providerId", source = "id.value")
    @Mapping(target = "providerType", source = "providerType", qualifiedByName = "providerTypeToString")
    @Mapping(target = "configuration", source = "configuration")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToInstant")
    @Mapping(target = "lastModifiedAt", source = "updatedAt", qualifiedByName = "localDateTimeToInstant")
    OIDCProviderConfigDetailResponse toDetailResponse(OIDCProviderConfigAggregate aggregate);
    
    // Update method
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "providerName", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "providerType", source = "providerType", qualifiedByName = "stringToProviderType")
    void updateDomainFromRequest(UpdateOIDCProviderConfigRequest request, @MappingTarget OIDCProviderConfigAggregate aggregate);
    
    // Configuration mappings
    OIDCConfiguration toOIDCConfiguration(OIDCConfigurationRequest request);
    OIDCConfigurationResponse toOIDCConfigurationResponse(OIDCConfiguration configuration);
    
    AttributeMappingConfig toAttributeMappingConfig(AttributeMappingConfigRequest request);
    RoleMappingConfig toRoleMappingConfig(RoleMappingConfigRequest request);
    JITProvisioningConfig toJITProvisioningConfig(JITProvisioningConfigRequest request);
    
    AttributeMap toAttributeMap(AttributeMapRequest request);
    RoleMappingRule toRoleMappingRule(RoleMappingRuleRequest request);
    
    // Named conversion methods
    @Named("stringToProviderType")
    default ProviderType stringToProviderType(String providerType) {
        return providerType != null ? ProviderType.valueOf(providerType) : null;
    }
    
    @Named("providerTypeToString")
    default String providerTypeToString(ProviderType providerType) {
        return providerType != null ? providerType.name() : null;
    }
    
    @Named("localDateTimeToInstant")
    default Instant localDateTimeToInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
    
    default MappingStrategy stringToMappingStrategy(String strategy) {
        return strategy != null ? MappingStrategy.valueOf(strategy) : MappingStrategy.EXPLICIT;
    }
}
```

### Step 4: Create Application Service (with @Transactional)

#### File 13: `OIDCProviderConfigApplicationService.java`
**Location:** `authservice-domain/domain-application/src/main/java/me/namila/service/auth/domain/application/configuration/service/`

```java
package me.namila.service.auth.domain.application.configuration.service;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.domain.application.configuration.dto.request.CreateOIDCProviderConfigRequest;
import me.namila.service.auth.domain.application.configuration.dto.request.UpdateOIDCProviderConfigRequest;
import me.namila.service.auth.domain.application.configuration.dto.response.OIDCProviderConfigDetailResponse;
import me.namila.service.auth.domain.application.configuration.dto.response.OIDCProviderConfigResponse;
import me.namila.service.auth.domain.application.configuration.mapper.OIDCProviderConfigDtoMapper;
import me.namila.service.auth.domain.application.port.configuration.OIDCProviderConfigRepositoryPort;
import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfigAggregate;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.exception.DuplicateEntityException;
import me.namila.service.auth.domain.core.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Application service for OIDC provider configuration management.
 * Orchestrates domain operations and handles transaction boundaries.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OIDCProviderConfigApplicationService {
    
    private final OIDCProviderConfigRepositoryPort configRepository;
    private final OIDCProviderConfigDtoMapper mapper;
    
    /**
     * Create a new OIDC provider configuration.
     */
    @Transactional
    public OIDCProviderConfigResponse createConfig(CreateOIDCProviderConfigRequest request) {
        // Validate uniqueness
        if (configRepository.existsByProviderName(request.getProviderName())) {
            throw new DuplicateEntityException("OIDCProviderConfig", "providerName", request.getProviderName());
        }
        
        // Map to domain
        OIDCProviderConfigAggregate config = mapper.toDomain(request);
        config.setId(OIDCProviderConfigId.generate());
        
        // Save
        OIDCProviderConfigAggregate saved = configRepository.save(config);
        
        // Map to response
        return mapper.toResponse(saved);
    }
    
    /**
     * Get config by ID with full details.
     */
    @Transactional(readOnly = true)
    public OIDCProviderConfigDetailResponse getConfigById(UUID providerId) {
        OIDCProviderConfigAggregate config = configRepository.findById(OIDCProviderConfigId.of(providerId))
            .orElseThrow(() -> new ResourceNotFoundException("OIDCProviderConfig", providerId));
        
        return mapper.toDetailResponse(config);
    }
    
    /**
     * Update an existing config.
     */
    @Transactional
    public OIDCProviderConfigResponse updateConfig(UUID providerId, UpdateOIDCProviderConfigRequest request) {
        OIDCProviderConfigAggregate config = configRepository.findById(OIDCProviderConfigId.of(providerId))
            .orElseThrow(() -> new ResourceNotFoundException("OIDCProviderConfig", providerId));
        
        // Update fields
        mapper.updateDomainFromRequest(request, config);
        config.markAsUpdated();
        
        // Save
        OIDCProviderConfigAggregate updated = configRepository.save(config);
        
        return mapper.toResponse(updated);
    }
    
    /**
     * Delete a config.
     */
    @Transactional
    public void deleteConfig(UUID providerId) {
        if (!configRepository.findById(OIDCProviderConfigId.of(providerId)).isPresent()) {
            throw new ResourceNotFoundException("OIDCProviderConfig", providerId);
        }
        
        configRepository.deleteById(OIDCProviderConfigId.of(providerId));
    }
    
    /**
     * List all configs.
     */
    @Transactional(readOnly = true)
    public List<OIDCProviderConfigResponse> getAllConfigs() {
        return configRepository.findAll().stream()
            .map(mapper::toResponse)
            .toList();
    }
    
    /**
     * List enabled configs.
     */
    @Transactional(readOnly = true)
    public List<OIDCProviderConfigResponse> getEnabledConfigs() {
        return configRepository.findEnabledConfigs().stream()
            .map(mapper::toResponse)
            .toList();
    }
    
    /**
     * Enable a provider.
     */
    @Transactional
    public void enableProvider(UUID providerId) {
        OIDCProviderConfigAggregate config = configRepository.findById(OIDCProviderConfigId.of(providerId))
            .orElseThrow(() -> new ResourceNotFoundException("OIDCProviderConfig", providerId));
        
        config.enable();
        configRepository.save(config);
    }
    
    /**
     * Disable a provider.
     */
    @Transactional
    public void disableProvider(UUID providerId) {
        OIDCProviderConfigAggregate config = configRepository.findById(OIDCProviderConfigId.of(providerId))
            .orElseThrow(() -> new ResourceNotFoundException("OIDCProviderConfig", providerId));
        
        config.disable();
        configRepository.save(config);
    }
}
```

### Step 5: Create REST Controller

#### File 14: `OIDCProviderController.java`
**Location:** `authservice-application/src/main/java/me/namila/service/auth/application/controller/`

```java
package me.namila.service.auth.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.namila.service.auth.domain.application.configuration.dto.request.CreateOIDCProviderConfigRequest;
import me.namila.service.auth.domain.application.configuration.dto.request.UpdateOIDCProviderConfigRequest;
import me.namila.service.auth.domain.application.configuration.dto.response.OIDCProviderConfigDetailResponse;
import me.namila.service.auth.domain.application.configuration.dto.response.OIDCProviderConfigResponse;
import me.namila.service.auth.domain.application.configuration.service.OIDCProviderConfigApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for OIDC provider configuration management.
 */
@RestController
@RequestMapping("/api/v1/oidc-providers")
@RequiredArgsConstructor
@Tag(name = "OIDC Provider Configuration", description = "APIs for managing OIDC provider configurations")
public class OIDCProviderController {
    
    private final OIDCProviderConfigApplicationService configService;
    
    @PostMapping
    @Operation(summary = "Create a new OIDC provider configuration")
    public ResponseEntity<OIDCProviderConfigResponse> createProvider(
            @Valid @RequestBody CreateOIDCProviderConfigRequest request) {
        OIDCProviderConfigResponse response = configService.createConfig(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @Operation(summary = "List all OIDC provider configurations")
    public ResponseEntity<List<OIDCProviderConfigResponse>> getAllProviders(
            @RequestParam(required = false) Boolean enabled) {
        
        List<OIDCProviderConfigResponse> response = enabled != null && enabled
            ? configService.getEnabledConfigs()
            : configService.getAllConfigs();
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{providerId}")
    @Operation(summary = "Get OIDC provider configuration by ID")
    public ResponseEntity<OIDCProviderConfigDetailResponse> getProviderById(
            @PathVariable UUID providerId) {
        OIDCProviderConfigDetailResponse response = configService.getConfigById(providerId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{providerId}")
    @Operation(summary = "Update OIDC provider configuration")
    public ResponseEntity<OIDCProviderConfigResponse> updateProvider(
            @PathVariable UUID providerId,
            @Valid @RequestBody UpdateOIDCProviderConfigRequest request) {
        OIDCProviderConfigResponse response = configService.updateConfig(providerId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{providerId}")
    @Operation(summary = "Delete OIDC provider configuration")
    public ResponseEntity<Void> deleteProvider(@PathVariable UUID providerId) {
        configService.deleteConfig(providerId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{providerId}/enable")
    @Operation(summary = "Enable OIDC provider")
    public ResponseEntity<Void> enableProvider(@PathVariable UUID providerId) {
        configService.enableProvider(providerId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{providerId}/disable")
    @Operation(summary = "Disable OIDC provider")
    public ResponseEntity<Void> disableProvider(@PathVariable UUID providerId) {
        configService.disableProvider(providerId);
        return ResponseEntity.noContent().build();
    }
}
```

## Testing

After creating all files, run the following commands:

```bash
# Build the project
cd /home/runner/work/auth-service/auth-service/auth-service
./gradlew clean build

# Run tests
./gradlew test

# View test report
# Check: build/reports/tests/test/index.html
```

## Validation Steps

1. **Compile Check**: Ensure all files compile without errors
2. **Test Execution**: Run unit tests
3. **API Testing**: Test endpoints using Swagger UI or Postman
4. **Code Coverage**: Check JaCoCo report

## Next Steps

After Task 1 is complete and tested:
- Move to Task 2: OIDC Authentication Flow
- Implement OAuth2 integration with Spring Security
- Add PKCE support
- Implement JIT provisioning

## Notes

- All DTOs follow validation best practices with Jakarta Validation
- Application service uses `@Transactional` for proper transaction management
- Mapper uses MapStruct for automatic DTO/Domain conversion
- Controller follows REST best practices with proper HTTP status codes
- Security: Client secrets are never returned in responses

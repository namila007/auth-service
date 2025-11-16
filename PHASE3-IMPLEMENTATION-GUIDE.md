# Phase 3: OIDC Integration Implementation Guide

## Overview

This document provides a comprehensive implementation guide for Phase 3: OIDC Integration. The implementation builds on existing Phase 1 and Phase 2 infrastructure.

## Prerequisites (Already Implemented ✅)

- ✅ Domain models: `OIDCProviderConfigAggregate`, `UserAggregate`, `FederatedIdentityEntity`
- ✅ JPA entities and repositories for configuration context
- ✅ Repository ports and adapters
- ✅ MapStruct mappers for JPA-Domain conversion
- ✅ JWT token provider with RS256 support
- ✅ Database schema with proper indexes
- ✅ Spring Security OAuth2 Client dependency

## Architecture Overview

```
authservice-domain/
├── domain-core/
│   └── configuration/model/        # ✅ Already exists
│       ├── OIDCProviderConfigAggregate.java
│       ├── OIDCConfiguration.java
│       ├── AttributeMappingConfig.java
│       ├── RoleMappingConfig.java
│       └── JITProvisioningConfig.java
│
├── domain-application/
│   ├── configuration/              # 🔨 TO IMPLEMENT
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   ├── CreateOIDCProviderConfigRequest.java
│   │   │   │   ├── UpdateOIDCProviderConfigRequest.java
│   │   │   │   ├── OIDCConfigurationRequest.java
│   │   │   │   ├── AttributeMappingConfigRequest.java
│   │   │   │   ├── RoleMappingConfigRequest.java
│   │   │   │   └── JITProvisioningConfigRequest.java
│   │   │   └── response/
│   │   │       ├── OIDCProviderConfigResponse.java
│   │   │       ├── OIDCProviderConfigDetailResponse.java
│   │   │       └── OIDCProviderConfigSummaryResponse.java
│   │   ├── mapper/
│   │   │   └── OIDCProviderConfigDtoMapper.java
│   │   └── service/
│   │       └── OIDCProviderConfigApplicationService.java
│   │
│   └── auth/                       # 🔨 TO IMPLEMENT
│       ├── dto/
│       │   ├── request/
│       │   │   ├── OIDCInitiateRequest.java
│       │   │   ├── OIDCCallbackRequest.java
│       │   │   └── TokenRefreshRequest.java
│       │   └── response/
│       │       ├── OIDCInitiateResponse.java
│       │       ├── AuthenticationResponse.java
│       │       └── TokenRefreshResponse.java
│       └── service/
│           ├── OIDCAuthenticationService.java
│           ├── JITProvisioningService.java
│           ├── AttributeTransformationService.java
│           └── RoleMappingService.java
│
authservice-data/
└── configuration/                  # ✅ Already exists
    ├── entity/OIDCProviderConfigJpaEntity.java
    ├── repository/OIDCProviderConfigJpaRepository.java
    ├── adapter/OIDCProviderConfigRepositoryAdapter.java
    └── mapper/OIDCProviderConfigEntityMapper.java
│
authservice-application/
└── controller/                     # 🔨 TO IMPLEMENT
    ├── OIDCProviderController.java
    └── AuthenticationController.java
```

## Implementation Tasks

### Task 1: OIDC Provider Configuration APIs (Days 1-2)

#### 1.1 Create DTOs

**Location:** `authservice-domain/domain-application/src/main/java/me/namila/service/auth/domain/application/configuration/dto/`

**Files to create:**

1. **request/CreateOIDCProviderConfigRequest.java**
```java
package me.namila.service.auth.domain.application.configuration.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOIDCProviderConfigRequest {
    
    @NotBlank(message = "Provider name is required")
    private String providerName;
    
    @NotBlank(message = "Display name is required")
    private String displayName;
    
    @NotNull(message = "Provider type is required")
    private String providerType; // OIDC, SAML
    
    @Builder.Default
    private Boolean enabled = true;
    
    @Valid
    @NotNull(message = "OIDC configuration is required")
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

2. **request/OIDCConfigurationRequest.java**
```java
package me.namila.service.auth.domain.application.configuration.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    private Set<String> scopes;
    
    private Map<String, String> additionalParameters;
}
```

3. **response/OIDCProviderConfigResponse.java**
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
    
    // Note: Configuration details excluded for security
    private Map<String, Object> metadata;
}
```

#### 1.2 Create Application Service

**Location:** `authservice-domain/domain-application/src/main/java/me/namila/service/auth/domain/application/configuration/service/`

**File: OIDCProviderConfigApplicationService.java**

```java
package me.namila.service.auth.domain.application.configuration.service;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.domain.application.configuration.dto.request.CreateOIDCProviderConfigRequest;
import me.namila.service.auth.domain.application.configuration.dto.request.UpdateOIDCProviderConfigRequest;
import me.namila.service.auth.domain.application.configuration.dto.response.OIDCProviderConfigResponse;
import me.namila.service.auth.domain.application.configuration.dto.response.OIDCProviderConfigDetailResponse;
import me.namila.service.auth.domain.application.configuration.mapper.OIDCProviderConfigDtoMapper;
import me.namila.service.auth.domain.application.dto.response.PagedResponse;
import me.namila.service.auth.domain.application.port.configuration.OIDCProviderConfigRepositoryPort;
import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfigAggregate;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.exception.DuplicateEntityException;
import me.namila.service.auth.domain.core.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

#### 1.3 Create REST Controller

**Location:** `authservice-application/src/main/java/me/namila/service/auth/application/controller/`

**File: OIDCProviderController.java**

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

### Task 2: OIDC Authentication Flow (Days 3-5)

#### 2.1 Spring Security OAuth2 Client Configuration

**Location:** `authservice-application/src/main/java/me/namila/service/auth/application/security/`

**File: OAuth2ClientConfig.java**

```java
package me.namila.service.auth.application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;

@Configuration
public class OAuth2ClientConfig {
    
    /**
     * Dynamic client registration repository that loads configs from database.
     */
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        // TODO: Implement dynamic client registration loader
        // This will load OIDC provider configs from database and convert to Spring Security ClientRegistration
        return new InMemoryClientRegistrationRepository();
    }
    
    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository();
    }
}
```

#### 2.2 OIDC Authentication Service

**Location:** `authservice-domain/domain-application/src/main/java/me/namila/service/auth/domain/application/auth/service/`

**File: OIDCAuthenticationService.java**

```java
package me.namila.service.auth.domain.application.auth.service;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.application.security.JwtTokenProvider;
import me.namila.service.auth.domain.application.auth.dto.request.OIDCCallbackRequest;
import me.namila.service.auth.domain.application.auth.dto.request.OIDCInitiateRequest;
import me.namila.service.auth.domain.application.auth.dto.response.AuthenticationResponse;
import me.namila.service.auth.domain.application.auth.dto.response.OIDCInitiateResponse;
import me.namila.service.auth.domain.application.port.configuration.OIDCProviderConfigRepositoryPort;
import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfigAggregate;
import me.namila.service.auth.domain.core.configuration.model.id.OIDCProviderConfigId;
import me.namila.service.auth.domain.core.exception.ResourceNotFoundException;
import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OIDCAuthenticationService {
    
    private final OIDCProviderConfigRepositoryPort providerConfigRepository;
    private final JITProvisioningService jitProvisioningService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ClientRegistrationRepository clientRegistrationRepository;
    
    /**
     * Initiate OIDC authentication flow with PKCE.
     */
    public OIDCInitiateResponse initiateAuthentication(OIDCInitiateRequest request) {
        // Load provider config
        OIDCProviderConfigAggregate providerConfig = providerConfigRepository
            .findById(OIDCProviderConfigId.of(request.getProviderId()))
            .orElseThrow(() -> new ResourceNotFoundException("OIDCProviderConfig", request.getProviderId()));
        
        if (!providerConfig.getEnabled()) {
            throw new IllegalStateException("OIDC provider is disabled");
        }
        
        // Generate PKCE code verifier and challenge
        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);
        
        // Generate state and nonce
        String state = generateRandomString(32);
        String nonce = generateRandomString(32);
        
        // Build authorization URL
        String authorizationUrl = UriComponentsBuilder
            .fromHttpUrl(providerConfig.getConfiguration().getAuthorizationUri())
            .queryParam("client_id", providerConfig.getConfiguration().getClientId())
            .queryParam("response_type", "code")
            .queryParam("redirect_uri", request.getRedirectUri())
            .queryParam("scope", String.join(" ", providerConfig.getConfiguration().getScopes()))
            .queryParam("state", state)
            .queryParam("nonce", nonce)
            .queryParam("code_challenge", codeChallenge)
            .queryParam("code_challenge_method", "S256")
            .build()
            .toUriString();
        
        // TODO: Store state, nonce, code_verifier in Redis with expiration (5 minutes)
        
        return OIDCInitiateResponse.builder()
            .authorizationUrl(authorizationUrl)
            .state(state)
            .codeVerifier(codeVerifier)
            .build();
    }
    
    /**
     * Handle OIDC callback and perform JIT provisioning.
     */
    @Transactional
    public AuthenticationResponse handleCallback(OIDCCallbackRequest request) {
        // TODO: Validate state from Redis
        
        // Load provider config
        OIDCProviderConfigAggregate providerConfig = providerConfigRepository
            .findById(OIDCProviderConfigId.of(request.getProviderId()))
            .orElseThrow(() -> new ResourceNotFoundException("OIDCProviderConfig", request.getProviderId()));
        
        // TODO: Exchange authorization code for tokens using code_verifier
        // This requires HTTP client call to provider's token endpoint
        
        // TODO: Validate ID token (signature, issuer, audience, nonce, expiration)
        
        // TODO: Extract claims from ID token
        
        // TODO: Perform JIT provisioning
        // UserAggregate user = jitProvisioningService.provisionUser(providerConfig, idToken);
        
        // TODO: Generate internal JWT token
        // String accessToken = jwtTokenProvider.generateToken(user.getId().getValue(), ...);
        
        // TODO: Generate refresh token
        
        return AuthenticationResponse.builder()
            // .accessToken(accessToken)
            // .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(1800)
            .build();
    }
    
    private String generateCodeVerifier() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    private String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    private String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, length);
    }
}
```

### Task 3: JIT Provisioning Service (Days 6-7)

**Location:** `authservice-domain/domain-application/src/main/java/me/namila/service/auth/domain/application/auth/service/`

**File: JITProvisioningService.java**

```java
package me.namila.service.auth.domain.application.auth.service;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.domain.application.port.identity.FederatedIdentityRepositoryPort;
import me.namila.service.auth.domain.application.port.identity.UserRepositoryPort;
import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfigAggregate;
import me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity;
import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import me.namila.service.auth.domain.core.identity.model.UserProfileEntity;
import me.namila.service.auth.domain.core.identity.model.id.FederatedIdentityId;
import me.namila.service.auth.domain.core.identity.model.id.UserId;
import me.namila.service.auth.domain.core.identity.valueobject.EmailValue;
import me.namila.service.auth.domain.core.identity.valueobject.UsernameValue;
import me.namila.service.auth.domain.core.identity.valueobject.UserStatus;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JITProvisioningService {
    
    private final UserRepositoryPort userRepository;
    private final FederatedIdentityRepositoryPort federatedIdentityRepository;
    private final AttributeTransformationService attributeTransformationService;
    private final RoleMappingService roleMappingService;
    
    /**
     * Provision or update user from OIDC ID token claims.
     */
    @Transactional
    public UserAggregate provisionUser(
            OIDCProviderConfigAggregate providerConfig,
            OidcIdToken idToken,
            Set<String> externalGroups) {
        
        // Extract subject ID (immutable identifier)
        String subjectId = extractSubjectId(idToken, providerConfig);
        String issuer = idToken.getIssuer().toString();
        
        // Check if federated identity exists
        Optional<FederatedIdentityEntity> existingIdentity = 
            federatedIdentityRepository.findByProviderIdAndSubjectId(
                providerConfig.getId().getValue(), 
                subjectId
            );
        
        UserAggregate user;
        
        if (existingIdentity.isPresent()) {
            // Update existing user
            user = userRepository.findById(existingIdentity.get().getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found for federated identity"));
            
            if (providerConfig.getJitProvisioning().getUpdateUsers()) {
                updateUserFromClaims(user, idToken, providerConfig);
                user = userRepository.save(user);
            }
            
            // Update last synced timestamp
            existingIdentity.get().updateSyncTimestamp();
            federatedIdentityRepository.save(existingIdentity.get());
            
        } else {
            // Create new user
            if (!providerConfig.getJitProvisioning().getCreateUsers()) {
                throw new IllegalStateException("JIT user creation is disabled for provider: " + providerConfig.getProviderName());
            }
            
            user = createUserFromClaims(idToken, providerConfig);
            user = userRepository.save(user);
            
            // Link federated identity
            FederatedIdentityEntity federatedIdentity = FederatedIdentityEntity.builder()
                .id(FederatedIdentityId.generate())
                .userId(user.getId())
                .providerId(providerConfig.getId().getValue())
                .subjectId(subjectId)
                .issuer(issuer)
                .linkedAt(Instant.now())
                .build();
            
            federatedIdentityRepository.save(federatedIdentity);
        }
        
        // Sync roles based on group mappings
        if (providerConfig.getJitProvisioning().getSyncGroups() && externalGroups != null) {
            roleMappingService.syncUserRoles(user, externalGroups, providerConfig);
        }
        
        // Apply default roles
        applyDefaultRoles(user, providerConfig);
        
        // TODO: Audit the provisioning event
        
        return user;
    }
    
    private String extractSubjectId(OidcIdToken idToken, OIDCProviderConfigAggregate providerConfig) {
        String subjectAttribute = providerConfig.getAttributeMapping().getSubjectAttribute();
        if (subjectAttribute == null) {
            subjectAttribute = "sub";
        }
        return idToken.getClaimAsString(subjectAttribute);
    }
    
    private UserAggregate createUserFromClaims(
            OidcIdToken idToken,
            OIDCProviderConfigAggregate providerConfig) {
        
        // Transform attributes using mapping config
        Map<String, Object> transformedAttributes = attributeTransformationService
            .transformAttributes(idToken.getClaims(), providerConfig);
        
        // Extract standard attributes
        String email = (String) transformedAttributes.get("email");
        String name = (String) transformedAttributes.get("name");
        String firstName = (String) transformedAttributes.get("given_name");
        String lastName = (String) transformedAttributes.get("family_name");
        
        // Build user
        UserAggregate user = UserAggregate.builder()
            .id(UserId.generate())
            .username(UsernameValue.of(email)) // Use email as username
            .email(EmailValue.of(email))
            .status(UserStatus.ACTIVE)
            .build();
        
        // Build profile
        UserProfileEntity profile = UserProfileEntity.builder()
            .firstName(firstName)
            .lastName(lastName)
            .displayName(name)
            .build();
        
        user.setProfile(profile);
        
        return user;
    }
    
    private void updateUserFromClaims(
            UserAggregate user,
            OidcIdToken idToken,
            OIDCProviderConfigAggregate providerConfig) {
        
        // Transform attributes
        Map<String, Object> transformedAttributes = attributeTransformationService
            .transformAttributes(idToken.getClaims(), providerConfig);
        
        // Update email if changed
        String email = (String) transformedAttributes.get("email");
        if (email != null && !email.equals(user.getEmail().getValue())) {
            user.updateEmail(EmailValue.of(email));
        }
        
        // Update profile attributes
        if (user.getProfile() != null) {
            String firstName = (String) transformedAttributes.get("given_name");
            String lastName = (String) transformedAttributes.get("family_name");
            String name = (String) transformedAttributes.get("name");
            
            if (firstName != null) user.getProfile().setFirstName(firstName);
            if (lastName != null) user.getProfile().setLastName(lastName);
            if (name != null) user.getProfile().setDisplayName(name);
        }
    }
    
    private void applyDefaultRoles(UserAggregate user, OIDCProviderConfigAggregate providerConfig) {
        // TODO: Assign default roles from providerConfig.getJitProvisioning().getDefaultRoles()
    }
}
```

### Task 4: Attribute Transformation Service (Day 8)

**File: AttributeTransformationService.java**

```java
package me.namila.service.auth.domain.application.auth.service;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.domain.core.configuration.model.AttributeMap;
import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfigAggregate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttributeTransformationService {
    
    /**
     * Transform OIDC claims to internal user attributes.
     */
    public Map<String, Object> transformAttributes(
            Map<String, Object> claims,
            OIDCProviderConfigAggregate providerConfig) {
        
        Map<String, Object> transformed = new HashMap<>();
        
        // Map standard attributes
        var attrMapping = providerConfig.getAttributeMapping();
        
        if (attrMapping.getEmailAttribute() != null) {
            transformed.put("email", claims.get(attrMapping.getEmailAttribute()));
        }
        
        if (attrMapping.getNameAttribute() != null) {
            transformed.put("name", claims.get(attrMapping.getNameAttribute()));
        }
        
        // Map custom attributes with transformation rules
        if (attrMapping.getCustomMappings() != null) {
            for (AttributeMap mapping : attrMapping.getCustomMappings()) {
                Object externalValue = claims.get(mapping.getExternalAttribute());
                Object transformedValue = applyTransformation(externalValue, mapping.getTransformationRule());
                transformed.put(mapping.getInternalAttribute(), transformedValue);
            }
        }
        
        return transformed;
    }
    
    private Object applyTransformation(Object value, String rule) {
        if (rule == null || rule.isBlank()) {
            return value;
        }
        
        // TODO: Implement expression evaluation
        // This could use JavaScript engine or Spring Expression Language (SpEL)
        
        return value;
    }
}
```

### Task 5: Role Mapping Service (Day 9)

**File: RoleMappingService.java**

```java
package me.namila.service.auth.domain.application.auth.service;

import lombok.RequiredArgsConstructor;
import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfigAggregate;
import me.namila.service.auth.domain.core.configuration.model.RoleMappingRule;
import me.namila.service.auth.domain.core.configuration.valueobject.MappingStrategy;
import me.namila.service.auth.domain.core.identity.model.UserAggregate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RoleMappingService {
    
    /**
     * Map external groups to internal roles and synchronize user roles.
     */
    @Transactional
    public void syncUserRoles(
            UserAggregate user,
            Set<String> externalGroups,
            OIDCProviderConfigAggregate providerConfig) {
        
        Set<String> mappedRoles = new HashSet<>();
        
        MappingStrategy strategy = providerConfig.getRoleMapping().getMappingStrategy();
        
        for (String externalGroup : externalGroups) {
            Set<String> roles = mapGroupToRoles(externalGroup, providerConfig);
            mappedRoles.addAll(roles);
        }
        
        // TODO: Synchronize roles
        // - Remove federated roles not in mapped set
        // - Add new mapped roles
        // - Use UserRoleAssignmentRepository
    }
    
    private Set<String> mapGroupToRoles(
            String externalGroup,
            OIDCProviderConfigAggregate providerConfig) {
        
        Set<String> roles = new HashSet<>();
        
        for (RoleMappingRule rule : providerConfig.getRoleMapping().getMappingRules()) {
            if (matchesRule(externalGroup, rule)) {
                roles.add(rule.getInternalRole());
            }
        }
        
        return roles;
    }
    
    private boolean matchesRule(String externalGroup, RoleMappingRule rule) {
        // Pattern-based matching (regex)
        Pattern pattern = Pattern.compile(rule.getExternalGroup());
        return pattern.matcher(externalGroup).matches();
    }
}
```

## Testing Guide

### Unit Tests

Create test classes for each service:

```
src/test/java/me/namila/service/auth/domain/application/
├── configuration/service/
│   └── OIDCProviderConfigApplicationServiceTest.java
└── auth/service/
    ├── OIDCAuthenticationServiceTest.java
    ├── JITProvisioningServiceTest.java
    ├── AttributeTransformationServiceTest.java
    └── RoleMappingServiceTest.java
```

### Integration Tests

```
src/test/java/me/namila/service/auth/integration/
├── OIDCProviderConfigIntegrationTest.java
├── OIDCAuthenticationFlowIntegrationTest.java
└── JITProvisioningIntegrationTest.java
```

## Security Considerations

1. **Client Secret Encryption**: Store OIDC client secrets encrypted in database
2. **State/Nonce Storage**: Use Redis with TTL for PKCE state management
3. **Token Validation**: Validate ID token signature, issuer, audience, nonce, expiration
4. **Rate Limiting**: Implement rate limiting on authentication endpoints
5. **Audit Logging**: Log all authentication attempts and provisioning events
6. **Error Handling**: Never expose sensitive information in error responses

## Configuration Properties

Add to `application.yml`:

```yaml
auth-service:
  oidc:
    callback-base-url: ${OIDC_CALLBACK_BASE_URL:http://localhost:8080}
    state-ttl: 300 # 5 minutes
    pkce-enabled: true
  
  jit-provisioning:
    enabled: true
    default-user-status: ACTIVE
    
  security:
    client-secret-encryption-key: ${CLIENT_SECRET_KEY}
```

## Next Steps

1. Implement OIDC Provider Configuration APIs (Task 1)
2. Test configuration CRUD operations
3. Implement OAuth2 authentication flow (Task 2)
4. Implement JIT provisioning (Task 3)
5. Add comprehensive tests
6. Security review and audit logging
7. Documentation and API examples

## Estimated Timeline

- **Week 1**: Tasks 1-2 (Configuration + OAuth2 Flow)
- **Week 2**: Tasks 3-5 (JIT + Transformation + Role Mapping)
- **Week 3**: Task 6-7 (Identity Management + Testing)

Total: 2-3 weeks for complete implementation with testing.

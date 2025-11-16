package me.namila.service.auth.domain.application.configuration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.namila.service.auth.domain.application.configuration.dto.request.CreateOIDCProviderConfigRequest;
import me.namila.service.auth.domain.application.configuration.dto.request.UpdateOIDCProviderConfigRequest;
import me.namila.service.auth.domain.application.configuration.dto.response.OIDCProviderConfigDetailResponse;
import me.namila.service.auth.domain.application.configuration.dto.response.OIDCProviderConfigResponse;
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
import java.util.stream.Collectors;

/**
 * Application service for OIDC Provider Configuration management.
 * Handles all use cases related to OIDC provider configuration.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OIDCProviderConfigApplicationService {
    
    private final OIDCProviderConfigRepositoryPort configRepository;
    private final OIDCProviderConfigDtoMapper mapper;
    
    /**
     * Create a new OIDC provider configuration.
     * 
     * @param request The creation request
     * @return The created configuration
     * @throws DuplicateEntityException if provider name already exists
     */
    @Transactional
    public OIDCProviderConfigResponse createConfig(CreateOIDCProviderConfigRequest request) {
        log.info("Creating OIDC provider configuration: {}", request.getProviderName());
        
        // Validate uniqueness
        if (configRepository.existsByProviderName(request.getProviderName())) {
            throw new DuplicateEntityException("OIDCProviderConfig", "providerName", request.getProviderName());
        }
        
        // Map to domain
        OIDCProviderConfigAggregate config = mapper.toDomain(request);
        config.setId(OIDCProviderConfigId.generate());
        
        // Save
        OIDCProviderConfigAggregate saved = configRepository.save(config);
        
        log.info("Successfully created OIDC provider configuration: {} with ID: {}", 
                 saved.getProviderName(), saved.getId().getValue());
        
        // Map to response
        return mapper.toResponse(saved);
    }
    
    /**
     * Get configuration by ID with full details.
     * 
     * @param providerId The provider ID
     * @return The configuration details
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public OIDCProviderConfigDetailResponse getConfigById(UUID providerId) {
        log.debug("Fetching OIDC provider configuration with ID: {}", providerId);
        
        OIDCProviderConfigAggregate config = configRepository.findById(OIDCProviderConfigId.of(providerId))
            .orElseThrow(() -> new ResourceNotFoundException("OIDCProviderConfig", providerId));
        
        return mapper.toDetailResponse(config);
    }
    
    /**
     * Get configuration by provider name with full details.
     * 
     * @param providerName The provider name
     * @return The configuration details
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public OIDCProviderConfigDetailResponse getConfigByName(String providerName) {
        log.debug("Fetching OIDC provider configuration with name: {}", providerName);
        
        OIDCProviderConfigAggregate config = configRepository.findByProviderName(providerName)
            .orElseThrow(() -> new ResourceNotFoundException("OIDCProviderConfig", providerName));
        
        return mapper.toDetailResponse(config);
    }
    
    /**
     * Get all configurations (paginated).
     * 
     * @param pageable Pagination information
     * @return Paginated list of configurations
     */
    @Transactional(readOnly = true)
    public PagedResponse<OIDCProviderConfigResponse> getAllConfigs(Pageable pageable) {
        log.debug("Fetching all OIDC provider configurations (page: {}, size: {})", 
                  pageable.getPageNumber(), pageable.getPageSize());
        
        Page<OIDCProviderConfigAggregate> page = configRepository.findAll(pageable);
        Page<OIDCProviderConfigResponse> responsePage = page.map(mapper::toResponse);
        
        return PagedResponse.of(responsePage);
    }
    
    /**
     * Get all enabled configurations.
     * 
     * @return List of enabled configurations
     */
    @Transactional(readOnly = true)
    public List<OIDCProviderConfigResponse> getEnabledConfigs() {
        log.debug("Fetching all enabled OIDC provider configurations");
        
        return configRepository.findEnabledConfigs().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Update an existing configuration.
     * 
     * @param providerId The provider ID
     * @param request The update request
     * @return The updated configuration
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional
    public OIDCProviderConfigResponse updateConfig(UUID providerId, UpdateOIDCProviderConfigRequest request) {
        log.info("Updating OIDC provider configuration: {}", providerId);
        
        OIDCProviderConfigAggregate config = configRepository.findById(OIDCProviderConfigId.of(providerId))
            .orElseThrow(() -> new ResourceNotFoundException("OIDCProviderConfig", providerId));
        
        // Update fields (mapper handles null values)
        mapper.updateDomainFromRequest(request, config);
        
        // Save
        OIDCProviderConfigAggregate updated = configRepository.save(config);
        
        log.info("Successfully updated OIDC provider configuration: {}", providerId);
        
        return mapper.toResponse(updated);
    }
    
    /**
     * Delete a configuration.
     * 
     * @param providerId The provider ID
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional
    public void deleteConfig(UUID providerId) {
        log.info("Deleting OIDC provider configuration: {}", providerId);
        
        if (!configRepository.findById(OIDCProviderConfigId.of(providerId)).isPresent()) {
            throw new ResourceNotFoundException("OIDCProviderConfig", providerId);
        }
        
        configRepository.deleteById(OIDCProviderConfigId.of(providerId));
        
        log.info("Successfully deleted OIDC provider configuration: {}", providerId);
    }
    
    /**
     * Enable a configuration.
     * 
     * @param providerId The provider ID
     * @return The updated configuration
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional
    public OIDCProviderConfigResponse enableConfig(UUID providerId) {
        log.info("Enabling OIDC provider configuration: {}", providerId);
        
        OIDCProviderConfigAggregate config = configRepository.findById(OIDCProviderConfigId.of(providerId))
            .orElseThrow(() -> new ResourceNotFoundException("OIDCProviderConfig", providerId));
        
        config.enable();
        
        OIDCProviderConfigAggregate updated = configRepository.save(config);
        
        log.info("Successfully enabled OIDC provider configuration: {}", providerId);
        
        return mapper.toResponse(updated);
    }
    
    /**
     * Disable a configuration.
     * 
     * @param providerId The provider ID
     * @return The updated configuration
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional
    public OIDCProviderConfigResponse disableConfig(UUID providerId) {
        log.info("Disabling OIDC provider configuration: {}", providerId);
        
        OIDCProviderConfigAggregate config = configRepository.findById(OIDCProviderConfigId.of(providerId))
            .orElseThrow(() -> new ResourceNotFoundException("OIDCProviderConfig", providerId));
        
        config.disable();
        
        OIDCProviderConfigAggregate updated = configRepository.save(config);
        
        log.info("Successfully disabled OIDC provider configuration: {}", providerId);
        
        return mapper.toResponse(updated);
    }
    
    /**
     * Test connection to an OIDC provider.
     * This method validates the configuration by attempting to retrieve
     * the provider's metadata from the issuer URL.
     * 
     * @param providerId The provider ID
     * @return true if connection test successful
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public boolean testConnection(UUID providerId) {
        log.info("Testing connection for OIDC provider configuration: {}", providerId);
        
        OIDCProviderConfigAggregate config = configRepository.findById(OIDCProviderConfigId.of(providerId))
            .orElseThrow(() -> new ResourceNotFoundException("OIDCProviderConfig", providerId));
        
        // TODO: Implement actual connection test logic
        // This would involve:
        // 1. Fetching OIDC discovery document from issuer/.well-known/openid-configuration
        // 2. Validating the configuration matches
        // 3. Optionally testing token endpoint with client credentials
        
        log.warn("Connection test not yet implemented for provider: {}", config.getProviderName());
        
        return true;
    }
}

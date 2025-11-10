package me.namila.service.auth.domain.application.port.configuration;

import me.namila.service.auth.domain.core.configuration.model.OIDCProviderConfigAggregate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for OIDCProviderConfig aggregate root.
 */
public interface OIDCProviderConfigRepositoryPort {
    
    /**
     * Save or update an OIDC provider config.
     * @param config The config to save
     * @return The saved config
     */
    OIDCProviderConfigAggregate save(OIDCProviderConfigAggregate config);
    
    /**
     * Find a config by ID.
     * @param providerId The provider ID
     * @return Optional containing the config if found
     */
    Optional<OIDCProviderConfigAggregate> findById(UUID providerId);
    
    /**
     * Find a config by provider name.
     * @param providerName The provider name
     * @return Optional containing the config if found
     */
    Optional<OIDCProviderConfigAggregate> findByProviderName(String providerName);
    
    /**
     * Check if a provider name exists.
     * @param providerName The provider name
     * @return true if provider name exists
     */
    boolean existsByProviderName(String providerName);
    
    /**
     * Find all configs.
     * @return List of all configs
     */
    List<OIDCProviderConfigAggregate> findAll();
    
    /**
     * Find enabled configs.
     * @return List of enabled configs
     */
    List<OIDCProviderConfigAggregate> findEnabledConfigs();
    
    /**
     * Delete a config by ID.
     * @param providerId The provider ID
     */
    void deleteById(UUID providerId);
}


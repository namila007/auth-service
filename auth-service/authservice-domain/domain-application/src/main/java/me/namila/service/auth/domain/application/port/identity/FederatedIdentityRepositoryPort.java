package me.namila.service.auth.domain.application.port.identity;

import me.namila.service.auth.domain.core.identity.model.FederatedIdentityEntity;
import me.namila.service.auth.domain.core.identity.model.id.FederatedIdentityId;
import me.namila.service.auth.domain.core.identity.model.id.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for FederatedIdentity entity.
 */
public interface FederatedIdentityRepositoryPort {
    
    /**
     * Save or update a federated identity.
     * @param federatedIdentity The federated identity to save
     * @return The saved federated identity
     */
    FederatedIdentityEntity save(FederatedIdentityEntity federatedIdentity);
    
    /**
     * Find a federated identity by ID.
     * @param federatedIdentityId The federated identity ID
     * @return Optional containing the federated identity if found
     */
    Optional<FederatedIdentityEntity> findById(FederatedIdentityId federatedIdentityId);
    
    /**
     * Find all federated identities for a user.
     * @param userId The user ID
     * @return List of federated identities for the user
     */
    List<FederatedIdentityEntity> findByUserId(UserId userId);
    
    /**
     * Find a federated identity by provider and subject.
     * @param providerId The provider ID
     * @param subjectId The subject ID
     * @return Optional containing the federated identity if found
     */
    Optional<FederatedIdentityEntity> findByProviderAndSubject(UUID providerId, String subjectId);
    
    /**
     * Check if a federated identity exists for provider and subject.
     * @param providerId The provider ID
     * @param subjectId The subject ID
     * @return true if exists
     */
    boolean existsByProviderAndSubject(UUID providerId, String subjectId);
    
    /**
     * Delete a federated identity by ID.
     * @param federatedIdentityId The federated identity ID
     */
    void deleteById(FederatedIdentityId federatedIdentityId);
    
    /**
     * Delete all federated identities for a user.
     * @param userId The user ID
     */
    void deleteByUserId(UserId userId);
}


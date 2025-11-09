package me.namila.service.auth.domain.application.port.authorization;

import me.namila.service.auth.domain.core.authorization.model.Policy;
import me.namila.service.auth.domain.core.authorization.valueobject.PolicyType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Policy aggregate root.
 */
public interface PolicyRepositoryPort {
    
    /**
     * Save or update a policy.
     * @param policy The policy to save
     * @return The saved policy
     */
    Policy save(Policy policy);
    
    /**
     * Find a policy by ID.
     * @param policyId The policy ID
     * @return Optional containing the policy if found
     */
    Optional<Policy> findById(UUID policyId);
    
    /**
     * Find a policy by name.
     * @param policyName The policy name
     * @return Optional containing the policy if found
     */
    Optional<Policy> findByPolicyName(String policyName);
    
    /**
     * Find all policies.
     * @return List of all policies
     */
    List<Policy> findAll();
    
    /**
     * Find enabled policies.
     * @return List of enabled policies
     */
    List<Policy> findEnabledPolicies();
    
    /**
     * Find policies by type.
     * @param policyType The policy type
     * @return List of policies with the given type
     */
    List<Policy> findByPolicyType(PolicyType policyType);
    
    /**
     * Find enabled policies by type.
     * @param policyType The policy type
     * @return List of enabled policies with the given type
     */
    List<Policy> findEnabledByPolicyType(PolicyType policyType);
    
    /**
     * Delete a policy by ID.
     * @param policyId The policy ID
     */
    void deleteById(UUID policyId);
}


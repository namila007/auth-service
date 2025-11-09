package me.namila.service.auth.data.authorization.repository;

import me.namila.service.auth.data.authorization.entity.PolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for PolicyEntity.
 */
@Repository
public interface PolicyJpaRepository extends JpaRepository<PolicyEntity, UUID> {
    
    Optional<PolicyEntity> findByPolicyName(String policyName);
    
    List<PolicyEntity> findByEnabledTrue();
    
    List<PolicyEntity> findByPolicyType(String policyType);
    
    List<PolicyEntity> findByPolicyTypeAndEnabledTrue(String policyType);
}


package me.namila.service.auth.data.authorization.repository;

import me.namila.service.auth.data.authorization.entity.PolicyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for PolicyJpaEntity.
 */
@Repository
public interface PolicyJpaRepository extends JpaRepository<PolicyJpaEntity, UUID> {
    
    Optional<PolicyJpaEntity> findByPolicyName(String policyName);
    
    List<PolicyJpaEntity> findByEnabledTrue();
    
    List<PolicyJpaEntity> findByPolicyType(String policyType);
    
    List<PolicyJpaEntity> findByPolicyTypeAndEnabledTrue(String policyType);
}


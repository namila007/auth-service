package me.namila.service.auth.data.identity.repository;

import me.namila.service.auth.data.identity.entity.FederatedIdentityJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for FederatedIdentityJpaEntity.
 */
@Repository
public interface FederatedIdentityJpaRepository extends JpaRepository<FederatedIdentityJpaEntity, UUID> {
    
    List<FederatedIdentityJpaEntity> findByUser_UserId(UUID userId);
    
    Optional<FederatedIdentityJpaEntity> findByProviderIdAndSubjectId(UUID providerId, String subjectId);
    
    boolean existsByProviderIdAndSubjectId(UUID providerId, String subjectId);
    
    void deleteByUser_UserId(UUID userId);
}


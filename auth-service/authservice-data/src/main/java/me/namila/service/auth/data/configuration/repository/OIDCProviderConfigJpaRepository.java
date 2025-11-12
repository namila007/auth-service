package me.namila.service.auth.data.configuration.repository;

import me.namila.service.auth.data.configuration.entity.OIDCProviderConfigJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for OIDCProviderConfigJpaEntity.
 */
@Repository
public interface OIDCProviderConfigJpaRepository extends JpaRepository<OIDCProviderConfigJpaEntity, UUID> {
    
    Optional<OIDCProviderConfigJpaEntity> findByProviderName(String providerName);
    
    boolean existsByProviderName(String providerName);
    
    List<OIDCProviderConfigJpaEntity> findByEnabled(Boolean enabled);
}


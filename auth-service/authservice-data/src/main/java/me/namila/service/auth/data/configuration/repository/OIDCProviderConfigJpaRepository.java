package me.namila.service.auth.data.configuration.repository;

import me.namila.service.auth.data.configuration.entity.OIDCProviderConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for OIDCProviderConfigEntity.
 */
@Repository
public interface OIDCProviderConfigJpaRepository extends JpaRepository<OIDCProviderConfigEntity, UUID> {
    
    Optional<OIDCProviderConfigEntity> findByProviderName(String providerName);
    
    boolean existsByProviderName(String providerName);
    
    List<OIDCProviderConfigEntity> findByEnabled(Boolean enabled);
}


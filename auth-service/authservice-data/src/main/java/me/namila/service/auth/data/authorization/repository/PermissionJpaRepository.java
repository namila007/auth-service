package me.namila.service.auth.data.authorization.repository;

import me.namila.service.auth.data.authorization.entity.PermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for PermissionJpaEntity.
 */
@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, UUID> {
    
    List<PermissionJpaEntity> findByResource(String resource);
    
    List<PermissionJpaEntity> findByResourceAndAction(String resource, String action);
}


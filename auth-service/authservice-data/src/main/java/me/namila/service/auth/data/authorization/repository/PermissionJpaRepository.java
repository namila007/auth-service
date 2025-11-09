package me.namila.service.auth.data.authorization.repository;

import me.namila.service.auth.data.authorization.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for PermissionEntity.
 */
@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, UUID> {
    
    List<PermissionEntity> findByResource(String resource);
    
    List<PermissionEntity> findByResourceAndAction(String resource, String action);
}


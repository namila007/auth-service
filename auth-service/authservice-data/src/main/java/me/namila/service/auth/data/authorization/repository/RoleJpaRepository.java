package me.namila.service.auth.data.authorization.repository;

import me.namila.service.auth.data.authorization.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for RoleEntity.
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {
    
    Optional<RoleEntity> findByRoleName(String roleName);
    
    boolean existsByRoleName(String roleName);
    
    List<RoleEntity> findByRoleType(String roleType);
}


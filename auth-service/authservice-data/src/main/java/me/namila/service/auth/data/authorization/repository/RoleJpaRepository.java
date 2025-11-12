package me.namila.service.auth.data.authorization.repository;

import me.namila.service.auth.data.authorization.entity.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for RoleJpaEntity.
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, UUID> {
    
    Optional<RoleJpaEntity> findByRoleName(String roleName);
    
    boolean existsByRoleName(String roleName);
    
    List<RoleJpaEntity> findByRoleType(String roleType);
}


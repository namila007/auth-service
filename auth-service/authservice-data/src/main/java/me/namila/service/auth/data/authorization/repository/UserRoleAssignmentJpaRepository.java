package me.namila.service.auth.data.authorization.repository;

import me.namila.service.auth.data.authorization.entity.UserRoleAssignmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for UserRoleAssignmentJpaEntity.
 */
@Repository
public interface UserRoleAssignmentJpaRepository extends JpaRepository<UserRoleAssignmentJpaEntity, UUID> {
    
    List<UserRoleAssignmentJpaEntity> findByUser_UserId(UUID userId);
    
    List<UserRoleAssignmentJpaEntity> findByRole_RoleId(UUID roleId);
    
    List<UserRoleAssignmentJpaEntity> findByUser_UserIdAndRole_RoleId(UUID userId, UUID roleId);
    
    List<UserRoleAssignmentJpaEntity> findByStatus(String status);
    
    List<UserRoleAssignmentJpaEntity> findByScope(String scope);
    
    void deleteByUser_UserId(UUID userId);
    
    void deleteByRole_RoleId(UUID roleId);
}


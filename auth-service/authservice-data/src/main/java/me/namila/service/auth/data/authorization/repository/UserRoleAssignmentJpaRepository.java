package me.namila.service.auth.data.authorization.repository;

import me.namila.service.auth.data.authorization.entity.UserRoleAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for UserRoleAssignmentEntity.
 */
@Repository
public interface UserRoleAssignmentJpaRepository extends JpaRepository<UserRoleAssignmentEntity, UUID> {
    
    List<UserRoleAssignmentEntity> findByUser_UserId(UUID userId);
    
    List<UserRoleAssignmentEntity> findByRole_RoleId(UUID roleId);
    
    List<UserRoleAssignmentEntity> findByUser_UserIdAndRole_RoleId(UUID userId, UUID roleId);
    
    List<UserRoleAssignmentEntity> findByStatus(String status);
    
    List<UserRoleAssignmentEntity> findByScope(String scope);
    
    void deleteByUser_UserId(UUID userId);
    
    void deleteByRole_RoleId(UUID roleId);
}


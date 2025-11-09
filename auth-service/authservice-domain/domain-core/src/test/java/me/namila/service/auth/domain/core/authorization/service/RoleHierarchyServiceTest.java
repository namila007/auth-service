package me.namila.service.auth.domain.core.authorization.service;

import me.namila.service.auth.domain.core.authorization.model.PermissionEntity;
import me.namila.service.auth.domain.core.authorization.model.RoleAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.PermissionId;
import me.namila.service.auth.domain.core.authorization.model.id.RoleId;
import me.namila.service.auth.domain.core.authorization.valueobject.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RoleHierarchyService.
 * Tests role hierarchy resolution and permission inheritance logic.
 */
@DisplayName("RoleHierarchyService Unit Tests")
class RoleHierarchyServiceTest {
    
    private RoleHierarchyService roleHierarchyService;
    
    @BeforeEach
    void setUp() {
        roleHierarchyService = new RoleHierarchyService();
    }
    
    @Test
    @DisplayName("getInheritedPermissions_RoleWithNoParents_ShouldReturnEmptySet")
    void getInheritedPermissions_RoleWithNoParents_ShouldReturnEmptySet() {
        // Given
        RoleAggregate role = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("test-role")
            .displayName("Test Role")
            .roleType(RoleType.CUSTOM)
            .parentRoles(new HashSet<>())
            .permissions(new HashSet<>())
            .build();
        
        // When
        Set<PermissionEntity> result = roleHierarchyService.getInheritedPermissions(role);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("getInheritedPermissions_RoleWithParent_ShouldReturnParentPermissions")
    void getInheritedPermissions_RoleWithParent_ShouldReturnParentPermissions() {
        // Given
        PermissionEntity parentPermission = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("read")
            .build();
        
        RoleAggregate parentRole = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("parent-role")
            .displayName("Parent Role")
            .roleType(RoleType.CUSTOM)
            .permissions(Set.of(parentPermission))
            .parentRoles(new HashSet<>())
            .build();
        
        RoleAggregate childRole = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("child-role")
            .displayName("Child Role")
            .roleType(RoleType.CUSTOM)
            .permissions(new HashSet<>())
            .parentRoles(Set.of(parentRole))
            .build();
        
        // When
        Set<PermissionEntity> result = roleHierarchyService.getInheritedPermissions(childRole);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(parentPermission));
    }
    
    @Test
    @DisplayName("getInheritedPermissions_RoleWithMultipleLevels_ShouldReturnAllInheritedPermissions")
    void getInheritedPermissions_RoleWithMultipleLevels_ShouldReturnAllInheritedPermissions() {
        // Given
        PermissionEntity grandparentPermission = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("read")
            .build();
        
        PermissionEntity parentPermission = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("write")
            .build();
        
        RoleAggregate grandparentRole = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("grandparent-role")
            .displayName("Grandparent Role")
            .roleType(RoleType.CUSTOM)
            .permissions(Set.of(grandparentPermission))
            .parentRoles(new HashSet<>())
            .build();
        
        RoleAggregate parentRole = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("parent-role")
            .displayName("Parent Role")
            .roleType(RoleType.CUSTOM)
            .permissions(Set.of(parentPermission))
            .parentRoles(Set.of(grandparentRole))
            .build();
        
        RoleAggregate childRole = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("child-role")
            .displayName("Child Role")
            .roleType(RoleType.CUSTOM)
            .permissions(new HashSet<>())
            .parentRoles(Set.of(parentRole))
            .build();
        
        // When
        Set<PermissionEntity> result = roleHierarchyService.getInheritedPermissions(childRole);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(grandparentPermission));
        assertTrue(result.contains(parentPermission));
    }
    
    @Test
    @DisplayName("getInheritedPermissions_NullRole_ShouldThrowException")
    void getInheritedPermissions_NullRole_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> roleHierarchyService.getInheritedPermissions(null)
        );
        
        assertEquals("Role cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("getEffectivePermissions_RoleWithDirectAndInherited_ShouldReturnAllPermissions")
    void getEffectivePermissions_RoleWithDirectAndInherited_ShouldReturnAllPermissions() {
        // Given
        PermissionEntity directPermission = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("delete")
            .build();
        
        PermissionEntity inheritedPermission = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("read")
            .build();
        
        RoleAggregate parentRole = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("parent-role")
            .displayName("Parent Role")
            .roleType(RoleType.CUSTOM)
            .permissions(Set.of(inheritedPermission))
            .parentRoles(new HashSet<>())
            .build();
        
        RoleAggregate role = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("test-role")
            .displayName("Test Role")
            .roleType(RoleType.CUSTOM)
            .permissions(Set.of(directPermission))
            .parentRoles(Set.of(parentRole))
            .build();
        
        // When
        Set<PermissionEntity> result = roleHierarchyService.getEffectivePermissions(role);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(directPermission));
        assertTrue(result.contains(inheritedPermission));
    }
    
    @Test
    @DisplayName("getEffectivePermissions_RoleWithNoPermissions_ShouldReturnEmptySet")
    void getEffectivePermissions_RoleWithNoPermissions_ShouldReturnEmptySet() {
        // Given
        RoleAggregate role = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("test-role")
            .displayName("Test Role")
            .roleType(RoleType.CUSTOM)
            .permissions(new HashSet<>())
            .parentRoles(new HashSet<>())
            .build();
        
        // When
        Set<PermissionEntity> result = roleHierarchyService.getEffectivePermissions(role);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("getEffectivePermissions_NullRole_ShouldThrowException")
    void getEffectivePermissions_NullRole_ShouldThrowException() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> roleHierarchyService.getEffectivePermissions(null)
        );
        
        assertEquals("Role cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("validateHierarchy_SameRoleAsParent_ShouldThrowException")
    void validateHierarchy_SameRoleAsParent_ShouldThrowException() {
        // Given
        RoleAggregate role = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("test-role")
            .displayName("Test Role")
            .roleType(RoleType.CUSTOM)
            .build();
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> roleHierarchyService.validateHierarchy(role, role)
        );
        
        assertEquals("Role cannot be its own parent", exception.getMessage());
    }
    
    @Test
    @DisplayName("validateHierarchy_ValidParent_ShouldNotThrowException")
    void validateHierarchy_ValidParent_ShouldNotThrowException() {
        // Given
        RoleAggregate parentRole = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("parent-role")
            .displayName("Parent Role")
            .roleType(RoleType.CUSTOM)
            .parentRoles(new HashSet<>())
            .build();
        
        RoleAggregate childRole = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("child-role")
            .displayName("Child Role")
            .roleType(RoleType.CUSTOM)
            .parentRoles(new HashSet<>())
            .build();
        
        // When & Then
        assertDoesNotThrow(() -> roleHierarchyService.validateHierarchy(childRole, parentRole));
    }
    
    @Test
    @DisplayName("validateHierarchy_NullRole_ShouldThrowException")
    void validateHierarchy_NullRole_ShouldThrowException() {
        // Given
        RoleAggregate parentRole = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("parent-role")
            .displayName("Parent Role")
            .roleType(RoleType.CUSTOM)
            .build();
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> roleHierarchyService.validateHierarchy(null, parentRole)
        );
        
        assertEquals("Role and potential parent cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("validateHierarchy_NullParent_ShouldThrowException")
    void validateHierarchy_NullParent_ShouldThrowException() {
        // Given
        RoleAggregate role = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("test-role")
            .displayName("Test Role")
            .roleType(RoleType.CUSTOM)
            .build();
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> roleHierarchyService.validateHierarchy(role, null)
        );
        
        assertEquals("Role and potential parent cannot be null", exception.getMessage());
    }
    
    @Test
    @DisplayName("validateHierarchy_CircularDependency_ShouldThrowException")
    void validateHierarchy_CircularDependency_ShouldThrowException() {
        // Given
        RoleAggregate roleA = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("role-a")
            .displayName("Role A")
            .roleType(RoleType.CUSTOM)
            .parentRoles(new HashSet<>())
            .build();
        
        RoleAggregate roleB = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("role-b")
            .displayName("Role B")
            .roleType(RoleType.CUSTOM)
            .parentRoles(Set.of(roleA))
            .build();
        
        // When & Then - Trying to make roleA a parent of roleB when roleB is already a descendant of roleA
        // This would create: roleA -> roleB -> roleA (circular)
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> roleHierarchyService.validateHierarchy(roleA, roleB)
        );
        
        assertEquals("Cannot add parent role: would create circular dependency", exception.getMessage());
    }
}


package me.namila.service.auth.domain.core.authorization.service;

import me.namila.service.auth.domain.core.authorization.model.PermissionEntity;
import me.namila.service.auth.domain.core.authorization.model.RoleAggregate;
import me.namila.service.auth.domain.core.authorization.model.id.PermissionId;
import me.namila.service.auth.domain.core.authorization.model.id.RoleId;
import me.namila.service.auth.domain.core.authorization.valueobject.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PermissionAggregationService.
 * Tests permission aggregation logic with a real RoleHierarchyService.
 */
@DisplayName("PermissionAggregationService Unit Tests")
class PermissionAggregationServiceTest {
    
    private RoleHierarchyService roleHierarchyService;
    private PermissionAggregationService permissionAggregationService;
    
    @BeforeEach
    void setUp() {
        roleHierarchyService = new RoleHierarchyService();
        permissionAggregationService = new PermissionAggregationService(roleHierarchyService);
    }
    
    @Test
    @DisplayName("aggregatePermissions_EmptyList_ShouldReturnEmptySet")
    void aggregatePermissions_EmptyList_ShouldReturnEmptySet() {
        // Given
        List<RoleAggregate> roles = new ArrayList<>();
        
        // When
        Set<PermissionEntity> result = permissionAggregationService.aggregatePermissions(roles);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("aggregatePermissions_NullList_ShouldReturnEmptySet")
    void aggregatePermissions_NullList_ShouldReturnEmptySet() {
        // When
        Set<PermissionEntity> result = permissionAggregationService.aggregatePermissions(null);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("aggregatePermissions_SingleRole_ShouldReturnRolePermissions")
    void aggregatePermissions_SingleRole_ShouldReturnRolePermissions() {
        // Given
        PermissionEntity permission1 = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("read")
            .build();
        
        PermissionEntity permission2 = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("write")
            .build();
        
        RoleAggregate role = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("test-role")
            .displayName("Test Role")
            .roleType(RoleType.CUSTOM)
            .permissions(Set.of(permission1, permission2))
            .parentRoles(new HashSet<>())
            .build();
        
        // When
        Set<PermissionEntity> result = permissionAggregationService.aggregatePermissions(List.of(role));
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(permission1));
        assertTrue(result.contains(permission2));
    }
    
    @Test
    @DisplayName("aggregatePermissions_MultipleRoles_ShouldReturnAllPermissions")
    void aggregatePermissions_MultipleRoles_ShouldReturnAllPermissions() {
        // Given
        PermissionEntity permission1 = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("read")
            .build();
        
        PermissionEntity permission2 = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("write")
            .build();
        
        PermissionEntity permission3 = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("user")
            .action("read")
            .build();
        
        RoleAggregate role1 = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("role-1")
            .displayName("Role 1")
            .roleType(RoleType.CUSTOM)
            .permissions(Set.of(permission1, permission2))
            .parentRoles(new HashSet<>())
            .build();
        
        RoleAggregate role2 = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("role-2")
            .displayName("Role 2")
            .roleType(RoleType.CUSTOM)
            .permissions(Set.of(permission3))
            .parentRoles(new HashSet<>())
            .build();
        
        // When
        Set<PermissionEntity> result = permissionAggregationService.aggregatePermissions(List.of(role1, role2));
        
        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(permission1));
        assertTrue(result.contains(permission2));
        assertTrue(result.contains(permission3));
    }
    
    @Test
    @DisplayName("aggregatePermissions_RoleWithNull_ShouldSkipNullRole")
    void aggregatePermissions_RoleWithNull_ShouldSkipNullRole() {
        // Given
        PermissionEntity permission = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("read")
            .build();
        
        RoleAggregate role = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("test-role")
            .displayName("Test Role")
            .roleType(RoleType.CUSTOM)
            .permissions(Set.of(permission))
            .parentRoles(new HashSet<>())
            .build();
        
        List<RoleAggregate> roles = new ArrayList<>();
        roles.add(role);
        roles.add(null);
        
        // When
        Set<PermissionEntity> result = permissionAggregationService.aggregatePermissions(roles);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(permission));
    }
    
    @Test
    @DisplayName("hasPermission_UserHasPermission_ShouldReturnTrue")
    void hasPermission_UserHasPermission_ShouldReturnTrue() {
        // Given
        PermissionEntity requiredPermission = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("read")
            .build();
        
        PermissionEntity otherPermission = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("write")
            .build();
        
        RoleAggregate role = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("test-role")
            .displayName("Test Role")
            .roleType(RoleType.CUSTOM)
            .permissions(Set.of(requiredPermission, otherPermission))
            .parentRoles(new HashSet<>())
            .build();
        
        // When
        boolean result = permissionAggregationService.hasPermission(List.of(role), requiredPermission);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    @DisplayName("hasPermission_UserDoesNotHavePermission_ShouldReturnFalse")
    void hasPermission_UserDoesNotHavePermission_ShouldReturnFalse() {
        // Given
        PermissionEntity requiredPermission = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("delete")
            .build();
        
        PermissionEntity otherPermission = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("read")
            .build();
        
        RoleAggregate role = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("test-role")
            .displayName("Test Role")
            .roleType(RoleType.CUSTOM)
            .permissions(Set.of(otherPermission))
            .parentRoles(new HashSet<>())
            .build();
        
        // When
        boolean result = permissionAggregationService.hasPermission(List.of(role), requiredPermission);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    @DisplayName("hasPermission_NullRoles_ShouldReturnFalse")
    void hasPermission_NullRoles_ShouldReturnFalse() {
        // Given
        PermissionEntity permission = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("read")
            .build();
        
        // When
        boolean result = permissionAggregationService.hasPermission(null, permission);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    @DisplayName("hasPermission_NullPermission_ShouldReturnFalse")
    void hasPermission_NullPermission_ShouldReturnFalse() {
        // Given
        RoleAggregate role = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("test-role")
            .displayName("Test Role")
            .roleType(RoleType.CUSTOM)
            .build();
        
        // When
        boolean result = permissionAggregationService.hasPermission(List.of(role), null);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    @DisplayName("getPermissionsByResource_ValidResource_ShouldReturnFilteredPermissions")
    void getPermissionsByResource_ValidResource_ShouldReturnFilteredPermissions() {
        // Given
        PermissionEntity documentPermission1 = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("read")
            .build();
        
        PermissionEntity documentPermission2 = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("write")
            .build();
        
        PermissionEntity userPermission = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("user")
            .action("read")
            .build();
        
        RoleAggregate role = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("test-role")
            .displayName("Test Role")
            .roleType(RoleType.CUSTOM)
            .permissions(Set.of(documentPermission1, documentPermission2, userPermission))
            .parentRoles(new HashSet<>())
            .build();
        
        // When
        Set<PermissionEntity> result = permissionAggregationService.getPermissionsByResource(
            List.of(role), "document");
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(documentPermission1));
        assertTrue(result.contains(documentPermission2));
        assertFalse(result.contains(userPermission));
    }
    
    @Test
    @DisplayName("getPermissionsByResource_NoMatchingResource_ShouldReturnEmptySet")
    void getPermissionsByResource_NoMatchingResource_ShouldReturnEmptySet() {
        // Given
        PermissionEntity permission = PermissionEntity.builder()
            .id(PermissionId.generate())
            .resource("document")
            .action("read")
            .build();
        
        RoleAggregate role = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("test-role")
            .displayName("Test Role")
            .roleType(RoleType.CUSTOM)
            .permissions(Set.of(permission))
            .parentRoles(new HashSet<>())
            .build();
        
        // When
        Set<PermissionEntity> result = permissionAggregationService.getPermissionsByResource(
            List.of(role), "user");
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("getPermissionsByResource_NullRoles_ShouldReturnEmptySet")
    void getPermissionsByResource_NullRoles_ShouldReturnEmptySet() {
        // When
        Set<PermissionEntity> result = permissionAggregationService.getPermissionsByResource(
            null, "document");
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("getPermissionsByResource_NullResource_ShouldReturnEmptySet")
    void getPermissionsByResource_NullResource_ShouldReturnEmptySet() {
        // Given
        RoleAggregate role = RoleAggregate.builder()
            .id(RoleId.generate())
            .roleName("test-role")
            .displayName("Test Role")
            .roleType(RoleType.CUSTOM)
            .build();
        
        // When
        Set<PermissionEntity> result = permissionAggregationService.getPermissionsByResource(
            List.of(role), null);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}


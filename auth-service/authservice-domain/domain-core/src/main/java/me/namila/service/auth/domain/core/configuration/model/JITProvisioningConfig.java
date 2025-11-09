package me.namila.service.auth.domain.core.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * JIT provisioning configuration value object.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JITProvisioningConfig {
    
    @Builder.Default
    private Boolean enabled = true;
    
    @Builder.Default
    private Boolean createUsers = true;
    
    @Builder.Default
    private Boolean updateUsers = true;
    
    @Builder.Default
    private Boolean syncGroups = true;
    
    @Builder.Default
    private Set<String> defaultRoles = new HashSet<>();
}


package me.namila.service.auth.domain.application.configuration.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Request DTO for JIT provisioning configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JITProvisioningConfigRequest {
    
    @Builder.Default
    private Boolean enabled = true;
    
    @Builder.Default
    private Boolean createUsers = true;
    
    @Builder.Default
    private Boolean updateUsers = true;
    
    @Builder.Default
    private Boolean syncGroups = true;
    
    private Set<String> defaultRoles;
}

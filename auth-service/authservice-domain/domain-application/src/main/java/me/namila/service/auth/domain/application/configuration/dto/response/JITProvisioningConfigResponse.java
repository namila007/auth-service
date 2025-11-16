package me.namila.service.auth.domain.application.configuration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Response DTO for JIT provisioning configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JITProvisioningConfigResponse {
    
    private Boolean enabled;
    private Boolean createUsers;
    private Boolean updateUsers;
    private Boolean syncGroups;
    private Set<String> defaultRoles;
}

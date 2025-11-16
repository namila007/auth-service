package me.namila.service.auth.domain.application.configuration.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for role mapping rule.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleMappingRuleRequest {
    
    private String externalGroup; // Regex pattern
    private String internalRole;
    private Map<String, Object> conditions;
}

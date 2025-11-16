package me.namila.service.auth.domain.application.configuration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO for role mapping rule.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleMappingRuleResponse {
    
    private String externalGroup;
    private String internalRole;
    private Map<String, Object> conditions;
}

package me.namila.service.auth.domain.application.configuration.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for role mapping configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleMappingConfigRequest {
    
    private String mappingStrategy; // EXPLICIT, PATTERN, SCRIPTED
    
    @Valid
    private List<RoleMappingRuleRequest> mappingRules;
}

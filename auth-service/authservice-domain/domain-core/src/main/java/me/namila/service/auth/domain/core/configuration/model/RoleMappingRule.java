package me.namila.service.auth.domain.core.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Role mapping rule value object.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleMappingRule {
    
    private String externalGroup; // Regex pattern
    private String internalRole;
    
    @Builder.Default
    private Map<String, Object> conditions = new HashMap<>();
}


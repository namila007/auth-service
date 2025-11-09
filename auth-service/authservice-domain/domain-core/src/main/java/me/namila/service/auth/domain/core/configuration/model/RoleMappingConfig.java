package me.namila.service.auth.domain.core.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.namila.service.auth.domain.core.configuration.valueobject.MappingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Role mapping configuration value object.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleMappingConfig {
    
    @Builder.Default
    private MappingStrategy mappingStrategy = MappingStrategy.EXPLICIT;
    
    @Builder.Default
    private List<RoleMappingRule> mappingRules = new ArrayList<>();
}


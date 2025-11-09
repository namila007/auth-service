package me.namila.service.auth.domain.core.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Attribute mapping configuration value object.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeMappingConfig {
    
    private String subjectAttribute; // e.g., "sub"
    private String emailAttribute; // e.g., "email"
    private String nameAttribute; // e.g., "name"
    private String groupsAttribute; // e.g., "groups"
    
    @Builder.Default
    private List<AttributeMap> customMappings = new ArrayList<>();
}


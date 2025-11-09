package me.namila.service.auth.domain.core.configuration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Attribute map value object for custom attribute mappings.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeMap {
    
    private String externalAttribute;
    private String internalAttribute;
    private String transformationRule; // Expression for transformation
}


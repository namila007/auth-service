package me.namila.service.auth.domain.core.identity.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.namila.service.auth.common.domain.BaseEntity;
import me.namila.service.auth.domain.core.identity.model.id.UserProfileId;

import java.util.HashMap;
import java.util.Map;

/**
 * User profile entity containing additional user information.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class UserProfileEntity extends BaseEntity<UserProfileId> {
    
    @EqualsAndHashCode.Include
    @Builder.Default
    private UserProfileId id = UserProfileId.generate();
    
    private String firstName;
    private String lastName;
    private String displayName;
    
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();
    
    public Map<String, Object> getAttributes() {
        return attributes != null ? attributes : new HashMap<>();
    }
    
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes != null ? attributes : new HashMap<>();
    }
    
    public void addAttribute(String key, Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
        markAsUpdated();
    }
}


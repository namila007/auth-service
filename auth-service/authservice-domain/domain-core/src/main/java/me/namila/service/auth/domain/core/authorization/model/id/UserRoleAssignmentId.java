package me.namila.service.auth.domain.core.authorization.model.id;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.EqualsAndHashCode;
import me.namila.service.auth.common.domain.BaseId;

import java.util.UUID;

/**
 * UserRoleAssignment identifier implementing BaseId with UUIDv7 generation.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserRoleAssignmentId implements BaseId<UUID> {
    
    @EqualsAndHashCode.Include
    private UUID value;
    
    private UserRoleAssignmentId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserRoleAssignment ID cannot be null");
        }
        this.value = value;
    }
    
    public static UserRoleAssignmentId of(String id) {
        return new UserRoleAssignmentId(UUID.fromString(id));
    }
    
    public static UserRoleAssignmentId of(UUID id) {
        return new UserRoleAssignmentId(id);
    }
    
    public static UserRoleAssignmentId generate() {
        return new UserRoleAssignmentId(UuidCreator.getTimeOrderedEpoch());
    }
    
    @Override
    public UUID getValue() {
        return value;
    }
    
    @Override
    public void setValue(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserRoleAssignment ID cannot be null");
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}


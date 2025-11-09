package me.namila.service.auth.domain.core.authorization.model.id;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.EqualsAndHashCode;
import me.namila.service.auth.common.domain.BaseId;

import java.util.UUID;

/**
 * Permission identifier implementing BaseId with UUIDv7 generation.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PermissionId implements BaseId<UUID> {
    
    @EqualsAndHashCode.Include
    private UUID value;
    
    private PermissionId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Permission ID cannot be null");
        }
        this.value = value;
    }
    
    public static PermissionId of(String id) {
        return new PermissionId(UUID.fromString(id));
    }
    
    public static PermissionId of(UUID id) {
        return new PermissionId(id);
    }
    
    public static PermissionId generate() {
        return new PermissionId(UuidCreator.getTimeOrderedEpoch());
    }
    
    @Override
    public UUID getValue() {
        return value;
    }
    
    @Override
    public void setValue(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Permission ID cannot be null");
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}


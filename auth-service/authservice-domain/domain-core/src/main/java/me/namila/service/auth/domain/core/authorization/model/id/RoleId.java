package me.namila.service.auth.domain.core.authorization.model.id;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.EqualsAndHashCode;
import me.namila.service.auth.common.domain.BaseId;

import java.util.UUID;

/**
 * Role identifier implementing BaseId with UUIDv7 generation.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RoleId implements BaseId<UUID> {
    
    @EqualsAndHashCode.Include
    private UUID value;
    
    private RoleId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Role ID cannot be null");
        }
        this.value = value;
    }
    
    public static RoleId of(String id) {
        return new RoleId(UUID.fromString(id));
    }
    
    public static RoleId of(UUID id) {
        return new RoleId(id);
    }
    
    public static RoleId generate() {
        return new RoleId(UuidCreator.getTimeOrderedEpoch());
    }
    
    @Override
    public UUID getValue() {
        return value;
    }
    
    @Override
    public void setValue(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Role ID cannot be null");
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}


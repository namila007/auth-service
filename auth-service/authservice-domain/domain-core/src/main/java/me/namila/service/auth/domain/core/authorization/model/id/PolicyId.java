package me.namila.service.auth.domain.core.authorization.model.id;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.EqualsAndHashCode;
import me.namila.service.auth.common.domain.BaseId;

import java.util.UUID;

/**
 * Policy identifier implementing BaseId with UUIDv7 generation.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PolicyId implements BaseId<UUID> {
    
    @EqualsAndHashCode.Include
    private UUID value;
    
    private PolicyId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Policy ID cannot be null");
        }
        this.value = value;
    }
    
    public static PolicyId of(String id) {
        return new PolicyId(UUID.fromString(id));
    }
    
    public static PolicyId of(UUID id) {
        return new PolicyId(id);
    }
    
    public static PolicyId generate() {
        return new PolicyId(UuidCreator.getTimeOrderedEpoch());
    }
    
    @Override
    public UUID getValue() {
        return value;
    }
    
    @Override
    public void setValue(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Policy ID cannot be null");
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}


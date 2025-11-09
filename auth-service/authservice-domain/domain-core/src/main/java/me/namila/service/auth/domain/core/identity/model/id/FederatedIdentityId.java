package me.namila.service.auth.domain.core.identity.model.id;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.EqualsAndHashCode;
import me.namila.service.auth.common.domain.BaseId;

import java.util.UUID;

/**
 * FederatedIdentity identifier implementing BaseId with UUIDv7 generation.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FederatedIdentityId implements BaseId<UUID> {
    
    @EqualsAndHashCode.Include
    private UUID value;
    
    private FederatedIdentityId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("FederatedIdentity ID cannot be null");
        }
        this.value = value;
    }
    
    public static FederatedIdentityId of(String id) {
        return new FederatedIdentityId(UUID.fromString(id));
    }
    
    public static FederatedIdentityId of(UUID id) {
        return new FederatedIdentityId(id);
    }
    
    public static FederatedIdentityId generate() {
        return new FederatedIdentityId(UuidCreator.getTimeOrderedEpoch());
    }
    
    @Override
    public UUID getValue() {
        return value;
    }
    
    @Override
    public void setValue(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("FederatedIdentity ID cannot be null");
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}


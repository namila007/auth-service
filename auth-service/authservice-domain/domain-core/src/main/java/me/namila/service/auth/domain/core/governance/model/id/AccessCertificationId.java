package me.namila.service.auth.domain.core.governance.model.id;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.EqualsAndHashCode;
import me.namila.service.auth.common.domain.BaseId;

import java.util.UUID;

/**
 * AccessCertification identifier implementing BaseId with UUIDv7 generation.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccessCertificationId implements BaseId<UUID> {
    
    @EqualsAndHashCode.Include
    private UUID value;
    
    private AccessCertificationId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("AccessCertification ID cannot be null");
        }
        this.value = value;
    }
    
    public static AccessCertificationId of(String id) {
        return new AccessCertificationId(UUID.fromString(id));
    }
    
    public static AccessCertificationId of(UUID id) {
        return new AccessCertificationId(id);
    }
    
    public static AccessCertificationId generate() {
        return new AccessCertificationId(UuidCreator.getTimeOrderedEpoch());
    }
    
    @Override
    public UUID getValue() {
        return value;
    }
    
    @Override
    public void setValue(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("AccessCertification ID cannot be null");
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}


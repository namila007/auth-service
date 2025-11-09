package me.namila.service.auth.domain.core.governance.model.id;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.EqualsAndHashCode;
import me.namila.service.auth.common.domain.BaseId;

import java.util.UUID;

/**
 * AuditLog identifier implementing BaseId with UUIDv7 generation.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AuditLogId implements BaseId<UUID> {
    
    @EqualsAndHashCode.Include
    private UUID value;
    
    private AuditLogId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("AuditLog ID cannot be null");
        }
        this.value = value;
    }
    
    public static AuditLogId of(String id) {
        return new AuditLogId(UUID.fromString(id));
    }
    
    public static AuditLogId of(UUID id) {
        return new AuditLogId(id);
    }
    
    public static AuditLogId generate() {
        return new AuditLogId(UuidCreator.getTimeOrderedEpoch());
    }
    
    @Override
    public UUID getValue() {
        return value;
    }
    
    @Override
    public void setValue(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("AuditLog ID cannot be null");
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}


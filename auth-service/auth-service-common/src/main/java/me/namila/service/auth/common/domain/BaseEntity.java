package me.namila.service.auth.common.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.experimental.SuperBuilder;

/**
 * Base class for all domain entities.
 * Provides identity, audit fields, and equality based on ID.
 * 
 * @param <ID> The type of the entity's identifier, must extend BaseId
 */
@SuperBuilder
public abstract class BaseEntity<ID extends BaseId<?>> {
    
    private ID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor.
     */
    protected BaseEntity() {
    }
    
    /**
     * Constructor with ID.
     * Automatically sets createdAt and updatedAt to current time.
     * 
     * @param id the entity's identifier
     */
    protected BaseEntity(ID id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Get the entity's identifier.
     * 
     * @return the entity's ID
     */
    public ID getId() {
        return id;
    }
    
    /**
     * Set the entity's identifier.
     * 
     * @param id the entity's ID
     */
    protected void setId(ID id) {
        this.id = id;
    }
    
    /**
     * Get the creation timestamp.
     * 
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Set the creation timestamp.
     * 
     * @param createdAt the creation timestamp
     */
    protected void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Get the last update timestamp.
     * 
     * @return the last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Set the last update timestamp.
     * 
     * @param updatedAt the last update timestamp
     */
    protected void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Update the updatedAt timestamp to current time.
     */
    protected void markAsUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Equality is based on ID.
     * Two entities are equal if they have the same ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return Objects.equals(id, that.id);
    }
    
    /**
     * Hash code is based on ID.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}


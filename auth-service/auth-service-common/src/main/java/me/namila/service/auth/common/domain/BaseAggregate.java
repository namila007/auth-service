package me.namila.service.auth.common.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.experimental.SuperBuilder;

/**
 * Base class for all aggregate roots.
 * Extends BaseEntity and adds domain event management.
 * 
 * @param <ID> The type of the aggregate's identifier, must extend BaseId
 */
@SuperBuilder
public abstract class BaseAggregate<ID extends BaseId<?>> extends BaseEntity<ID> {
    
    private final List<Object> domainEvents = new ArrayList<>();
    
    /**
     * Default constructor.
     */
    protected BaseAggregate() {
        super();
    }
    
    /**
     * Constructor with ID.
     * 
     * @param id the aggregate's identifier
     */
    protected BaseAggregate(ID id) {
        super(id);
    }
    
    /**
     * Register a domain event to be published.
     * 
     * @param event the domain event to register
     */
    protected void registerDomainEvent(Object event) {
        if (event != null) {
            domainEvents.add(event);
        }
    }
    
    /**
     * Get all registered domain events.
     * Returns an unmodifiable list to prevent external modification.
     * 
     * @return unmodifiable list of domain events
     */
    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    /**
     * Clear all registered domain events.
     * Typically called after events have been published.
     */
    public void clearDomainEvents() {
        domainEvents.clear();
    }
    
    /**
     * Check if there are any registered domain events.
     * 
     * @return true if there are domain events, false otherwise
     */
    public boolean hasDomainEvents() {
        return !domainEvents.isEmpty();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + getId() +
                ", domainEvents=" + domainEvents.size() +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}


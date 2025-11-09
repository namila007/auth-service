package me.namila.service.auth.common.domain;

/**
 * Base interface for all domain identifiers.
 * Provides type-safe ID handling with generic type parameter.
 * 
 * @param <T> The type of the ID value (e.g., UUID, Long)
 */
public interface BaseId<T> {
    
    /**
     * Get the ID value.
     * 
     * @return the ID value
     */
    T getValue();
    
    /**
     * Set the ID value.
     * 
     * @param value the ID value to set
     */
    void setValue(T value);
    
    // Note: Static factory methods (of(String), of(T), generate()) 
    // must be implemented by concrete ID classes.
    // They cannot be defined in the interface as they need to return
    // the concrete type, not the interface type.
}


package me.namila.service.auth.domain.core.identity.valueobject;

import lombok.EqualsAndHashCode;

/**
 * Username value object with validation rules.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class UsernameValue {
    
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;
    private static final String VALID_PATTERN = "^[a-zA-Z0-9._-]+$";
    
    @EqualsAndHashCode.Include
    private final String value;
    
    private UsernameValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        String trimmed = value.trim();
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Username must be between %d and %d characters", MIN_LENGTH, MAX_LENGTH)
            );
        }
        if (!trimmed.matches(VALID_PATTERN)) {
            throw new IllegalArgumentException(
                "Username can only contain letters, numbers, dots, underscores, and hyphens"
            );
        }
        this.value = trimmed;
    }
    
    public static UsernameValue of(String value) {
        return new UsernameValue(value);
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}


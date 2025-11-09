package me.namila.service.auth.domain.core.identity.valueobject;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;

/**
 * Email value object with validation.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class EmailValue {
    
    @EqualsAndHashCode.Include
    @NotBlank(message = "Email cannot be blank")
    @jakarta.validation.constraints.Email(message = "Email must be a valid email address")
    private final String value;
    
    private EmailValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (!isValidEmail(value)) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        this.value = value.toLowerCase().trim();
    }
    
    public static EmailValue of(String value) {
        return new EmailValue(value);
    }

    public String getValue() {
        return value;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    @Override
    public String toString() {
        return value;
    }
}


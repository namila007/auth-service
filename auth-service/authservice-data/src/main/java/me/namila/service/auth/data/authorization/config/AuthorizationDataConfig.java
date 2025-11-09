package me.namila.service.auth.data.authorization.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration for Authorization context data module.
 */
@Configuration
@EnableJpaRepositories(basePackages = "me.namila.service.auth.data.authorization.repository")
public class AuthorizationDataConfig {
    // Context-specific configuration can be added here
}


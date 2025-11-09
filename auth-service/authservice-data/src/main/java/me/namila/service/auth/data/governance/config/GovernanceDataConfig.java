package me.namila.service.auth.data.governance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration for Governance context data module.
 */
@Configuration
@EnableJpaRepositories(basePackages = "me.namila.service.auth.data.governance.repository")
public class GovernanceDataConfig {
    // Context-specific configuration can be added here
}


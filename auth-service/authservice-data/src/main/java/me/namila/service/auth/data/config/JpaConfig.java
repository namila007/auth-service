package me.namila.service.auth.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

/**
 * JPA configuration for entity auditing and repository scanning.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableJpaRepositories(basePackages = "me.namila.service.auth.data")
@EnableTransactionManagement
public class JpaConfig {
    
    /**
     * Provides auditor information for JPA auditing.
     * In a real application, this would extract the current user from security context.
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("SYSTEM"); // Default auditor
    }
}


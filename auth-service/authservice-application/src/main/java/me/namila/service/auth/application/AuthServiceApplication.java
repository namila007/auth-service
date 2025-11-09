package me.namila.service.auth.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "me.namila.service.auth.application",
    "me.namila.service.auth.domain.application",
    "me.namila.service.auth.data"
})
@EntityScan(basePackages = "me.namila.service.auth.data")
@EnableJpaRepositories(basePackages = "me.namila.service.auth.data")
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}


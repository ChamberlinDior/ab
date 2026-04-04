package com.agora.assemblee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@SpringBootApplication
public class AgoraBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgoraBackendApplication.class, args);
    }
}

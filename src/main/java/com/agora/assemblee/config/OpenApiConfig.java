package com.agora.assemblee.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("AGORA Backend API")
                .version("2.0.0")
                .description("API parlementaire intégrée de l'Assemblée Nationale du Gabon"));
    }
}

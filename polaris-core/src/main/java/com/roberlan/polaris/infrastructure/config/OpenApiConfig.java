package com.roberlan.polaris.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Value("${spring.application.name}") private String appName;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Polaris API").version("1.0.0").description(
                "API de gerenciamento de tarefas de alta performance usando Redis e Clean Architecture.").contact(
                new Contact().name("Roberlan Carvalho").url("https://roberlancarvalho.com").email(
                        "roberlan@technorth.com.br"))).servers(
                List.of(new Server().url("http://localhost:8080").description("Ambiente Local (Docker)")));
    }
}
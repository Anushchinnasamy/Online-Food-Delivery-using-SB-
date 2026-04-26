package com.fooddelivery.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI foodDeliveryOpenAPI() {
        // Define JWT security scheme
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT token for authentication. Format: Bearer {token}");

        // Define security requirement
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        // Define API info
        Info info = new Info()
                .title("Food Delivery Platform API")
                .description("Complete REST API documentation for the Food Delivery Platform. " +
                        "This API provides endpoints for user authentication, restaurant management, " +
                        "menu management, order placement and tracking, payment processing, and delivery management.")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Food Delivery Platform")
                        .email("support@fooddelivery.com")
                        .url("https://fooddelivery.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));

        // Define servers
        Server localServer = new Server()
                .url("http://localhost:8081/api")
                .description("Local Development Server");

        Server productionServer = new Server()
                .url("https://api.fooddelivery.com")
                .description("Production Server");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, productionServer))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme));
    }
}

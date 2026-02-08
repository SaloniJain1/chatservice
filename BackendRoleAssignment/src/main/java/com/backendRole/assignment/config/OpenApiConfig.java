package com.backendRole.assignment.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OpenAPI and Swagger UI.
 * Defines the documentation metadata and the security scheme (Bearer Token) for
 * testing API endpoints.
 */
@Configuration
public class OpenApiConfig {

        /**
         * Configures the OpenAPI bean with custom info and security requirements.
         *
         * @return a configured OpenAPI instance
         */
        @Bean
        public OpenAPI customOpenAPI() {
                final String securitySchemeName = "BearerAuth";
                return new OpenAPI()
                                .info(new Info()
                                                .title("Backend Role Assignment API")
                                                .version("1.0.0")
                                                .description("Advanced API for managing RAG-based chatbot history with robust JWT RS256 authentication and detailed logging.")
                                                .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .components(new Components()
                                                .addSecuritySchemes(securitySchemeName,
                                                                new SecurityScheme()
                                                                                .name(securitySchemeName)
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")));
        }
}

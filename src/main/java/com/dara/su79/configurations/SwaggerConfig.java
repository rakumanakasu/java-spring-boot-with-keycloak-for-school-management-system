package com.dara.su79.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String securitySchemeName = "oauth2Scheme";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SU7.9 Spring Boot API")
                        .version("1.0")
                        .description("API documentation for My Spring Boot application"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(new OAuthFlows()
                                                .authorizationCode(
                                                        new OAuthFlow()
                                                                .authorizationUrl(
                                                                        "http://localhost:8067/realms/su79-school-management-realm/protocol/openid-connect/auth")
                                                                .tokenUrl(
                                                                        "http://localhost:8067/realms/su79-school-management-realm/protocol/openid-connect/token")
                                                                .scopes(new Scopes()
                                                                        .addString("openid", "OpenID Connect scope")
                                                                        .addString("profile", "Access profile info")
                                                                        .addString("email", "Access email"))))));

    }
}


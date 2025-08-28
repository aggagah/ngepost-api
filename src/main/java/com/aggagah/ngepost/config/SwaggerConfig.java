package com.aggagah.ngepost.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI ngepostOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ngepost API")
                        .description("NgePost - Blog Post API | Tech Test NoLimit Indonesia")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Gagah Aji Gunadi")
                                .email("aggagah.dev@gmail.com")
                                .url("https://github.com/aggagah")))
                .externalDocs(new ExternalDocumentation()
                        .description("Ngepost Documentation")
                        .url("https://github.com/aggagah/ngepost"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .schemaRequirement(SECURITY_SCHEME_NAME, new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}

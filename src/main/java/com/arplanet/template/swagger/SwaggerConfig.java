package com.arplanet.template.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .info(new Info()
                        .title(applicationName + "API Documentation")
                        .version("1.0.0")
                        .description("API Document for " + applicationName)
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http:/springdoc.org")
                        )
                )
                .servers(List.of(new Server().url(contextPath)));
    }

//    @Bean
//    public OpenAPI openAPI() {
//        final String securitySchemeName = "bearerAuth";
//
//        // 確保 contextPath 不為空
//        String baseUrl = StringUtils.hasText(contextPath) ? contextPath : "/";
//
//        return new OpenAPI()
//                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
//                .components(new Components()
//                        .addSecuritySchemes(securitySchemeName,
//                                new SecurityScheme()
//                                        .name(securitySchemeName)
//                                        .type(SecurityScheme.Type.HTTP)
//                                        .scheme("bearer")
//                                        .bearerFormat("JWT")
//                        )
//                )
//                .info(new Info()
//                        .title(applicationName + " API Documentation")
//                        .version("1.0.0")
//                        .description("API Document for " + applicationName)
//                )
//                .servers(List.of(new Server().url(baseUrl)
//                        .description("Server URL in Development environment")));
//    }
}

package com.nexware.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Collections;
import java.util.List;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";
    private static final List<String> PUBLIC_PATHS = List.of(
            "/actuator/health",
            "/actuator/info",
            "/api/v1/health"
    );

    @Bean
    OpenAPI nexwareOpenApi(
            @Value("${nexware.openapi.server-url:http://localhost:8080}") String serverUrl
    ) {
        return new OpenAPI()
                .servers(List.of(server(serverUrl)))
                .info(new Info()
                        .title("NexWare Distribution Platform API")
                        .version("v1")
                        .description("""
                                Enterprise electronics distribution and warehouse management backend.

                                Quick check: open **health-check → GET /api/v1/health** first — expect `{"status":"UP"}`.
                                """)
                        .contact(new Contact().name("NexWare Engineering")))
                .components(new Components().addSecuritySchemes(BEARER_AUTH,
                        new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }

    /** Tell SpringDoc not to replace our configured server list with a generated http URL. */
    @Bean
    OpenApiBuilderCustomizer markServersConfigured() {
        return openApiService -> openApiService.setServersPresent(true);
    }

    /** Runs before SpringDoc writes "Generated server url" on every /api-docs request. */
    @Bean
    ServerBaseUrlCustomizer serverBaseUrlCustomizer(
            @Value("${nexware.openapi.server-url:http://localhost:8080}") String serverUrl
    ) {
        return (serverBaseUrl, request) -> normalizeServerUrl(serverUrl, serverBaseUrl);
    }

    /** Final pass after SpringDoc builds the document — always force the configured server URL. */
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    OpenApiCustomizer enforceConfiguredServer(
            @Value("${nexware.openapi.server-url:http://localhost:8080}") String serverUrl
    ) {
        return openApi -> openApi.setServers(List.of(server(serverUrl)));
    }

    @Bean
    OpenApiCustomizer publicHealthEndpoints() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            for (String path : PUBLIC_PATHS) {
                var pathItem = openApi.getPaths().get(path);
                if (pathItem != null && pathItem.getGet() != null) {
                    pathItem.getGet().setSecurity(Collections.emptyList());
                }
            }
        };
    }

    private static Server server(String serverUrl) {
        return new Server().url(normalizeServerUrl(serverUrl, null)).description("API server");
    }

    private static String normalizeServerUrl(String configuredUrl, String fallbackUrl) {
        String url = (configuredUrl != null && !configuredUrl.isBlank()) ? configuredUrl : fallbackUrl;
        if (url == null || url.isBlank()) {
            return "http://localhost:8080";
        }
        url = url.trim();
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}

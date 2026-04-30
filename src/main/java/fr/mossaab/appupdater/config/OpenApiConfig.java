package fr.mossaab.appupdater.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Value("${app.server.base-url:http://localhost:8080}")
    private String serverUrl;

    @Value("${app.contact.mail-address:app@mail.app}")
    private String contactMailAddress;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Сервис обновлений приложения")
                        .description("API управление обновлениями версий приложения")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Support")
                                .email(contactMailAddress)))
                .components(new Components().addSecuritySchemes("ApiKeyAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-API-Key")
                ))
                .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"))
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description("Production server")
                ));
    }
}
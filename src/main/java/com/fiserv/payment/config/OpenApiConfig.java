package com.fiserv.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Payment Authorization API")
                .version("1.0.0")
                .description("Microservice for payment transaction authorization with fraud detection")
                .contact(new Contact()
                    .name("Fiserv Payment Team")))
            .addServersItem(new Server()
                .url("http://localhost:8080")
                .description("Local Development"));
    }
}


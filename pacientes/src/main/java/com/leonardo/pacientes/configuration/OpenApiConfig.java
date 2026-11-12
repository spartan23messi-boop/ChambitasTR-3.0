package com.leonardo.pacientes.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Pacientes")
                        .version("1.0.0")
                        .description("API para la gestión de pacientes"))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Servidor local de Pacientes"),
                        new Server().url("http://localhost:8080/api").description("Servidor API Gateway")
                ));
    }
}

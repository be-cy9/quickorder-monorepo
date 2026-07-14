package com.duoc.quickorder.mspedidos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("QuickOrder - Microservicio de Pedidos")
                        .version("1.0.0")
                        .description("""
                                API REST para la gestión de pedidos del sistema QuickOrder.
                                
                                Permite crear, consultar, actualizar y eliminar pedidos,
                                además de obtener información combinada con datos del cliente
                                a través de comunicación con el microservicio ms-clientes.
                                
                                **Autenticación:** Se requiere token JWT en el encabezado Authorization.
                                Primero obtenga su token desde el endpoint `/auth/login`.
                                """)
                        .contact(new Contact()
                                .name("Equipo QuickOrder - DUOC UC")
                                .email("soporte@quickorder.cl")
                                .url("https://github.com/quickorder"))
                        .license(new License()
                                .name("Uso Académico - DUOC UC")
                                .url("https://www.duoc.cl")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Servidor local de desarrollo"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("API Gateway (acceso unificado)")))
                .addSecurityItem(new SecurityRequirement().addList("Token JWT"))
                .components(new Components()
                        .addSecuritySchemes("Token JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingrese el token JWT obtenido desde /auth/login. " +
                                        "Ejemplo: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")));
    }
}

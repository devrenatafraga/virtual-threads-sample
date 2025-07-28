package edu.renata.fraga.virtual_threads_sample.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Virtual Threads Sample API")
                        .description("Demonstração prática do uso de Virtual Threads (Project Loom) com Spring Boot WebFlux")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Renata Fraga")
                                .email("devrenatafraga@example.com")
                                .url("https://github.com/devrenatafraga"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desenvolvimento"),
                        new Server()
                                .url("https://virtual-threads-sample.herokuapp.com")
                                .description("Servidor de produção")
                ));
    }
}

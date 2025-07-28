package edu.renata.fraga.virtual_threads_sample.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "API Info", description = "Informações sobre a API e links úteis")
public class ApiInfoController {

    @Operation(
            summary = "Informações da API",
            description = "Retorna informações básicas sobre a API e links para documentação"
    )
    @ApiResponse(responseCode = "200", description = "Informações da API retornadas com sucesso")
    @GetMapping("/info")
    public Map<String, Object> getApiInfo() {
        return Map.of(
                "name", "Virtual Threads Sample API",
                "version", "1.0.0",
                "description", "Demonstração prática do uso de Virtual Threads (Project Loom) com Spring Boot",
                "javaVersion", System.getProperty("java.version"),
                "virtualThreadsSupported", Thread.class.getDeclaredMethods().length > 20, // Proxy para verificar se Virtual Threads estão disponíveis
                "documentation", Map.of(
                        "swagger", "/swagger-ui.html",
                        "openapi", "/v3/api-docs",
                        "github", "https://github.com/devrenatafraga/virtual-threads-sample"
                ),
                "endpoints", Map.of(
                        "traditionalThreads", "/api/virtual-threads/**",
                        "webfluxThreads", "/api/webflux-virtual-threads/**",
                        "info", "/api/info"
                )
        );
    }

    @Operation(
            summary = "Informações do Sistema",
            description = "Retorna informações sobre o ambiente de execução atual"
    )
    @ApiResponse(responseCode = "200", description = "Informações do sistema retornadas com sucesso")
    @GetMapping("/system-info")
    public Map<String, Object> getSystemInfo() {
        Runtime runtime = Runtime.getRuntime();
        
        return Map.of(
                "jvm", Map.of(
                        "version", System.getProperty("java.version"),
                        "vendor", System.getProperty("java.vendor"),
                        "name", System.getProperty("java.vm.name")
                ),
                "memory", Map.of(
                        "total", runtime.totalMemory(),
                        "free", runtime.freeMemory(),
                        "max", runtime.maxMemory(),
                        "used", runtime.totalMemory() - runtime.freeMemory()
                ),
                "processors", runtime.availableProcessors(),
                "currentThread", Map.of(
                        "name", Thread.currentThread().getName(),
                        "threadId", Thread.currentThread().threadId(),
                        "virtual", Thread.currentThread().isVirtual(),
                        "daemon", Thread.currentThread().isDaemon(),
                        "priority", Thread.currentThread().getPriority()
                )
        );
    }
}

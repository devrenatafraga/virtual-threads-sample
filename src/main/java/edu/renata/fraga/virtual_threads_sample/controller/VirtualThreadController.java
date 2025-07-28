package edu.renata.fraga.virtual_threads_sample.controller;

import edu.renata.fraga.virtual_threads_sample.service.VirtualThreadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/virtual-threads")
@Tag(name = "Virtual Threads API", description = "APIs demonstrando o uso de Virtual Threads tradicionais (Spring MVC)")
public class VirtualThreadController {

    @Autowired
    private VirtualThreadService virtualThreadService;

    @Operation(
            summary = "Comparar Virtual Threads vs Platform Threads",
            description = "Executa tarefas concorrentes comparando performance entre Virtual Threads e Platform Threads tradicionais"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comparação realizada com sucesso",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                                "virtualThreads": "Processed 100 tasks in 1.2s using Virtual Threads",
                                "platformThreads": "Processed 100 tasks in 2.1s using Platform Threads",
                                "currentThread": "Thread[#123,pool-1-thread-1,5,main] - Virtual: false"
                            }
                            """)))
    })
    @GetMapping("/compare")
    public Map<String, String> compareThreads(
            @Parameter(description = "Número de tarefas a serem executadas", example = "100")
            @RequestParam(defaultValue = "100") int tasks) {
        String virtualResult = virtualThreadService.processWithVirtualThreads(tasks);
        String platformResult = virtualThreadService.processWithPlatformThreads(tasks);
        
        return Map.of(
            "virtualThreads", virtualResult,
            "platformThreads", platformResult,
            "currentThread", getCurrentThreadInfo()
        );
    }

    @Operation(
            summary = "Operação Bloqueante",
            description = "Demonstra uma operação de I/O bloqueante executada em Virtual Thread"
    )
    @ApiResponse(responseCode = "200", description = "Operação bloqueante executada com sucesso")
    @GetMapping("/blocking")
    public String blockingOperation() {
        return virtualThreadService.simulateBlockingOperation();
    }

    @Operation(
            summary = "Múltiplas Operações Bloqueantes",
            description = "Executa múltiplas operações bloqueantes concorrentemente usando Virtual Threads"
    )
    @ApiResponse(responseCode = "200", description = "Operações bloqueantes executadas com sucesso")
    @GetMapping("/multiple-blocking")
    public String multipleBlockingOperations(
            @Parameter(description = "Número de operações bloqueantes a executar", example = "10")
            @RequestParam(defaultValue = "10") int operations) {
        return virtualThreadService.processMultipleBlockingOperations(operations);
    }

    @Operation(
            summary = "Criação Manual de Virtual Thread",
            description = "Demonstra como criar Virtual Threads manualmente"
    )
    @ApiResponse(responseCode = "200", description = "Virtual Thread criada manualmente com sucesso")
    @GetMapping("/manual-creation")
    public String manualVirtualThreadCreation() {
        return virtualThreadService.createVirtualThreadManually();
    }

    @Operation(
            summary = "Criação com Factory",
            description = "Demonstra o uso de ThreadFactory para criar Virtual Threads"
    )
    @ApiResponse(responseCode = "200", description = "Virtual Thread criada via factory com sucesso")
    @GetMapping("/factory-creation")
    public String factoryVirtualThreadCreation() {
        return virtualThreadService.demonstrateVirtualThreadFactory();
    }

    @Operation(
            summary = "Endpoint Assíncrono",
            description = "Demonstra endpoint assíncrono com CompletableFuture"
    )
    @ApiResponse(responseCode = "200", description = "Processamento assíncrono concluído")
    @GetMapping("/async-endpoint")
    public CompletableFuture<String> asyncEndpoint() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000); // Simula processamento
                return String.format("Async operation completed on thread: %s", getCurrentThreadInfo());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }

    @GetMapping("/thread-info")
    public Map<String, Object> getThreadInfo() {
        Thread currentThread = Thread.currentThread();
        
        return Map.of(
            "threadName", currentThread.getName(),
            "threadId", currentThread.threadId(),
            "isVirtual", currentThread.isVirtual(),
            "isDaemon", currentThread.isDaemon(),
            "state", currentThread.getState().toString(),
            "priority", currentThread.getPriority()
        );
    }

    @GetMapping("/stress-test")
    public String stressTest(@RequestParam(defaultValue = "1000") int numberOfTasks) {
        long startTime = System.currentTimeMillis();
        
        // Executa muitas operações concorrentes para demonstrar a eficiência das Virtual Threads
        var futures = java.util.stream.IntStream.range(0, numberOfTasks)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(100); // Simula I/O
                        return "Task " + i + " completed";
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }))
                .toList();

        futures.forEach(CompletableFuture::join);
        
        long duration = System.currentTimeMillis() - startTime;
        
        return String.format("Stress test completed: %d tasks in %d ms using Virtual Threads", 
                numberOfTasks, duration);
    }

    private String getCurrentThreadInfo() {
        Thread currentThread = Thread.currentThread();
        return String.format("%s (Virtual: %s)", 
                currentThread.getName(), 
                currentThread.isVirtual());
    }
}

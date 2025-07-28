package edu.renata.fraga.virtual_threads_sample.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/api/webflux-virtual-threads")
@Tag(name = "WebFlux + Virtual Threads API", description = "APIs demonstrando a combinação de WebFlux com Virtual Threads (Recomendado)")
public class WebFluxVirtualThreadController {

    @Autowired
    @Qualifier("virtualThreadScheduler")
    private Scheduler virtualThreadScheduler;

    @Autowired
    @Qualifier("virtualThreadExecutor")
    private Executor virtualThreadExecutor;

    @Operation(
            summary = "Mono com Operação Bloqueante",
            description = "Demonstra como executar operações bloqueantes em Virtual Threads dentro de um Mono"
    )
    @ApiResponse(responseCode = "200", description = "Operação bloqueante executada com sucesso no contexto reativo",
            content = @Content(examples = @ExampleObject(value = """
                    "Blocking operation completed on Virtual Thread: VirtualThread[#45]/runnable@ForkJoinPool-1-worker-1 (Virtual: true)"
                    """)))
    @GetMapping("/mono-blocking")
    public Mono<String> monoWithBlockingOperation() {
        return Mono.fromCallable(() -> {
            // Simula operação bloqueante executada em Virtual Thread
            try {
                Thread.sleep(1000);
                return String.format("Blocking operation completed on Virtual Thread: %s (Virtual: %s)",
                        Thread.currentThread().getName(),
                        Thread.currentThread().isVirtual());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }).subscribeOn(virtualThreadScheduler);
    }

    @GetMapping("/flux-parallel")
    public Flux<String> fluxWithParallelOperations(@RequestParam(defaultValue = "10") int count) {
        return Flux.range(0, count)
                .flatMap(i -> Mono.fromCallable(() -> {
                    try {
                        Thread.sleep(500); // Simula I/O bloqueante
                        return String.format("Task %d completed on Virtual Thread: %s (Virtual: %s)",
                                i,
                                Thread.currentThread().getName(),
                                Thread.currentThread().isVirtual());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }).subscribeOn(virtualThreadScheduler))
                .doOnNext(result -> System.out.println("Processed: " + result));
    }

    @GetMapping("/compare-schedulers")
    public Mono<Map<String, Object>> compareSchedulers(@RequestParam(defaultValue = "5") int tasks) {
        long startTime = System.currentTimeMillis();

        // Testa com Virtual Thread Scheduler
        Mono<String> virtualThreadResult = Flux.range(0, tasks)
                .flatMap(i -> Mono.fromCallable(() -> {
                    try {
                        Thread.sleep(200);
                        return String.format("VT-Task-%d: %s (Virtual: %s)",
                                i,
                                Thread.currentThread().getName(),
                                Thread.currentThread().isVirtual());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }).subscribeOn(virtualThreadScheduler))
                .collectList()
                .map(results -> String.format("Virtual Threads completed %d tasks", results.size()));

        // Testa com Scheduler padrão
        Mono<String> defaultSchedulerResult = Flux.range(0, tasks)
                .flatMap(i -> Mono.fromCallable(() -> {
                    try {
                        Thread.sleep(200);
                        return String.format("Default-Task-%d: %s (Virtual: %s)",
                                i,
                                Thread.currentThread().getName(),
                                Thread.currentThread().isVirtual());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }))
                .collectList()
                .map(results -> String.format("Default Scheduler completed %d tasks", results.size()));

        return Mono.zip(virtualThreadResult, defaultSchedulerResult)
                .map(tuple -> {
                    long duration = System.currentTimeMillis() - startTime;
                    return Map.of(
                            "virtualThreadResult", tuple.getT1(),
                            "defaultSchedulerResult", tuple.getT2(),
                            "totalDurationMs", duration,
                            "currentThread", getCurrentThreadInfo()
                    );
                });
    }

    @GetMapping("/sequential-calls")
    public Mono<String> sequentialCalls() {
        return Mono.fromCallable(() -> {
            // Demonstra como Virtual Threads facilitam código sequencial
            String result1 = callExternalService("Service A", 300);
            String result2 = callExternalService("Service B", 400);
            String result3 = callExternalService("Service C", 200);
            
            return String.format("Sequential calls completed: %s -> %s -> %s on Virtual Thread: %s",
                    result1, result2, result3, Thread.currentThread().getName());
        }).subscribeOn(virtualThreadScheduler);
    }

    @GetMapping("/error-handling")
    public Mono<String> errorHandling() {
        return Mono.fromCallable(() -> {
            if (Math.random() > 0.5) {
                throw new RuntimeException("Simulated error in Virtual Thread");
            }
            return "Operation succeeded on Virtual Thread: " + Thread.currentThread().getName();
        })
        .subscribeOn(virtualThreadScheduler)
        .onErrorReturn("Error handled gracefully")
        .doOnError(error -> System.err.println("Error caught: " + error.getMessage()));
    }

    @GetMapping("/stress-test-reactive")
    public Mono<Map<String, Object>> stressTestReactive(@RequestParam(defaultValue = "1000") int numberOfTasks) {
        long startTime = System.currentTimeMillis();
        
        return Flux.range(0, numberOfTasks)
                .flatMap(i -> Mono.fromCallable(() -> {
                    try {
                        Thread.sleep(10); // Pequeno delay para simular I/O
                        return Thread.currentThread().isVirtual();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }).subscribeOn(virtualThreadScheduler))
                .collectList()
                .map(results -> {
                    long duration = System.currentTimeMillis() - startTime;
                    long virtualThreadCount = results.stream().filter(isVirtual -> isVirtual).count();
                    
                    return Map.of(
                            "totalTasks", numberOfTasks,
                            "virtualThreadsUsed", virtualThreadCount,
                            "durationMs", duration,
                            "tasksPerSecond", numberOfTasks * 1000.0 / duration
                    );
                });
    }

    @GetMapping("/thread-info")
    public Mono<Map<String, Object>> getThreadInfo() {
        return Mono.fromCallable(() -> {
            Thread currentThread = Thread.currentThread();
            Map<String, Object> info = new HashMap<>();
            info.put("threadName", currentThread.getName());
            info.put("threadId", currentThread.threadId());
            info.put("isVirtual", currentThread.isVirtual());
            info.put("isDaemon", currentThread.isDaemon());
            info.put("state", currentThread.getState().toString());
            info.put("priority", currentThread.getPriority());
            info.put("scheduler", "Virtual Thread Scheduler");
            return info;
        }).subscribeOn(virtualThreadScheduler);
    }

    private String callExternalService(String serviceName, int delayMs) {
        try {
            Thread.sleep(delayMs);
            return String.format("%s(%dms)", serviceName, delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private String getCurrentThreadInfo() {
        Thread currentThread = Thread.currentThread();
        return String.format("%s (Virtual: %s)",
                currentThread.getName(),
                currentThread.isVirtual());
    }
}

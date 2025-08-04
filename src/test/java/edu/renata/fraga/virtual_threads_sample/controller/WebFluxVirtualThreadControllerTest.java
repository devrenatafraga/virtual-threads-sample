package edu.renata.fraga.virtual_threads_sample.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WebFluxVirtualThreadControllerTest {

    @Mock
    private Scheduler virtualThreadScheduler;

    @Mock
    private Executor virtualThreadExecutor;

    @InjectMocks
    private WebFluxVirtualThreadController controller;

    @BeforeEach
    void setUp() {
        // Use o scheduler padrão para testes
        controller = new WebFluxVirtualThreadController();
        // Usando reflection para injetar um scheduler real para testes
        try {
            var schedulerField = WebFluxVirtualThreadController.class.getDeclaredField("virtualThreadScheduler");
            schedulerField.setAccessible(true);
            schedulerField.set(controller, Schedulers.boundedElastic());
            
            var executorField = WebFluxVirtualThreadController.class.getDeclaredField("virtualThreadExecutor");
            executorField.setAccessible(true);
            executorField.set(controller, (Executor) Runnable::run);
        } catch (Exception e) {
            // Fallback - continue with mock
        }
    }

    @Test
    void testMonoWithBlockingOperation() {
        // When
        Mono<String> result = controller.monoWithBlockingOperation();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    assertNotNull(response);
                    assertTrue(response.contains("Blocking operation completed"));
                    assertTrue(response.contains("Virtual:"));
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testFluxWithParallelOperations() {
        // When
        Flux<String> result = controller.fluxWithParallelOperations(3);

        // Then
        StepVerifier.create(result)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void testFluxWithParallelOperationsTimeout() {
        // When
        Flux<String> result = controller.fluxWithParallelOperations(2);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    assertNotNull(response);
                    assertTrue(response.contains("Task"));
                    return true;
                })
                .expectNextMatches(response -> {
                    assertNotNull(response);
                    assertTrue(response.contains("Task"));
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testCompareSchedulers() {
        // When
        Mono<Map<String, Object>> result = controller.compareSchedulers(5);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    assertNotNull(response);
                    assertTrue(response.containsKey("virtualThreadResult"));
                    assertTrue(response.containsKey("defaultSchedulerResult"));
                    assertTrue(response.containsKey("totalDurationMs"));
                    assertTrue(response.containsKey("currentThread"));
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testSequentialCalls() {
        // When
        Mono<String> result = controller.sequentialCalls();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    assertNotNull(response);
                    assertTrue(response.contains("Sequential calls completed"));
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testErrorHandling() {
        // When
        Mono<String> result = controller.errorHandling();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    assertNotNull(response);
                    // O método tem comportamento aleatório, pode ser sucesso ou error handling
                    return response.contains("Error handled gracefully") || 
                           response.contains("Operation succeeded on Virtual Thread");
                })
                .verifyComplete();
    }

    @Test
    void testStressTestReactive() {
        // When - usando um número pequeno para o teste
        Mono<Map<String, Object>> result = controller.stressTestReactive(10);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    assertNotNull(response);
                    assertTrue(response.containsKey("totalTasks"));
                    assertTrue(response.containsKey("virtualThreadsUsed"));
                    assertTrue(response.containsKey("durationMs"));
                    assertTrue(response.containsKey("tasksPerSecond"));
                    assertEquals(10, response.get("totalTasks"));
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testGetThreadInfo() {
        // When
        Mono<Map<String, Object>> result = controller.getThreadInfo();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    assertNotNull(response);
                    assertTrue(response.containsKey("threadName"));
                    assertTrue(response.containsKey("threadId"));
                    assertTrue(response.containsKey("isVirtual"));
                    assertTrue(response.containsKey("isDaemon"));
                    assertTrue(response.containsKey("priority"));
                    assertTrue(response.containsKey("state"));
                    assertTrue(response.containsKey("scheduler"));
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testMonoWithBlockingOperationPerformance() {
        // When
        long startTime = System.currentTimeMillis();
        Mono<String> result = controller.monoWithBlockingOperation();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    
                    // Verificar que levou pelo menos 1 segundo (devido ao sleep)
                    assertTrue(duration >= 1000, "Should take at least 1 second due to sleep");
                    assertTrue(response.contains("Blocking operation completed"));
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testFluxParallelOperationsCount() {
        // Given
        int taskCount = 5;

        // When
        Flux<String> result = controller.fluxWithParallelOperations(taskCount);

        // Then
        StepVerifier.create(result)
                .expectNextCount(taskCount)
                .verifyComplete();
    }

    @Test
    void testCompareSchedulersResponseStructure() {
        // When
        Mono<Map<String, Object>> result = controller.compareSchedulers(3);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    // Verificar estrutura da resposta
                    assertTrue(response.get("virtualThreadResult") instanceof String);
                    assertTrue(response.get("defaultSchedulerResult") instanceof String);
                    assertTrue(response.get("totalDurationMs") instanceof Long);
                    assertTrue(response.get("currentThread") instanceof String);
                    
                    String virtualResult = (String) response.get("virtualThreadResult");
                    assertTrue(virtualResult.contains("completed"));
                    
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testStressTestReactiveWithLargerLoad() {
        // When - teste com carga um pouco maior
        Mono<Map<String, Object>> result = controller.stressTestReactive(50);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    assertNotNull(response);
                    assertEquals(50, response.get("totalTasks"));
                    assertTrue(response.containsKey("virtualThreadsUsed"));
                    assertTrue(response.containsKey("durationMs"));
                    assertTrue(response.containsKey("tasksPerSecond"));
                    
                    return true;
                })
                .verifyComplete();
    }
}

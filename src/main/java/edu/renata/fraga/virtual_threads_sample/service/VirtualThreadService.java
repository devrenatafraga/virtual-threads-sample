package edu.renata.fraga.virtual_threads_sample.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

@Service
public class VirtualThreadService {

    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    private final ExecutorService platformThreadExecutor = Executors.newFixedThreadPool(200);

    public String processWithVirtualThreads(int numberOfTasks) {
        Instant start = Instant.now();
        
        var futures = IntStream.range(0, numberOfTasks)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                    return processTask(i);
                }, virtualThreadExecutor))
                .toList();

        futures.stream()
                .map(CompletableFuture::join)
                .toList();

        Duration duration = Duration.between(start, Instant.now());
        
        return String.format("Virtual Threads - Processed %d tasks in %d ms. Thread info: %s", 
                numberOfTasks, duration.toMillis(), getCurrentThreadInfo());
    }

    public String processWithPlatformThreads(int numberOfTasks) {
        Instant start = Instant.now();
        
        var futures = IntStream.range(0, numberOfTasks)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                    return processTask(i);
                }, platformThreadExecutor))
                .toList();

        futures.stream()
                .map(CompletableFuture::join)
                .toList();

        Duration duration = Duration.between(start, Instant.now());
        
        return String.format("Platform Threads - Processed %d tasks in %d ms. Thread info: %s", 
                numberOfTasks, duration.toMillis(), getCurrentThreadInfo());
    }

    public String simulateBlockingOperation() {
        Instant start = Instant.now();
        
        // Simula operação de I/O bloqueante
        try {
            Thread.sleep(1000); // 1 segundo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        
        Duration duration = Duration.between(start, Instant.now());
        
        return String.format("Blocking operation completed in %d ms. Thread: %s", 
                duration.toMillis(), getCurrentThreadInfo());
    }

    public String processMultipleBlockingOperations(int numberOfOperations) {
        Instant start = Instant.now();
        
        var futures = IntStream.range(0, numberOfOperations)
                .mapToObj(i -> CompletableFuture.supplyAsync(this::simulateBlockingOperation, virtualThreadExecutor))
                .toList();

        futures.stream()
                .map(CompletableFuture::join)
                .toList();

        Duration duration = Duration.between(start, Instant.now());
        
        return String.format("Completed %d blocking operations in %d ms using Virtual Threads", 
                numberOfOperations, duration.toMillis());
    }

    private String processTask(int taskId) {
        try {
            // Simula processamento com pequeno delay
            Thread.sleep(100);
            return String.format("Task %d processed by %s", taskId, getCurrentThreadInfo());
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

    // Método para demonstrar criação manual de Virtual Threads
    public String createVirtualThreadManually() {
        var result = new StringBuilder();
        
        // Criação usando Thread.ofVirtual()
        Thread virtualThread = Thread.ofVirtual()
                .name("manual-virtual-thread")
                .start(() -> {
                    result.append("Virtual Thread executando: ")
                          .append(Thread.currentThread().getName())
                          .append(" (Virtual: ")
                          .append(Thread.currentThread().isVirtual())
                          .append(")");
                });

        try {
            virtualThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        return result.toString();
    }

    // Método para demonstrar ThreadFactory de Virtual Threads
    public String demonstrateVirtualThreadFactory() {
        ThreadFactory factory = Thread.ofVirtual().factory();
        var result = new StringBuilder();
        
        Thread thread = factory.newThread(() -> {
            result.append("Thread criada via Factory: ")
                  .append(Thread.currentThread().getName())
                  .append(" (Virtual: ")
                  .append(Thread.currentThread().isVirtual())
                  .append(")");
        });
        
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        
        return result.toString();
    }
}

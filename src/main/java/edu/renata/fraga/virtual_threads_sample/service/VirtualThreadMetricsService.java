package edu.renata.fraga.virtual_threads_sample.service;

import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service responsible for collecting and providing metrics about Virtual Threads usage
 */
@Service
public class VirtualThreadMetricsService {

    private final ThreadMXBean threadMXBean;
    private final AtomicLong virtualThreadCreationCount = new AtomicLong(0);
    private final AtomicLong taskExecutionCount = new AtomicLong(0);

    public VirtualThreadMetricsService() {
        this.threadMXBean = ManagementFactory.getThreadMXBean();
    }

    /**
     * Collects comprehensive metrics about the current thread environment
     */
    public Map<String, Object> collectMetrics() {
        Runtime runtime = Runtime.getRuntime();
        
        return Map.of(
                "virtualThreads", getVirtualThreadMetrics(),
                "platformThreads", getPlatformThreadMetrics(),
                "memory", getMemoryMetrics(runtime),
                "system", getSystemMetrics(runtime),
                "statistics", getStatistics()
        );
    }

    /**
     * Gets metrics specific to Virtual Threads
     */
    public Map<String, Object> getVirtualThreadMetrics() {
        long currentVirtualThreads = getCurrentVirtualThreadCount();
        
        return Map.of(
                "currentCount", currentVirtualThreads,
                "totalCreated", virtualThreadCreationCount.get(),
                "isSupported", isVirtualThreadSupported(),
                "carrierThreads", getCarrierThreadCount()
        );
    }

    /**
     * Gets metrics specific to Platform Threads
     */
    public Map<String, Object> getPlatformThreadMetrics() {
        return Map.of(
                "currentCount", threadMXBean.getThreadCount(),
                "peakCount", threadMXBean.getPeakThreadCount(),
                "totalStarted", threadMXBean.getTotalStartedThreadCount(),
                "daemonCount", threadMXBean.getDaemonThreadCount()
        );
    }

    /**
     * Gets memory-related metrics
     */
    public Map<String, Object> getMemoryMetrics(Runtime runtime) {
        return Map.of(
                "totalMemory", runtime.totalMemory(),
                "freeMemory", runtime.freeMemory(),
                "maxMemory", runtime.maxMemory(),
                "usedMemory", runtime.totalMemory() - runtime.freeMemory(),
                "memoryUsagePercent", calculateMemoryUsagePercent(runtime)
        );
    }

    /**
     * Gets system-level metrics
     */
    public Map<String, Object> getSystemMetrics(Runtime runtime) {
        return Map.of(
                "availableProcessors", runtime.availableProcessors(),
                "javaVersion", System.getProperty("java.version"),
                "osName", System.getProperty("os.name"),
                "osArch", System.getProperty("os.arch")
        );
    }

    /**
     * Gets application-specific statistics
     */
    public Map<String, Object> getStatistics() {
        return Map.of(
                "taskExecutions", taskExecutionCount.get(),
                "avgTasksPerVirtualThread", calculateAvgTasksPerVirtualThread(),
                "uptime", ManagementFactory.getRuntimeMXBean().getUptime()
        );
    }

    /**
     * Increments the virtual thread creation counter
     */
    public void incrementVirtualThreadCreation() {
        virtualThreadCreationCount.incrementAndGet();
    }

    /**
     * Increments the task execution counter
     */
    public void incrementTaskExecution() {
        taskExecutionCount.incrementAndGet();
    }

    /**
     * Estimates the current number of virtual threads
     */
    private long getCurrentVirtualThreadCount() {
        // Since there's no direct API to count virtual threads,
        // we estimate based on total threads minus platform threads
        return Thread.getAllStackTraces().keySet().stream()
                .mapToLong(thread -> thread.isVirtual() ? 1 : 0)
                .sum();
    }

    /**
     * Checks if Virtual Threads are supported in the current JVM
     */
    private boolean isVirtualThreadSupported() {
        try {
            Thread.class.getMethod("isVirtual");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Estimates the number of carrier threads (platform threads used by virtual threads)
     */
    private int getCarrierThreadCount() {
        // Approximate carrier thread count based on available processors
        return Math.min(threadMXBean.getThreadCount(), Runtime.getRuntime().availableProcessors());
    }

    /**
     * Calculates memory usage percentage
     */
    private double calculateMemoryUsagePercent(Runtime runtime) {
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        return (double) usedMemory / runtime.maxMemory() * 100;
    }

    /**
     * Calculates average tasks per virtual thread
     */
    private double calculateAvgTasksPerVirtualThread() {
        long virtualThreadsCreated = virtualThreadCreationCount.get();
        if (virtualThreadsCreated == 0) {
            return 0.0;
        }
        return (double) taskExecutionCount.get() / virtualThreadsCreated;
    }
}

package edu.renata.fraga.virtual_threads_sample.controller;

import edu.renata.fraga.virtual_threads_sample.service.VirtualThreadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VirtualThreadControllerTest {

    @Mock
    private VirtualThreadService virtualThreadService;

    @InjectMocks
    private VirtualThreadController virtualThreadController;

    private MockMvc mockMvc;

    @Test
    void testCompareThreads() {
        // Given
        when(virtualThreadService.processWithVirtualThreads(anyInt()))
                .thenReturn("Virtual Threads: Processed 100 tasks in 1.2s");
        when(virtualThreadService.processWithPlatformThreads(anyInt()))
                .thenReturn("Platform Threads: Processed 100 tasks in 2.1s");

        // When
        Map<String, String> result = virtualThreadController.compareThreads(100);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("virtualThreads"));
        assertTrue(result.containsKey("platformThreads"));
        assertTrue(result.containsKey("currentThread"));
        assertEquals("Virtual Threads: Processed 100 tasks in 1.2s", result.get("virtualThreads"));
        assertEquals("Platform Threads: Processed 100 tasks in 2.1s", result.get("platformThreads"));
    }

    @Test
    void testCompareThreadsWithMockMvc() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.standaloneSetup(virtualThreadController).build();
        
        when(virtualThreadService.processWithVirtualThreads(anyInt()))
                .thenReturn("Virtual Threads: Processed 50 tasks in 0.8s");
        when(virtualThreadService.processWithPlatformThreads(anyInt()))
                .thenReturn("Platform Threads: Processed 50 tasks in 1.5s");

        // When & Then
        mockMvc.perform(get("/api/virtual-threads/compare")
                        .param("tasks", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.virtualThreads").exists())
                .andExpect(jsonPath("$.platformThreads").exists())
                .andExpect(jsonPath("$.currentThread").exists());
    }

    @Test
    void testBlockingOperation() {
        // Given
        when(virtualThreadService.simulateBlockingOperation())
                .thenReturn("Blocking operation completed in 2.1s");

        // When
        String result = virtualThreadController.blockingOperation();

        // Then
        assertNotNull(result);
        assertEquals("Blocking operation completed in 2.1s", result);
    }

    @Test
    void testBlockingOperationWithMockMvc() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.standaloneSetup(virtualThreadController).build();
        
        when(virtualThreadService.simulateBlockingOperation())
                .thenReturn("Blocking operation completed successfully");

        // When & Then
        mockMvc.perform(get("/api/virtual-threads/blocking"))
                .andExpect(status().isOk())
                .andExpect(content().string("Blocking operation completed successfully"));
    }

    @Test
    void testMultipleBlockingOperations() {
        // Given
        when(virtualThreadService.processMultipleBlockingOperations(anyInt()))
                .thenReturn("Completed 5 blocking operations in 3.2s");

        // When
        String result = virtualThreadController.multipleBlockingOperations(5);

        // Then
        assertNotNull(result);
        assertEquals("Completed 5 blocking operations in 3.2s", result);
    }

    @Test
    void testMultipleBlockingOperationsWithMockMvc() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.standaloneSetup(virtualThreadController).build();
        
        when(virtualThreadService.processMultipleBlockingOperations(anyInt()))
                .thenReturn("Completed 3 blocking operations successfully");

        // When & Then
        mockMvc.perform(get("/api/virtual-threads/multiple-blocking")
                        .param("count", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Completed 3 blocking operations successfully"));
    }

    @Test
    void testManualVirtualThreadCreation() {
        // Given
        when(virtualThreadService.createVirtualThreadManually())
                .thenReturn("Virtual thread created manually: Virtual=true");

        // When
        String result = virtualThreadController.manualVirtualThreadCreation();

        // Then
        assertNotNull(result);
        assertEquals("Virtual thread created manually: Virtual=true", result);
    }

    @Test
    void testFactoryVirtualThreadCreation() {
        // Given
        when(virtualThreadService.demonstrateVirtualThreadFactory())
                .thenReturn("Virtual thread via factory: Virtual=true");

        // When
        String result = virtualThreadController.factoryVirtualThreadCreation();

        // Then
        assertNotNull(result);
        assertEquals("Virtual thread via factory: Virtual=true", result);
    }

    @Test
    void testAsyncEndpoint() {
        // When
        CompletableFuture<String> result = virtualThreadController.asyncEndpoint();

        // Then
        assertNotNull(result);
        assertFalse(result.isDone()); // Should be asynchronous
    }

    @Test
    void testGetThreadInfo() {
        // When
        Map<String, Object> result = virtualThreadController.getThreadInfo();

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("threadName"));
        assertTrue(result.containsKey("threadId"));
        assertTrue(result.containsKey("isVirtual"));
        assertTrue(result.containsKey("isDaemon"));
        assertTrue(result.containsKey("state"));
        assertTrue(result.containsKey("priority"));
    }

    @Test
    void testStressTest() {
        // When
        String result = virtualThreadController.stressTest(1000);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("Stress test completed"));
        assertTrue(result.contains("1000 tasks"));
    }
}

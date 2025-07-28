package edu.renata.fraga.virtual_threads_sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class VirtualThreadConfig {

    @Bean
    Scheduler virtualThreadScheduler() {
        return Schedulers.fromExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }

    @Bean
    Executor virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}

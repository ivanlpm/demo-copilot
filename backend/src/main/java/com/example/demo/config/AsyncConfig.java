package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration class that enables asynchronous method execution and defines
 * a dedicated thread pool for background tasks.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "duckTaskExecutor")
    public Executor duckTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("duck-async-");
        executor.initialize();
        return executor;
    }
}

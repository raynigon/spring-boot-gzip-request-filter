package com.raynigon.ecs.logging.async;

import com.raynigon.ecs.logging.async.executor.MdcForkJoinPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@Configuration
public class MdcExecutorConfiguration {

    private final MdcForkJoinPool globalPool = new MdcForkJoinPool();

    @Bean
    public MdcForkJoinPool mdcForkJoinPool() {
        return globalPool;
    }

    @Bean
    public TaskScheduler mdcTaskScheduler() {
        ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler();
        return scheduler;
    }
}
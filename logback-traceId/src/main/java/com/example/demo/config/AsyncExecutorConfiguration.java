package com.example.demo.config;

import com.example.demo.component.MdcTaskDecorator;
import com.example.demo.component.ThreadPoolExecutorMdcWrapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Configuration
@EnableAsync
public class AsyncExecutorConfiguration {

    @Bean(name = "asyncExecutor1")
    public ExecutorService asyncExecutor1Service() {
        return new ThreadPoolExecutor(
                10,
                100,
                60L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(128),
                new ThreadFactoryBuilder().setNameFormat("async-executor-1-pool-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Bean(name ="asyncExecutor2")
    public ExecutorService asyncExecutor2Service() {
        return new ThreadPoolExecutorMdcWrapper(
                10,
                100,
                60,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(128),
                new ThreadFactoryBuilder().setNameFormat("async-executor-2-pool-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean(name ="taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(128);
        executor.setThreadNamePrefix("task-executor-pool-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.initialize();

        return executor;
    }
}

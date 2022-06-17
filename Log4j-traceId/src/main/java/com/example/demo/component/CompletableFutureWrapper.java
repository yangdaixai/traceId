package com.example.demo.component;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
public class CompletableFutureWrapper {

    @Autowired
    @Qualifier("asyncExecutor1")
    private ExecutorService asyncExecutorService;

    public interface Task<U> {
        U callback();
    }

    public <U> CompletableFuture<U> supplyAsync(Task<U> task) {

        Map<String, String> contextMap = MDC.getCopyOfContextMap();

        return CompletableFuture.supplyAsync(() -> {
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }

                return task.callback();
            } finally {
                MDC.clear();
            }
        }, asyncExecutorService);
    }
}

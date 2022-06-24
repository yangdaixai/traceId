package com.example.demo.component;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.*;

public class ThreadPoolExecutorMdcWrapper extends ThreadPoolExecutor {

    public ThreadPoolExecutorMdcWrapper(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                        BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                                        RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);

    }

    private <T> Callable<T> wrap(final Callable<T> callable) {
        Map<String, String> context = MDC.getCopyOfContextMap();

        return () -> {
            if (context != null) {
                MDC.setContextMap(context);
            }

            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    private Runnable wrap(final Runnable runnable) {
        Map<String, String> context = MDC.getCopyOfContextMap();

        return () -> {
            if (context != null) {
                MDC.setContextMap(context);
            }

            try {
                runnable.run();
            } finally {
                MDC.clear();

            }
        };
    }

    @Override
    public void execute(Runnable task) {
        super.execute(wrap(task));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return super.submit(wrap(task), result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(wrap(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(wrap(task));
    }
}

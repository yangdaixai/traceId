package com.example.demo.component;

import org.slf4j.MDC;
import java.util.Map;

/**
 * @author yangshengwei_zidie
 * @description
 * @date 2022-06-24-4:36 PM
 */
public abstract class AbstractTracerRunnable implements Runnable{
    private Map<String, String> context = MDC.getCopyOfContextMap();

    public abstract void doTracerRun();

    @Override
    public void run() {

        if (this.context != null) {
            MDC.setContextMap(this.context);
        }

        try {
            this.doTracerRun();
        } finally {
            if (this.context != null) {
                MDC.clear();
            }
        }
    }
}

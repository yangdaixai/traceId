package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@Slf4j
public class StudentService {


    @Autowired
    @Qualifier("asyncExecutor2")
    ExecutorService executorService;


    @Async("taskExecutor")
    public Future<Integer> getAge(Long id){
        log.info("getAge id:{}",id);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return AsyncResult.forValue(18);
    }

    public String getName(Long id){
        log.info("getName id:{}",id);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "加班写Bug";
    }

    public Byte getSex(Long id){
        log.info("========================");
        log.info("getSex id:{}",id);

        Map<String, String> context = MDC.getCopyOfContextMap();

        log.info("1级子线程:{}, context:{}",Thread.currentThread().getId(),context);

        executorService.execute(() -> {
            log.info("========================");
            Map<String, String> context2 = MDC.getCopyOfContextMap();

            log.info("2级子线程:{}, context:{}",Thread.currentThread().getId(),context2);
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return 1;
    }
}

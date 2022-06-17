package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
@Slf4j
public class StudentService {

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
        log.info("getSex id:{}",id);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return 1;
    }
}

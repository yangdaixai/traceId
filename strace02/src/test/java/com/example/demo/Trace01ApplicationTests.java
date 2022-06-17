package com.example.demo;

import com.example.demo.dto.UserDTO;
import com.example.demo.response.ResponseData;
import com.example.demo.service.StudentService;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
class Trace01ApplicationTests {

    @Test
    void contextLoads() {
    }

    private final static String TRACE_ID = "TRACE_ID";


    @Autowired
    StudentService studentService;

    @Autowired
    @Qualifier("asyncExecutor2")
    ExecutorService executorService;


    @Test
    public void detail() {
       // Preconditions.checkNotNull(id, "id is null");

        Long id = 1000L;

        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);

        Future<Integer> ageFuture = studentService.getAge(id);

        Future<Byte> sexFuture = executorService.submit(()-> studentService.getSex(id));

        userDTO.setUsername(studentService.getName(id));

        try {
            userDTO.setAge(ageFuture.get());
            userDTO.setSex(sexFuture.get());
        } catch (Exception e) {
            log.error("user service error:{}", e.getMessage(), e);
        }

    }

    @Test
    public void test111() throws InterruptedException {

        MDC.put(TRACE_ID, UUID.randomUUID().toString());

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10,
                10,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadFactoryBuilder()
                        .setNameFormat("Test-YSW-Thread-Pool-%d")
                        .build());

        executor.execute(  new Runnable(){
            @Override
            public void run() {
                Map<String, String> context = MDC.getCopyOfContextMap();
                if (context != null) {
                    MDC.setContextMap(context);
                }

                try {

                        log.info("Runnable:{}",Thread.currentThread());
                        try {
                            TimeUnit.SECONDS.sleep(1L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                } finally {
                    MDC.clear();
                }
            }
        });


            log.info("12345");
            log.info("main:{}",Thread.currentThread());
            TimeUnit.SECONDS.sleep(1L);
            Map<String, String> context = MDC.getCopyOfContextMap();

    }

}

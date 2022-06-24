package com.example.demo.controller;

import com.example.demo.component.AbstractTracerCallable;
import com.example.demo.component.AbstractTracerRunnable;
import com.example.demo.dto.UserDTO;
import com.example.demo.response.ResponseData;
import com.example.demo.service.StudentService;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/student")
@Slf4j
public class StudentController {

    @Autowired
    StudentService studentService;

    @Autowired
    @Qualifier("asyncExecutor2")
    ExecutorService executorService;

    private final static String TRACE_ID = "TRACE_ID";

    @GetMapping("/{id}")
    public ResponseData<UserDTO> detail(@PathVariable Long id) {
        Preconditions.checkNotNull(id, "id is null");

//        UserDTO userDTO = new UserDTO();
//        userDTO.setId(id);
//
//        Future<Integer> ageFuture = studentService.getAge(id);
//
//      executorService.execute(()-> studentService.getSex(id));


        //MDC.put(TRACE_ID, UUID.randomUUID().toString());
        Map<String, String> context = MDC.getCopyOfContextMap();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MDC.setContextMap(context);
                log.info("====new runable1 threadid:{}===",Thread.currentThread().getId());

             //   Map<String, String> context = MDC.getCopyOfContextMap();
              String trceId =  MDC.get(TRACE_ID);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //MDC.setContextMap(context);
                        MDC.put(TRACE_ID, trceId+"-runable2" );
                        log.info("====new runable2 threadid:{}====",Thread.currentThread().getId());
                    }
                }).start();
            }
        }).start();

        new Thread(new AbstractTracerRunnable() {
            @Override
            public void doTracerRun() {
                log.info("====new AbstractTracerRunnable threadid:{}====",Thread.currentThread().getId());
            }
        }).start();

        FutureTask futureTask = new FutureTask<String>(new AbstractTracerCallable<String>(){

            @Override
            public String doTracer() {
                log.info("====new AbstractTracerCallable threadid:{}====",Thread.currentThread().getId());
                return null;
            }
        });
        Thread thread = new Thread(futureTask);
        thread.start();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                5,
                5,
                1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadFactoryBuilder().setNameFormat("test-thread-pool-%d").build());

        threadPoolExecutor.execute(new AbstractTracerRunnable(){
            @Override
            public void doTracerRun() {
                log.info("====new thread-pool-AbstractTracerRunnable threadid:{}====",Thread.currentThread().getId());
            }
        });

//        userDTO.setUsername(studentService.getName(id));
//
//        try {
//            userDTO.setAge(ageFuture.get());
//            //userDTO.setSex(sexFuture.get());
//        } catch (Exception e) {
//            log.error("user service error:{}", e.getMessage(), e);
//        }
//
//        return ResponseData.success(userDTO);

        return null;
    }


    @GetMapping("/test/{id}")
    public ResponseData<UserDTO> detail1(@PathVariable Long id) {
        Preconditions.checkNotNull(id, "id is null");

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

        return ResponseData.success(userDTO);
    }

}

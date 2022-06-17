package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.response.ResponseData;
import com.example.demo.service.StudentService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/student")
@Slf4j
public class StudentController {

    @Autowired
    StudentService studentService;

    @Autowired
    @Qualifier("asyncExecutor2")
    ExecutorService executorService;



    @GetMapping("/{id}")
    public ResponseData<UserDTO> detail(@PathVariable Long id) {
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

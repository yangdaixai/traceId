package com.example.demo.controller;

import com.example.demo.component.CompletableFutureWrapper;
import com.example.demo.dto.UserDTO;
import com.example.demo.response.ResponseData;
import com.example.demo.service.UserService;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    CompletableFutureWrapper completableFutureWrapper;

    @GetMapping("/{id}")
    public ResponseData<UserDTO> detail(@PathVariable Long id) {
        Preconditions.checkNotNull(id, "id is null");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);

        CompletableFuture<Integer> ageFuture = completableFutureWrapper.supplyAsync(() -> userService.getAge(id));

        userDTO.setUsername(userService.getName(id));

        try {
            userDTO.setAge(ageFuture.get());
        } catch (Exception e) {
            log.error("user service error:{}", e.getMessage(), e);
        }

        return ResponseData.success(userDTO);
    }
}

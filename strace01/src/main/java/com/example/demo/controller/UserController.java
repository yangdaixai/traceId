package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.response.ResponseData;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @GetMapping("/{id}")
    public ResponseData<UserDTO> detail(@PathVariable Long id) {
        Preconditions.checkNotNull(id, "id is null");

        log.info("user id:{}", id);

        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setAge(20);
        userDTO.setUsername("加班写Bug");

        return ResponseData.success(userDTO);
    }
}

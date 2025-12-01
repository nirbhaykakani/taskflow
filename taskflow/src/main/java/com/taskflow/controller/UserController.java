package com.taskflow.controller;

import com.taskflow.dto.RegisterDTO;
import com.taskflow.dto.UserDTO;
import com.taskflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterDTO dto) {
        UserDTO saved = userService.registerUser(dto);
        return ResponseEntity.ok(saved);
    }
}

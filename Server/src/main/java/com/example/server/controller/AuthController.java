package com.example.server.controller;

import com.example.server.dto.request.LoginRequestDTO;
import com.example.server.dto.request.UserRequestDTO;
import com.example.server.dto.response.ApiResponse;
import com.example.server.dto.response.UserResponseDTO;
import com.example.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins ="http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@RequestBody UserRequestDTO request) {
        UserResponseDTO user = userService.registerUser(request);
        return new ResponseEntity<>(ApiResponse.success("User registered successfully", user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponseDTO>> login(@RequestBody LoginRequestDTO request) {
        UserResponseDTO user = userService.loginUser(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", user));
    }
}
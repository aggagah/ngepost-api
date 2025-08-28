package com.aggagah.ngepost.controller;

import com.aggagah.ngepost.dto.*;
import com.aggagah.ngepost.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
        UserResponse savedUser = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success("User registered successfully", savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(BaseResponse.success("Login successful", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getUserById(@PathVariable UUID id) {
        return userService.findById(id)
                .map(u -> ResponseEntity.ok(BaseResponse.success("User found", u)))
                .orElse(ResponseEntity.status(404).body(BaseResponse.error("User not found", null)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<BaseResponse> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(u -> ResponseEntity.ok(BaseResponse.success("User found", u)))
                .orElse(ResponseEntity.status(404).body(BaseResponse.error("User not found", null)));
    }
}

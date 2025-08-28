package com.aggagah.ngepost.service;

import com.aggagah.ngepost.dto.*;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserResponse registerUser(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    Optional<UserResponse> findById(UUID id);
    Optional<UserResponse> findByEmail(String email);
}

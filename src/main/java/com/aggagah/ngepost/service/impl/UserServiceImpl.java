package com.aggagah.ngepost.service.impl;

import com.aggagah.ngepost.dto.*;
import com.aggagah.ngepost.entity.User;
import com.aggagah.ngepost.exception.BadRequestException;
import com.aggagah.ngepost.repository.UserRepository;
import com.aggagah.ngepost.service.UserService;
import com.aggagah.ngepost.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${redis.prefix}")
    String REDIS_PREFIX;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        String redisKey = REDIS_PREFIX + user.getEmail();
        String existingTokenJson = redisTemplate.opsForValue().get(redisKey);

        try {
            if (existingTokenJson != null) {
                TokenInfo existingToken = objectMapper.readValue(existingTokenJson, TokenInfo.class);

                if (!jwtUtil.isTokenExpired(existingToken.getAccessToken())) {
                    log.info("Token valid: {}", redisKey);
                    return LoginResponse.builder()
                            .accessToken(existingToken.getAccessToken())
                            .refreshToken(existingToken.getRefreshToken())
                            .build();
                } else {
                    log.info("Token expired: {}", redisKey);
                }
            }

            String accessToken = jwtUtil.generateToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            UserResponse userResp = UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();

            TokenInfo tokenInfo = TokenInfo.builder()
                    .user(userResp)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            String tokenJson = objectMapper.writeValueAsString(tokenInfo);
            redisTemplate.opsForValue().set(redisKey, tokenJson, Duration.ofMillis(jwtUtil.getExpirationTime()));

            log.info("Token saved: {}", redisKey);

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (JsonProcessingException e) {
            log.error("Failed serialized token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize token info", e);
        }
    }


    @Override
    public Optional<UserResponse> findById(UUID id) {
        return userRepository.findById(id)
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .build());
    }

    @Override
    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(u -> UserResponse.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .build());
    }
}

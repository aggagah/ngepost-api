package com.aggagah.ngepost.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenInfo {
    private UserResponse user;
    private String accessToken;
    private String refreshToken;
}

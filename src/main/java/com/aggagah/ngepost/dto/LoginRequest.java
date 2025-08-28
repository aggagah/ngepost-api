package com.aggagah.ngepost.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @Schema(description = "user email", example = "example@test.com")
    @NotBlank(message = "email is required")
    @Email(message = "email format is invalid")

    private String email;
    @Schema(description = "password", example = "secretpassword")
    @NotBlank(message = "password is required")
    @Size(min = 5, message = "Password must be at least 5 characters")
    private String password;
}

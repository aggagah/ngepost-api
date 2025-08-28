package com.aggagah.ngepost.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostRequest {
    @Schema(description = "post content", example = "test post content")
    @NotBlank(message = "content is required")
    private String content;
}

package com.example.mooddy_Auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank (message = "email is required")
    private String email;

    @NotBlank (message = "message is required")
    private String password;
}

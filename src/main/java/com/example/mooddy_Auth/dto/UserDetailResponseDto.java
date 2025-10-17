package com.example.mooddy_Auth.dto;

import com.example.mooddy_Auth.entity.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponseDto {
    private Long id;
    private String username;
    private String email;
    private LocalDate birthDate;
    private AuthProvider provider;
    private boolean enabled;
}

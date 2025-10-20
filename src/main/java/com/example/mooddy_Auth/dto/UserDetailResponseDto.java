package com.example.mooddy_Auth.dto;

import com.example.mooddy_Auth.entity.AuthProvider;
import com.example.mooddy_Auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 화면 표시용
public class UserDetailResponseDto {
    private Long id;
    private String nickname;
    private String email;
    private LocalDate birthDate;
    private AuthProvider provider;
    private boolean enabled;

    public static UserDetailResponseDto fromEntity(User user) {
        return UserDetailResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .provider(user.getProvider())
                .enabled(user.isEnabled())
                .build();
    }
}

package com.example.mooddy_Auth.service;

import com.example.mooddy_Auth.dto.AuthResponse;
import com.example.mooddy_Auth.dto.SignupRequest;
import com.example.mooddy_Auth.entity.AuthProvider;
import com.example.mooddy_Auth.entity.User;
import com.example.mooddy_Auth.exception.UserAlreadyExistsException;
import com.example.mooddy_Auth.repository.UserRepository;
import com.example.mooddy_Auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse signup(SignupRequest signupRequest) {
        // 닉네임 중복 확인
        if(userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        // 이메일 중복 확인
        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .birthDate(signupRequest.getBirthDate())
                .provider(AuthProvider.Local)
                .build();

        // DB에 저장하고 저장된 객체 반환(DB ID 포함)
        user = userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .user(user)     //화면 표시용
                .build();
    }
}
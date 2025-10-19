package com.example.mooddy_Auth.service;

import com.example.mooddy_Auth.dto.AuthRequest;
import com.example.mooddy_Auth.dto.AuthResponse;
import com.example.mooddy_Auth.dto.SignupRequest;
import com.example.mooddy_Auth.dto.UserDetailResponseDto;
import com.example.mooddy_Auth.entity.AuthProvider;
import com.example.mooddy_Auth.entity.User;
import com.example.mooddy_Auth.exception.AuthenticationException;
import com.example.mooddy_Auth.exception.UserAlreadyExistsException;
import com.example.mooddy_Auth.repository.UserRepository;
import com.example.mooddy_Auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // 회원가입
    public AuthResponse signup(SignupRequest signupRequest) {
        // 닉네임 중복 확인
        if(userRepository.existsByNickname(signupRequest.getNickname())) {
            throw new UserAlreadyExistsException("nickname already exists");
        }
        // 이메일 중복 확인
        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .nickname(signupRequest.getNickname())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .birthDate(signupRequest.getBirthDate())
                .provider(AuthProvider.Local)
                .build();

        // DB에 저장하고 저장된 객체 반환(DB ID 포함)
        user = userRepository.save(user);

        // JWT 발급 (회원가입시 자동 로그인)
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // User -> UserDetailResponseDto 변환
        UserDetailResponseDto userDetailResponseDto = UserDetailResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .provider(user.getProvider())
                .enabled(user.isEnabled())
                .build();

        //AuthResponse 반환
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(userDetailResponseDto)     //화면 표시용
                .build();
    }

    // 로그인
    public AuthResponse login(AuthRequest authRequest) {
        try {
            // 이메일로만 로그인
            String email = authRequest.getEmail();

            // 인증 시도 및 user 조회 후 Authentication 객체 얻기
            Authentication authentication =authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, authRequest.getPassword())
            );

            // AuthenticationManager가 UserDetailsService를 통해 인증 후 반환한 User 객체 가져오기
            User user = (User) authentication.getPrincipal();

            // 로그인 성공시 토큰 발급
            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // User -> UserDetailResponseDto 변환
            UserDetailResponseDto userDetailResponseDto = UserDetailResponseDto.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .birthDate(user.getBirthDate())
                    .provider(user.getProvider())
                    .enabled(user.isEnabled())
                    .build();

            return AuthResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .user(userDetailResponseDto)
                    .build();
        } catch (AuthenticationException e) {
            throw new AuthenticationException("Invalid email or password");
        }
    }
}
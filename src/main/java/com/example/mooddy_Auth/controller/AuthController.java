package com.example.mooddy_Auth.controller;

import com.example.mooddy_Auth.dto.ApiResponse;
import com.example.mooddy_Auth.dto.AuthRequest;
import com.example.mooddy_Auth.dto.AuthResponse;
import com.example.mooddy_Auth.dto.SignupRequest;
import com.example.mooddy_Auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(
            @Valid @RequestBody SignupRequest signupRequest
    ) {
        return ResponseEntity.ok(authService.signup(signupRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest authRequest
            ) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        return ResponseEntity.ok(new ApiResponse(true, "로그아웃 성공"));
    }
}

package com.example.mooddy_Auth.controller;

import com.example.mooddy_Auth.dto.AuthResponse;
import com.example.mooddy_Auth.dto.SignupRequest;
import com.example.mooddy_Auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(
            @Valid @RequestBody SignupRequest signupRequest
            ) {
        return ResponseEntity.ok(authService.signup(signupRequest));
    }
}

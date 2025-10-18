package com.example.mooddy_Auth.config;

import com.example.mooddy_Auth.exception.AuthenticationException;
import com.example.mooddy_Auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // authenticationManager.authenticate() 호출 시 내부에서 이 Bean 사용
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            return userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new AuthenticationException("No user found with email" + email)
                    );
        };
    }



    // PasswordEncoder Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

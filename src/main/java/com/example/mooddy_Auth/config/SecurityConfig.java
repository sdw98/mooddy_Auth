package com.example.mooddy_Auth.config;

//import com.example.mooddy_Auth.security.JwtAuthenticationFilter;
import com.example.mooddy_Auth.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

//    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                //REST API라 CSRF는 끔
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions().disable()) // h2-console
                //url별 접근 권한 규칙 정함
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/h2-console/**").permitAll()     // 이 경로는 인증없이 접속 가능
                        .anyRequest().authenticated()       // 나머지는 로그인 필요
                )
                //JWT 토큰 방식이라 세션 사용 안함
                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )
                // 폼 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // 인증실패시 EntryPoint 연결
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .build();
    }
}


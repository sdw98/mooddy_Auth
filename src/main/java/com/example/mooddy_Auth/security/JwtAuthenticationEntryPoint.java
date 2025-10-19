package com.example.mooddy_Auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // SecurityConfig 에서 AuthenticationException 발생하면 여기로 들어옴
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json");    // 응답 타입 JSON
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 인증실패 -> 401 Unauthorized

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED); // 상태 코드
        body.put("error", "Unauthorized"); //에러 타입
        body.put("message", authException.getMessage()); // 인증 실패 메시지
        body.put("path", request.getRequestURI()); // 어느 URL에서 실패했는지
        body.put("timestamp", LocalDateTime.now().toString());

        // Map을 JSON으로 변환하여 HTTP 응답으로 출력
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);

    }
}

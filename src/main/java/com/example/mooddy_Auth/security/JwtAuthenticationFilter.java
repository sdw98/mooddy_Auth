package com.example.mooddy_Auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
//요청 한번당 한번만 실행되는 필터
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // DB 에서 사용자 정보조회

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); //Authorization 담긴 헤더값 가져오기
        final String jwt; // 실제 토큰만 빼서 저장하는 변수
        final String username; // 토큰에서 추출한 사용자 식별값

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 다음 필터로 진행 (FilterSecurityInterceptor)
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer "
        jwt = authHeader.substring(7);

        // jwt 비어있거나 "null", "undefined" 면 다음 필터 진행
        if (jwt.isEmpty() || jwt.equals("null") || jwt.equals("undefined")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            username = jwtService.extractUser(jwt);
        } catch (ExpiredJwtException e) {   //토큰 만료
            sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired");
            return;
        } catch (MalformedJwtException e) { // 토큰 형식 오류
            sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Malformed JWT token");
            return;
        } catch (SignatureException e) { //토큰 서명 오류
            sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature.");
            return;
        } catch (UnsupportedJwtException e) {// 지원하지 않는 JWT
            sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unsupported JWT token.");
            return;
        } catch (Exception e) { // 기타 오류
            sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT authentication failed");
            return;
        }

        // username 이 존재하고 아직 인증 안 된 요청인지 확인
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // DB에서 사용자 정보 조회
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 토큰 유효성 검증
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 인증용 토큰 생성
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, // 사용자 정보
                                null,
                                userDetails.getAuthorities() // 권한 정보
                        );
                // 추가정보 담는 메서드
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                //Security Context에 인증 정보 등록
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendResponse(HttpServletResponse response,int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("error", "Unauthorized");  // 에러 타입 (고정)
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString()); // 현재 시간

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}

package com.example.mooddy_Auth.security;

import com.example.mooddy_Auth.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    //application.yml에 Key 가져오기
    @Value("${jwt.secret}")
    private String secretKey;

    // access token 만료시간
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    //refresh token 만료시간
    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    // ------------------- 토큰 생성 -------------------
    // 회원가입/로그인 access token 생성
    public String generateToken(UserDetails userDetails) {
        // jwt 추가로 담을 정보
        Map<String, Object> extraClaims = new HashMap<>();

        //UserDetails가 실제 User 엔티티 타입일 경우, 추가 정보 추출
        if (userDetails instanceof User user) {
            extraClaims.put("id", user.getId());
            extraClaims.put("email", user.getEmail());
            extraClaims.put("username", user.getUsername());
        }

        //중간 메서드 호출
        return generateToken(extraClaims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            Long expiration
    ) {
        return Jwts.builder()
                .setClaims(extraClaims) // 클라이언트/추가 정보용
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) //토큰 발급시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) //토큰 만료 시간 설정
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keybytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keybytes);
    }

    // ------------------- 토큰 검증 -------------------
}

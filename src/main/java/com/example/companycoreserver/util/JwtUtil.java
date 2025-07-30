package com.example.companycoreserver.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // 256비트 이상의 안전한 키 사용
    private final String SECRET_KEY = "mySecretKeyForJWTTokenGenerationThatIsLongEnoughForHS256Algorithm";
    private final long JWT_EXPIRATION = 86400000; // 24시간

    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // JwtUtil 클래스에 디버그 로그 추가
    public String generateToken(String employeeCode) {
        System.out.println("=== JWT 토큰 생성 시작 ===");
        System.out.println("employeeCode: " + employeeCode);
//        System.out.println("secret: " + secret);
//        System.out.println("expiration: " + expiration);

        try {
            String token = Jwts.builder()
                    .setSubject(employeeCode)
                    .setIssuedAt(new Date())
//                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact();

            System.out.println("생성된 토큰: " + token);
            return token;
        } catch (Exception e) {
            System.out.println("토큰 생성 실패: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

//    public String generateToken(String employeeCode) {
//        try {
//            System.out.println("JWT 토큰 생성 시작 - employeeCode: " + employeeCode);
//
//            String token = Jwts.builder()
//                    .setSubject(employeeCode)
//                    .setIssuedAt(new Date())
//                    .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
//                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                    .compact();
//
//            System.out.println("JWT 토큰 생성 완료: " + token);
//            return token;
//
//        } catch (Exception e) {
//            System.err.println("JWT 토큰 생성 중 오류 발생: " + e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
//    }

    // 누락된 메서드 추가: 토큰에서 employeeCode 추출
    public String getEmployeeCodeFromToken(String token) {
        try {
            System.out.println("토큰에서 employeeCode 추출 시작");
            String employeeCode = getClaimFromToken(token, Claims::getSubject);
            System.out.println("추출된 employeeCode: " + employeeCode);
            return employeeCode;
        } catch (Exception e) {
            System.err.println("employeeCode 추출 중 오류: " + e.getMessage());
            return null;
        }
    }

    // 토큰에서 만료 시간 추출
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // 토큰에서 특정 claim 추출하는 범용 메서드
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 토큰에서 모든 claims 추출
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            System.err.println("토큰이 만료되었습니다: " + e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            System.err.println("지원되지 않는 JWT 토큰입니다: " + e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            System.err.println("잘못된 형식의 JWT 토큰입니다: " + e.getMessage());
            throw e;
        } catch (SignatureException e) {
            System.err.println("JWT 서명이 유효하지 않습니다: " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            System.err.println("JWT 토큰이 비어있습니다: " + e.getMessage());
            throw e;
        }
    }

    // 토큰 만료 여부 확인
    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            System.err.println("토큰 만료 확인 중 오류: " + e.getMessage());
            return true; // 오류 발생 시 만료된 것으로 처리
        }
    }

    public boolean validateToken(String token) {
        try {
            System.out.println("JWT 토큰 검증 시작");

            // 토큰 파싱 및 서명 검증
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            // 만료 시간 확인
            if (isTokenExpired(token)) {
                System.err.println("토큰이 만료되었습니다");
                return false;
            }

            System.out.println("JWT 토큰 검증 성공");
            return true;

        } catch (Exception e) {
            System.err.println("JWT 토큰 검증 실패: " + e.getMessage());
            return false;
        }
    }
}

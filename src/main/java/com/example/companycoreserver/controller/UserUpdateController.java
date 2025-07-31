package com.example.companycoreserver.controller;

import com.example.companycoreserver.dto.UserUpdateRequest;
import com.example.companycoreserver.dto.UserUpdateResponse;
import com.example.companycoreserver.service.UserUpdateService;
import com.example.companycoreserver.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserUpdateController {

    @Autowired
    private UserUpdateService userUpdateService;

    @Autowired
    private JwtUtil jwtUtil;

    @PutMapping("/update")
    public ResponseEntity<UserUpdateResponse> updateUser(
            @RequestBody UserUpdateRequest request,
            HttpServletRequest httpRequest) {

        try {
            // JWT에서 사용자 ID 추출
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.badRequest()
                        .body(new UserUpdateResponse(false, "인증 토큰이 없습니다."));
            }

            Long jwtUserId = jwtUtil.getUserIdFromToken(token);
            if (jwtUserId == null) {
                return ResponseEntity.badRequest()
                        .body(new UserUpdateResponse(false, "유효하지 않은 토큰입니다."));
            }

            // Request의 userId와 JWT의 userId 일치 확인 (보안)
            if (request.getUserId() != null && !request.getUserId().equals(jwtUserId)) {
                return ResponseEntity.badRequest()
                        .body(new UserUpdateResponse(false, "권한이 없습니다."));
            }

            // Request에 userId가 없으면 JWT에서 추출한 값으로 설정
            if (request.getUserId() == null) {
                request.setUserId(jwtUserId);
            }

            UserUpdateResponse response = userUpdateService.updateUser(request.getUserId(), request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new UserUpdateResponse(false, "서버 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/info")
    public ResponseEntity<UserUpdateResponse> getUserInfo(HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.badRequest()
                        .body(new UserUpdateResponse(false, "인증 토큰이 없습니다."));
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest()
                        .body(new UserUpdateResponse(false, "유효하지 않은 토큰입니다."));
            }

            UserUpdateResponse response = userUpdateService.getUserInfo(userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new UserUpdateResponse(false, "사용자 정보 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // 첫 로그인 상태 업데이트 엔드포인트 추가
    @PutMapping("/first-login")
    public ResponseEntity<UserUpdateResponse> updateFirstLoginStatus(HttpServletRequest httpRequest) {
        try {
            String token = extractTokenFromRequest(httpRequest);
            if (token == null) {
                return ResponseEntity.badRequest()
                        .body(new UserUpdateResponse(false, "인증 토큰이 없습니다."));
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest()
                        .body(new UserUpdateResponse(false, "유효하지 않은 토큰입니다."));
            }

            // 첫 로그인 상태 업데이트
            UserUpdateResponse response = userUpdateService.updateFirstLoginStatus(userId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new UserUpdateResponse(false, "첫 로그인 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

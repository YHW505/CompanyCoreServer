// UserController.java
package com.example.companycoreserver.controller;

import com.example.companycoreserver.dto.UserUpdateRequest;
import com.example.companycoreserver.dto.LoginResponse;
import com.example.companycoreserver.dto.UserUpdateResponse;
import com.example.companycoreserver.service.UserService;
import com.example.companycoreserver.service.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserUpdateService userUpdateService;  // 이걸 추가하고

    /**
     * 토큰 검증 메서드
     */
    private boolean isValidToken(String token) {
        return token != null && !token.trim().isEmpty();
    }

    /**
     * 첫 로그인 상태 업데이트 API
     */
    @PutMapping("/update-first-login")
    public ResponseEntity<UserUpdateResponse> updateFirstLoginStatus(
            @RequestHeader("Authorization") String token,
            @RequestBody UserUpdateRequest request) {

        try {
            // 토큰 검증
            String actualToken = token.replace("Bearer ", "");
            if (!isValidToken(actualToken)) {
                return ResponseEntity.status(401).body(
                        new UserUpdateResponse(false, "Unauthorized: Invalid token")
                );
            }

            // 입력값 검증
            if (request.getUserId() <= 0) {
                return ResponseEntity.badRequest().body(
                        new UserUpdateResponse(false, "Invalid user ID")
                );
            }

            // 첫 로그인 상태 업데이트
            UserUpdateResponse response = userUpdateService.updateFirstLoginStatus(request.getUserId());


            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else if (response.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.status(500).body(response);
            }

        } catch (Exception e) {
            UserUpdateResponse errorResponse = new UserUpdateResponse(
                    false,
                    "Internal server error: " + e.getMessage()
            );
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 사용자 정보 조회 API
     */
    @GetMapping("/info/{userId}")
    public ResponseEntity<?> getUserInfo(
            @RequestHeader("Authorization") String token,
            @PathVariable long userId) {  // int → long 변경

        try {
            // 토큰 검증
            String actualToken = token.replace("Bearer ", "");
            if (!isValidToken(actualToken)) {
                return ResponseEntity.status(401).body(
                        new UserUpdateResponse(false, "Unauthorized: Invalid token")
                );
            }

            // 입력값 검증
            if (userId <= 0) {
                return ResponseEntity.badRequest().body(
                        new UserUpdateResponse(false, "Invalid user ID")
                );
            }

            // 사용자 정보 조회
            LoginResponse userInfo = userService.getUserInfo(userId);

            if (userInfo != null) {
                return ResponseEntity.ok(userInfo);
            } else {
                return ResponseEntity.status(404).body(
                        new UserUpdateResponse(false, "User not found")
                );
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new UserUpdateResponse(false, "Internal server error: " + e.getMessage())
            );
        }
    }
}

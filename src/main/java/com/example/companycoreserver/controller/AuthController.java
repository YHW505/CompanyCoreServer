package com.example.companycoreserver.controller;

import com.example.companycoreserver.entity.Enum.Role;
import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.service.AuthService;
import com.example.companycoreserver.dto.LoginRequest;
import com.example.companycoreserver.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("=== 로그인 API 호출 ===");
        System.out.println("요청 직원코드: " + loginRequest.getEmployeeCode());

        try {
            // 1. 먼저 사용자 존재 여부 확인
            if (!authService.userExists(loginRequest.getEmployeeCode())) {
                System.err.println("❌ 존재하지 않는 직원코드: " + loginRequest.getEmployeeCode());
                LoginResponse response = new LoginResponse();
                return ResponseEntity.status(401).body(response);
            }

            // 2. AuthService로 로그인 처리 (토큰 생성까지)
            String token = authService.login(loginRequest.getEmployeeCode(), loginRequest.getPassword());

            if (token != null) {
                // 3. AuthService의 getUserByEmployeeCode로 사용자 정보 조회
                User user = authService.getUserByEmployeeCode(loginRequest.getEmployeeCode());

                if (user != null) {
                    // 4. 🎯 모든 필드를 응답에 설정
                    LoginResponse response = new LoginResponse();
                    response.setToken(token);
                    response.setEmployeeCode(user.getEmployeeCode());
                    response.setUsername(user.getUsername());
                    response.setRole(user.getRole() != null ? user.getRole() : Role.EMPLOYEE);
                    response.setUserId(user.getUserId());
                    response.setDepartmentId(user.getDepartmentId());

                    // 🔥 누락된 필드들 추가
                    response.setEmail(user.getEmail() != null ? user.getEmail() : "");
                    response.setPhone(user.getPhone() != null ? user.getPhone() : "");
                    response.setJoinDate(user.getJoinDate());
                    response.setPositionId(user.getPositionId());
                    response.setIsFirstLogin(user.getIsFirstLogin() != null ? user.getIsFirstLogin() : 1);
                    response.setIsActive(user.getIsActive() != null ? user.getIsActive() : 0);
                    response.setCreatedAt(user.getCreatedAt());

                    System.out.println("로그인 완료 - 사용자: " + user.getUsername());
                    System.out.println("역할: " + user.getRole());
                    System.out.println("첫 로그인: " + user.getIsFirstLogin());

                    return ResponseEntity.ok(response);
                } else {
                    System.err.println("사용자 정보 조회 실패");
                }
            } else {
                System.err.println("토큰 생성 실패 - 패스워드 불일치");
            }

            // 실패 응답
            LoginResponse response = new LoginResponse();
            return ResponseEntity.status(401).body(response);

        } catch (Exception e) {
            System.err.println("로그인 API 오류: " + e.getMessage());
            e.printStackTrace();

            LoginResponse response = new LoginResponse();
            return ResponseEntity.status(500).body(response);
        }
    }


    // 추가: 사용자 존재 확인 API (테스트용)
    @GetMapping("/check-user/{employeeCode}")
    public ResponseEntity<?> checkUser(@PathVariable String employeeCode) {
        try {
            boolean exists = authService.userExists(employeeCode);
            return ResponseEntity.ok().body("사용자 존재: " + exists);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("확인 중 오류 발생");
        }
    }


    @PostMapping("/test-password")
    public ResponseEntity<?> testPassword(@RequestBody Map<String, String> request) {
        try {
            String employeeCode = request.get("employeeCode");
            String inputPassword = request.get("password");

            User user = authService.getUserByEmployeeCode(employeeCode);
            if (user == null) {
                return ResponseEntity.ok("❌ 사용자 없음");
            }

            String dbPassword = user.getPassword();

            System.out.println("=== 패스워드 테스트 ===");
            System.out.println("입력 패스워드: [" + inputPassword + "]");
            System.out.println("DB 패스워드: [" + dbPassword + "]");

            // 직접 BCryptPasswordEncoder 생성해서 테스트
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean matches = encoder.matches(inputPassword, dbPassword);
            System.out.println("BCrypt 매칭 결과: " + matches);

            return ResponseEntity.ok(String.format(
                    "테스트 결과:\n" +
                            "- 입력: [%s]\n" +
                            "- DB: [%s]\n" +
                            "- 매칭: %s",
                    inputPassword, dbPassword.substring(0, 20) + "...", matches
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("❌ 테스트 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * 로그아웃 처리
     * @param request 로그아웃 요청 (토큰 포함)
     * @return 로그아웃 결과
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body("토큰이 필요합니다.");
            }
            
            authService.logout(token);
            return ResponseEntity.ok("로그아웃 성공");
            
        } catch (Exception e) {
            System.err.println("로그아웃 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("로그아웃 처리 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 토큰 유효성 검증
     * @param request 토큰 검증 요청
     * @return 토큰 유효성 결과
     */
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body("토큰이 필요합니다.");
            }
            
            boolean isValid = authService.validateToken(token);
            return ResponseEntity.ok(Map.of("valid", isValid));
            
        } catch (Exception e) {
            System.err.println("토큰 검증 중 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("토큰 검증 중 오류가 발생했습니다.");
        }
    }

}

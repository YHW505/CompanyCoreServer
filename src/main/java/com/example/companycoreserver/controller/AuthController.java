package com.example.companycoreserver.controller;

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
            // 1. 먼저 사용자 존재 여부 확인 (UserRepository.existsByEmployeeCode 사용)
            if (!authService.userExists(loginRequest.getEmployeeCode())) {
                System.err.println("❌ 존재하지 않는 직원코드: " + loginRequest.getEmployeeCode());
                LoginResponse response = new LoginResponse();
                response.setMessage("존재하지 않는 직원코드입니다");
                return ResponseEntity.status(401).body(response);
            }

            // 2. AuthService로 로그인 처리 (토큰 생성까지)
            String token = authService.login(loginRequest.getEmployeeCode(), loginRequest.getPassword());

            if (token != null) {
                // 3. AuthService의 getUserByEmployeeCode로 사용자 정보 조회
                User user = authService.getUserByEmployeeCode(loginRequest.getEmployeeCode());

                if (user != null) {
                    // 4. 성공 응답 생성 (User entity 필드에 맞춤)
                    LoginResponse response = new LoginResponse();
                    response.setToken(token);
                    response.setEmployeeCode(user.getEmployeeCode());
                    response.setUsername(user.getUsername());
                    response.setRole(user.getRole().toString());
                    response.setMessage("로그인 성공");
                    response.setFirstLogin(user.getIsFirstLogin());
                    response.setUserId(user.getUserId());
                    response.setDepartmentId(user.getDepartmentId());

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
            response.setMessage("로그인 실패 - 패스워드를 확인해주세요");
            return ResponseEntity.status(401).body(response);

        } catch (Exception e) {
            System.err.println("로그인 API 오류: " + e.getMessage());
            e.printStackTrace();

            LoginResponse response = new LoginResponse();
            response.setMessage("서버 오류가 발생했습니다");
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

}

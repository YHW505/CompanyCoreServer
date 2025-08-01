package com.example.companycoreserver.service;

import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.repository.UserRepository;
import com.example.companycoreserver.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(String employeeCode, String password) {
        try {
            System.out.println("=== 로그인 프로세스 시작 ===");
            System.out.println("직원코드: " + employeeCode);
            System.out.println("입력 패스워드: [" + password + "]");
            System.out.println("입력 패스워드 길이: " + password.length());

            // 1. 사용자 조회
            Optional<User> userOptional = userRepository.findByEmployeeCode(employeeCode);
            if (userOptional.isEmpty()) {
                System.err.println("사용자를 찾을 수 없습니다: " + employeeCode);
                return null;
            }

            User user = userOptional.get();
            System.out.println("사용자 조회 성공: " + user.getEmployeeCode());

            String dbPassword = user.getPassword();
            if (dbPassword == null) {
                System.err.println("DB에 패스워드가 저장되어 있지 않습니다");
                return null;
            }

            // 🔍 상세 패스워드 정보 출력
            System.out.println("=== 패스워드 상세 분석 ===");
            System.out.println("DB 패스워드 전체: [" + dbPassword + "]");
            System.out.println("DB 패스워드 길이: " + dbPassword.length());
            System.out.println("입력 패스워드 바이트: " + java.util.Arrays.toString(password.getBytes()));
            System.out.println("DB 패스워드 바이트 (앞 20개): " + java.util.Arrays.toString(java.util.Arrays.copyOf(dbPassword.getBytes(), Math.min(20, dbPassword.getBytes().length))));

            // 🔍 BCrypt 패턴 확인
            boolean isBCryptPattern = dbPassword.matches("^\\$2[abyxy]?\\$\\d+\\$[./A-Za-z0-9]{53}$");
            System.out.println("올바른 BCrypt 패턴인가: " + isBCryptPattern);

            // 🔍 여러 방법으로 패스워드 테스트
            System.out.println("=== 패스워드 매칭 테스트 ===");

            // 방법 1: 기존 passwordEncoder
            boolean match1 = passwordEncoder.matches(password, dbPassword);
            System.out.println("1. 기존 passwordEncoder 매칭: " + match1);

            // 방법 2: 새로운 BCryptPasswordEncoder 인스턴스
            BCryptPasswordEncoder newEncoder = new BCryptPasswordEncoder();
            boolean match2 = newEncoder.matches(password, dbPassword);
            System.out.println("2. 새로운 BCryptPasswordEncoder 매칭: " + match2);

            // 방법 3: 입력 패스워드를 다시 인코딩해서 비교해보기
            String testEncoded = passwordEncoder.encode(password);
            System.out.println("3. 입력 패스워드를 인코딩한 결과: " + testEncoded);
            boolean match3 = passwordEncoder.matches(password, testEncoded);
            System.out.println("4. 새로 인코딩한 것과 매칭: " + match3);

            // 방법 4: 공백 제거 후 테스트
            String trimmedPassword = password.trim();
            boolean match4 = passwordEncoder.matches(trimmedPassword, dbPassword);
            System.out.println("5. 공백 제거 후 매칭: " + match4);

            // 방법 5: DB 패스워드 공백 제거 후 테스트
            String trimmedDbPassword = dbPassword.trim();
            boolean match5 = passwordEncoder.matches(password, trimmedDbPassword);
            System.out.println("6. DB 패스워드 공백 제거 후 매칭: " + match5);

            // 2. 패스워드 검증
            boolean isPasswordValid = passwordEncoder.matches(password, dbPassword);
            System.out.println("최종 패스워드 검증 결과: " + (isPasswordValid ? "성공" : "실패"));

            if (!isPasswordValid) {
                System.err.println("패스워드가 일치하지 않습니다");
                return null;
            }

            // 3. JWT 토큰 생성
            String token = jwtUtil.generateToken(employeeCode, user.getUserId());
            System.out.println("JWT 토큰 생성 성공");

            return token;

        } catch (Exception e) {
            System.err.println("로그인 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    // UserRepository의 findByEmployeeCode를 사용한 사용자 조회 메서드
    public User getUserByEmployeeCode(String employeeCode) {
        try {
            Optional<User> userOptional = userRepository.findByEmployeeCode(employeeCode);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                System.out.println("사용자 정보 조회 성공: " + user.getUsername());
                return user;
            } else {
                System.err.println("사용자를 찾을 수 없습니다: " + employeeCode);
                return null;
            }
        } catch (Exception e) {
            System.err.println("사용자 조회 오류: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // UserRepository의 existsByEmployeeCode를 사용한 사용자 존재 확인
    public boolean userExists(String employeeCode) {
        try {
            boolean exists = userRepository.existsByEmployeeCode(employeeCode);
            System.out.println("사용자 존재 확인 - " + employeeCode + ": " + exists);
            return exists;
        } catch (Exception e) {
            System.err.println("사용자 존재 확인 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 특정 사용자 패스워드 수동 인코딩 (테스트용)
    public boolean encodePasswordForUser(String employeeCode, String plainPassword) {
        try {
            Optional<User> userOptional = userRepository.findByEmployeeCode(employeeCode);
            if (userOptional.isEmpty()) {
                System.err.println("❌ 사용자를 찾을 수 없습니다: " + employeeCode);
                return false;
            }

            User user = userOptional.get();
            String encodedPassword = passwordEncoder.encode(plainPassword);
            user.setPassword(encodedPassword);
            userRepository.save(user);  // JpaRepository의 save 메서드 사용

            System.out.println("✅ 패스워드 인코딩 완료 - " + employeeCode +
                    ": " + plainPassword + " → " + encodedPassword.substring(0, 20) + "...");
            return true;

        } catch (Exception e) {
            System.err.println("❌ 패스워드 인코딩 실패: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

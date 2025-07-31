package com.example.companycoreserver.service;

import com.example.companycoreserver.dto.UserInfo;
import com.example.companycoreserver.dto.UserUpdateRequest;
import com.example.companycoreserver.dto.UserUpdateResponse;
import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@Transactional
public class UserUpdateService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserUpdateResponse updateUser(Long userId, UserUpdateRequest request) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 기본 정보 업데이트
            if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
                user.setUsername(request.getUsername().trim());
            }

            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                // 이메일 중복 체크 (자신 제외)
                if (userRepository.existsByEmail(request.getEmail().trim()) &&
                        !request.getEmail().trim().equals(user.getEmail())) {
                    throw new RuntimeException("이미 사용 중인 이메일입니다.");
                }
                user.setEmail(request.getEmail().trim());
            }

            if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                user.setPhone(request.getPhone().trim());
            }

            if (request.getBirthDate() != null && !request.getBirthDate().trim().isEmpty()) {
                try {
                    LocalDate birthDate = LocalDate.parse(request.getBirthDate());
                    user.setBirthDate(birthDate);
                } catch (DateTimeParseException e) {
                    throw new RuntimeException("생년월일 형식이 올바르지 않습니다. (YYYY-MM-DD)");
                }
            }

            // 비밀번호 변경 (새 비밀번호가 있는 경우만)
            if (request.getNewPassword() != null && !request.getNewPassword().trim().isEmpty()) {
                // 현재 비밀번호 확인 (첫 로그인이 아닌 경우)
                if (!request.isFirstLogin()) {
                    if (request.getCurrentPassword() == null ||
                            !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
                    }
                }

                // 비밀번호 강도 검증
                if (request.getNewPassword().length() < 6) {
                    throw new RuntimeException("비밀번호는 최소 6자 이상이어야 합니다.");
                }

                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            }

            // 첫 로그인 상태 업데이트 (Integer로 처리)
            if (request.isFirstLogin()) {
                user.setIsFirstLogin(0); // 첫 로그인 완료로 변경 (1 -> 0)
            }

            // 저장
            User updatedUser = userRepository.save(user);

            // 응답 생성
            UserInfo userInfo = convertToUserInfo(updatedUser);

            return new UserUpdateResponse(true, "사용자 정보가 성공적으로 업데이트되었습니다.", userInfo);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("사용자 정보 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public UserUpdateResponse getUserInfo(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            UserInfo userInfo = convertToUserInfo(user);

            return new UserUpdateResponse(true, "사용자 정보 조회 성공", userInfo);

        } catch (Exception e) {
            throw new RuntimeException("사용자 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private UserInfo convertToUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setEmployeeCode(user.getEmployeeCode());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setBirthDate(user.getBirthDate() != null ? user.getBirthDate().toString() : null);
        userInfo.setRole(user.getRole() != null ? user.getRole().toString() : null);
        userInfo.setIsFirstLogin(user.getIsFirstLogin()); // Integer 그대로 설정
        userInfo.setIsActive(user.getIsActive()); // Integer 그대로 설정
        userInfo.setCreatedAt(user.getCreatedAt() != null ?
                user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);

        return userInfo;
    }

    // UserUpdateService에 추가
    public UserUpdateResponse updateFirstLoginStatus(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 첫 로그인 상태를 0으로 변경 (완료 상태)
            user.setIsFirstLogin(0);

            // 저장
            User updatedUser = userRepository.save(user);

            // 응답 생성
            UserInfo userInfo = convertToUserInfo(updatedUser);

            return new UserUpdateResponse(true, "첫 로그인 상태가 성공적으로 업데이트되었습니다.", userInfo);

        } catch (Exception e) {
            throw new RuntimeException("첫 로그인 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

}

package com.example.companycoreserver.service;

import com.example.companycoreserver.dto.LoginResponse;
import com.example.companycoreserver.dto.UserInfo;
import com.example.companycoreserver.dto.UserUpdateResponse;
import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    public void saveUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        // 사용자 저장 로직...
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
        userInfo.setDepartmentName(null); // 또는 ID를 String으로 변환
        userInfo.setPositionName(null); // 또는 ID를 String으로 변환
        userInfo.setIsFirstLogin(user.getIsFirstLogin());
        userInfo.setIsActive(user.getIsActive());
        userInfo.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);

        return userInfo;
    }


    // UserUpdateService의 updateFirstLoginStatus 메서드
    public UserUpdateResponse updateFirstLoginStatus(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 첫 로그인 상태를 0으로 변경 (완료 상태)
            user.setIsFirstLogin(0);

            // 저장
            User updatedUser = userRepository.save(user);


            // 응답 생성 (이 부분만 남기고 나머지는 삭제)
            UserInfo userInfo = convertToUserInfo(updatedUser);

            return new UserUpdateResponse(
                    true,
                    "첫 로그인 상태가 성공적으로 업데이트되었습니다.",
                    userInfo
            );

            // 아래 두 줄은 삭제
            // return new UserUpdateResponse(true, "First login status updated successfully", userId, firstLogin);
            // userResponse.setFirstLogin(isFirstLogin);

        } catch (Exception e) {
            throw new RuntimeException("첫 로그인 상태 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    /**
     * 사용자 정보 조회 - LoginResponse 반환
     */
    public LoginResponse getUserInfo(long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return null;
            }

            // User 엔티티를 LoginResponse로 변환
            LoginResponse response = new LoginResponse();
            response.setUserId(user.getUserId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());

            // Integer를 boolean으로 변환 (1: true, 0 또는 null: false)
            boolean isFirstLogin = user.getIsFirstLogin() != null && user.getIsFirstLogin() == 1;
            response.setIsFirstLogin(user.getIsFirstLogin()); // LoginResponse에 setFirstLogin이 있다고 가정

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
